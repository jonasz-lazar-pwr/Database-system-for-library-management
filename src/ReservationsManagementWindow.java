import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class ReservationsManagementWindow extends JFrame {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private String username;
    private JTable reservationsTable;

    public ReservationsManagementWindow(String jdbcUrl, String dbUsername, String dbPassword, String username) {
        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.username = username;

        setTitle("Zarządzanie rezerwacjami");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        JButton cancelReservationButton = new JButton("Anuluj rezerwację");
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cancelReservationButton.addActionListener(e -> {
            cancelReservation();
            refreshTable();
        });

        buttonsPanel.add(cancelReservationButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        initializeTable();

        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        setVisible(true);
    }

    private void initializeTable() {
        ArrayList<String[]> tableData = fetchReservationsFromDatabase();

        String[] columnNames = {"Tytuł książki", "Data rezerwacji", "Status"};

        DefaultTableModel model = new DefaultTableModel(tableData.toArray(new Object[0][0]), columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationsTable = new JTable(model);

        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private ArrayList<String[]> fetchReservationsFromDatabase() {
        ArrayList<String[]> data = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT title, reservation_date, status FROM UserReservationsView WHERE Login = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {

                    while (resultSet.next()) {
                        String bookTitle = resultSet.getString("title");
                        Date reservationDate = resultSet.getDate("reservation_date");
                        String status = resultSet.getString("status");
                        data.add(new String[]{bookTitle, reservationDate.toString(), status});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    private void cancelReservation() {
        int selectedRow = reservationsTable.getSelectedRow();

        if (selectedRow != -1) {
            String bookTitle = (String) reservationsTable.getValueAt(selectedRow, 0);

            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
                String updateQuery = "UPDATE Reservations SET Status = 'anulowana' WHERE UserID = ? AND BookID = ? AND Status = 'aktywna'";

                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setInt(1, getUserID(username));
                    updateStatement.setInt(2, getBookID(bookTitle));
                    int rowsAffected = updateStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        String message = "Anulowano rezerwację książki: " + bookTitle;
                        JOptionPane.showMessageDialog(this, message, "Potwierdzenie anulowania rezerwacji", JOptionPane.INFORMATION_MESSAGE);
                        System.out.println(username + " anulował rezerwację " + bookTitle);
                    } else {
                        JOptionPane.showMessageDialog(this, "Nie udało się anulować rezerwacji.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Błąd podczas łączenia z bazą danych.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Wybierz rezerwację, którą chcesz anulować.", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getUserID(String username) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT UserID FROM Users WHERE Login = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("UserID");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int getBookID(String bookTitle) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT BookID FROM Books WHERE Title = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, bookTitle);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("BookID");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) reservationsTable.getModel();
        model.setRowCount(0);
        ArrayList<String[]> newData = fetchReservationsFromDatabase();
        newData.forEach(model::addRow);
    }
}
