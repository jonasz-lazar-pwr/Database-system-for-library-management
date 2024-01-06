import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class ReservationsManagementWindow extends JFrame {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final String username;

    private final int mainR;
    private final int mainG;
    private final int mainB;

    private JPanel panel;
    private JTable reservationsTable;

    public ReservationsManagementWindow(String jdbcUrl, String dbUsername, String dbPassword, String username, int mainR, int mainG, int mainB) {

        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.username = username;

        this.mainR = mainR;
        this.mainG = mainG;
        this.mainB = mainB;

        initComponents();

        // Ustawienia okna
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("System obsługi biblioteki - zarządzanie rezerwacjami");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.DARK_GRAY);
        setVisible(true);
        requestFocusInWindow();

        add(panel);
    }

    private void initComponents() {

        panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setOpaque(false);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.setBackground(Color.LIGHT_GRAY);
        buttonsPanel.setOpaque(false);

        MyButton cancelReservationButton = new MyButton("Anuluj rezerwację", mainR, mainG, mainB);
        cancelReservationButton.setPreferredSize(new Dimension(150, 45));

        cancelReservationButton.addActionListener(e -> {
            cancelReservation();
            refreshTable();
        });

        MyButton menuReturnButton = new MyButton("Powrót", mainR, mainG, mainB);
        menuReturnButton.setPreferredSize(new Dimension(150, 45));

        menuReturnButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new UserWindow(jdbcUrl, dbUsername, dbPassword, username, mainR, mainG, mainB));
            dispose();
        });

        buttonsPanel.add(menuReturnButton);
        buttonsPanel.add(cancelReservationButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        initializeTable();

        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        scrollPane.setForeground(Color.LIGHT_GRAY);
        scrollPane.setBackground(Color.LIGHT_GRAY);
        scrollPane.setBorder(new LineBorder(Color.DARK_GRAY));
        scrollPane.getViewport().setBackground(Color.DARK_GRAY);

        panel.add(scrollPane, BorderLayout.CENTER);
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
        reservationsTable.setFont(new Font("Roboto", Font.PLAIN, 15));
        reservationsTable.setRowHeight(20);
        reservationsTable.setForeground(Color.DARK_GRAY);
        reservationsTable.setBackground(Color.LIGHT_GRAY);
        reservationsTable.setBorder(new LineBorder(Color.LIGHT_GRAY));

        reservationsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        reservationsTable.getColumnModel().getColumn(0).setMinWidth(200);
        reservationsTable.getColumnModel().getColumn(1).setMinWidth(150);
        reservationsTable.getColumnModel().getColumn(2).setMinWidth(150);

        reservationsTable.getTableHeader().setReorderingAllowed(false);
        reservationsTable.getTableHeader().setFont(new Font("Roboto", Font.PLAIN, 15));
        reservationsTable.getTableHeader().setForeground(Color.DARK_GRAY);
        reservationsTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        reservationsTable.getTableHeader().setBorder(new LineBorder(Color.GRAY));
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
