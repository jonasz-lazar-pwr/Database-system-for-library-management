import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class AdminWindow extends JFrame {
    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private String username;
    private JTable booksTable;
    private JTable loansTable;

    public AdminWindow(String jdbcUrl, String dbUsername, String dbPassword, String username) {
        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.username = username;

        setTitle("Książkowość - panel administratora");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        JButton accountManagementButton = new JButton("Zarządzanie kontem");
        JButton viewBooksButton = new JButton("Przeglądaj książki");
        JButton returnBookButton = new JButton("Zwróć książkę");
        JButton addBookButton = new JButton("Dodaj książkę");
        JButton removeBookButton = new JButton("Usuń książkę");
        JButton reportButton = new JButton("Raporty");
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        //accountManagementButton.addActionListener(e -> new AccountManagementWindow(jdbcUrl, dbUsername, dbPassword, this.username, null, this));

        viewBooksButton.addActionListener(e -> {
            initializeBooksTable();
            JOptionPane.showMessageDialog(this, new JScrollPane(booksTable), "Przeglądaj książki", JOptionPane.PLAIN_MESSAGE);
        });

        returnBookButton.addActionListener(e -> {
            returnBook();
        });

        addBookButton.addActionListener(e -> {
            new AddBookWindow(jdbcUrl,dbUsername,dbPassword);
        });

        removeBookButton.addActionListener(e -> {
            new RemoveBookWindow(jdbcUrl, dbUsername, dbPassword);
        });

        reportButton.addActionListener(e -> {
            new ReportWindow(jdbcUrl, dbUsername, dbPassword);
        });

        buttonsPanel.add(accountManagementButton);
        buttonsPanel.add(viewBooksButton);
        buttonsPanel.add(returnBookButton);
        buttonsPanel.add(addBookButton);
        buttonsPanel.add(removeBookButton);
        buttonsPanel.add(reportButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        initializeLoansTable();

        JScrollPane scrollPaneLoans = new JScrollPane(loansTable);
        panel.add(scrollPaneLoans, BorderLayout.CENTER);

        add(panel);
        setVisible(true);
    }

    private void initializeBooksTable() {
        ArrayList<String[]> tableData = fetchDataFromDatabaseBooks();

        String[] columnNames = {"Tytuł książki", "Autor", "Rok wydania", "Dostępność"};

        DefaultTableModel model = new DefaultTableModel(tableData.toArray(new Object[0][0]), columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable = new JTable(model);
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
    }

    private ArrayList<String[]> fetchDataFromDatabaseBooks() {
        ArrayList<String[]> data = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT BookTitle, AuthorFullName, PublicationYear, BookAvailability FROM BookView";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String bookTitle = resultSet.getString("BookTitle");
                    String authorFullName = resultSet.getString("AuthorFullName");
                    String publicationYear = resultSet.getString("PublicationYear");
                    String availability = resultSet.getString("BookAvailability");
                    data.add(new String[]{bookTitle, authorFullName, publicationYear, availability});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
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
            String loanID = (String) loansTable.getValueAt(selectedRow, 0);

            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
                String updateLoanQuery = "UPDATE Loans SET ReturnDate = CURRENT_DATE, Status = 'zrealizowane' WHERE LoanID = ?";
                String updateBookQuery = "UPDATE Books SET BookAvailability = 'dostępna' WHERE BookID = (SELECT BookID FROM Loans WHERE LoanID = ?)";

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


    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }
}
