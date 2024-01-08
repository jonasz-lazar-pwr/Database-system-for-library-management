import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class RentalsManagementWindow extends JFrame {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final String username;

    private JPanel panel;
    private JTable loansTable;
    private boolean isDataFiltered;

    public RentalsManagementWindow(String jdbcUrl, String dbUsername, String dbPassword, String username) {

        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.username = username;

        this.isDataFiltered = false;

        initComponents();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("System obsługi biblioteki - zarządzanie wypożyczeniami");
        setSize(700, 500);
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

        MyButton returnBookButton = new MyButton("Zwróć książkę");
        returnBookButton.setPreferredSize(new Dimension(150, 45));

        returnBookButton.addActionListener(e -> {
            returnBook();
        });

        MyButton menuReturnButton = new MyButton("Powrót");
        menuReturnButton.setPreferredSize(new Dimension(150, 45));

        menuReturnButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new AdminWindow(jdbcUrl, dbUsername, dbPassword, username));
            dispose();
        });

        MyButton selectUserButton = new MyButton("Filtruj");
        selectUserButton.setPreferredSize(new Dimension(150, 45));

        selectUserButton.addActionListener(e -> {
            if(!isDataFiltered){
                String selectedUsername = JOptionPane.showInputDialog(this, "Podaj login użytkownika:", "Wybierz użytkownika", JOptionPane.PLAIN_MESSAGE);
                if (selectedUsername != null && !selectedUsername.isEmpty()) {
                    filterLoansByUser(selectedUsername);
                }
                isDataFiltered = true;
                selectUserButton.setText("Usuń filtr");
            } else{
                updateLoansTable();
                isDataFiltered = false;
                selectUserButton.setText("Filtruj");
            }

        });

        buttonsPanel.add(menuReturnButton);
        buttonsPanel.add(returnBookButton);
        buttonsPanel.add(selectUserButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        initializeLoansTable();

        JScrollPane scrollPane = new JScrollPane(loansTable);
        scrollPane.setForeground(Color.LIGHT_GRAY);
        scrollPane.setBackground(Color.LIGHT_GRAY);
        scrollPane.setBorder(new LineBorder(Color.DARK_GRAY));
        scrollPane.getViewport().setBackground(Color.DARK_GRAY);

        panel.add(scrollPane, BorderLayout.CENTER);

    }

    private void initializeLoansTable() {
        ArrayList<String[]> tableData = fetchDataFromDatabaseLoans();

        String[] columnNames = {"Id wypożyczenia", "Czytelnik", "Tytuł książki", "Data wypożyczenia", "Status"};

        DefaultTableModel model = new DefaultTableModel(tableData.toArray(new Object[0][0]), columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        loansTable = new JTable(model);
        loansTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loansTable.setFont(new Font("Roboto", Font.PLAIN, 15));
        loansTable.setRowHeight(20);
        loansTable.setForeground(Color.DARK_GRAY);
        loansTable.setBackground(Color.LIGHT_GRAY);
        loansTable.setBorder(new LineBorder(Color.LIGHT_GRAY));

        loansTable.getTableHeader().setReorderingAllowed(false);
        loansTable.getTableHeader().setFont(new Font("Roboto", Font.PLAIN, 15));
        loansTable.getTableHeader().setForeground(Color.DARK_GRAY);
        loansTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        loansTable.getTableHeader().setBorder(new LineBorder(Color.GRAY));
    }

    private ArrayList<String[]> fetchDataFromDatabaseLoans(){
        ArrayList<String[]> data = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT loan_id, login, title, loan_date, status FROM UserLoansView";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()){
                    String loanId = resultSet.getString("loan_id");
                    String username = resultSet.getString("login");
                    String bookTitle = resultSet.getString("title");
                    String loanDate = resultSet.getString("loan_date");
                    String status = resultSet.getString("status");
                    data.add(new String[]{loanId, username, bookTitle, loanDate, status});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    private int returnBook() {
        int selectedRow = loansTable.getSelectedRow();

        if (selectedRow != -1) {
            String status = (String) loansTable.getValueAt(selectedRow, 4);

            if (!status.equals("zrealizowane")) {
                String loanID = (String) loansTable.getValueAt(selectedRow, 0);

                try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
                    String updateLoanQuery = "UPDATE Loans SET ReturnDate = CURRENT_DATE, Status = 'zrealizowane' WHERE LoanID = ?";
                    String updateBookQuery = "UPDATE Books SET BookAvailability = 'dostępna', Amount = Amount + 1 WHERE BookID = (SELECT BookID FROM Loans WHERE LoanID = ?)";

                    try (PreparedStatement updateLoanStatement = connection.prepareStatement(updateLoanQuery);
                         PreparedStatement updateBookStatement = connection.prepareStatement(updateBookQuery)) {

                        connection.setAutoCommit(false);

                        updateLoanStatement.setString(1, loanID);
                        updateLoanStatement.executeUpdate();

                        updateBookStatement.setString(1, loanID);
                        updateBookStatement.executeUpdate();

                        connection.commit();

                        String message = "Zwrócono książkę o ID: " + loanID;
                        JOptionPane.showMessageDialog(this, message, "Potwierdzenie zwrotu", JOptionPane.INFORMATION_MESSAGE);

                        updateLoansTable();
                        isDataFiltered = false;
                        // todo selectedUserButton.setText("Filtruj");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        connection.rollback();
                        JOptionPane.showMessageDialog(this, "Błąd podczas zwracania książki.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        connection.setAutoCommit(true);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Błąd podczas łączenia z bazą danych.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Książka została już zwrócona!.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Wybierz wypożyczenie, które chcesz zwrócić.", "Błąd", JOptionPane.ERROR_MESSAGE);
        }

        return 0;
    }


    private void updateLoansTable() {
        ArrayList<String[]> tableData = fetchDataFromDatabaseLoans();

        DefaultTableModel model = (DefaultTableModel) loansTable.getModel();
        model.setRowCount(0);

        for (String[] rowData : tableData) {
            model.addRow(rowData);
        }
    }

    private void updateLoansTable(ArrayList<String[]> tableData) {
        DefaultTableModel model = (DefaultTableModel) loansTable.getModel();
        model.setRowCount(0);

        for (String[] rowData : tableData) {
            model.addRow(rowData);
        }
    }

    private void filterLoansByUser(String selectedUsername) {
        ArrayList<String[]> filteredData = fetchDataForUserFromDatabaseLoans(selectedUsername);
        updateLoansTable(filteredData);
    }

    private ArrayList<String[]> fetchDataForUserFromDatabaseLoans(String selectedUsername) {
        ArrayList<String[]> data = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT loan_id, login, title, loan_date, status FROM UserLoansView WHERE login = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, selectedUsername);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String loanId = resultSet.getString("loan_id");
                        String username = resultSet.getString("login");
                        String bookTitle = resultSet.getString("title");
                        String loanDate = resultSet.getString("loan_date");
                        String status = resultSet.getString("status");
                        data.add(new String[]{loanId, username, bookTitle, loanDate, status});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }
}
