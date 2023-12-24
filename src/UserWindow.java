import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.Connection;
import java.util.Objects;
public class UserWindow extends JFrame{
    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private String username;
    private JTable booksTable;
    public UserWindow(String jdbcUrl, String dbUsername, String dbPassword, String username){
        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.username = username;

        setTitle("Książkowość - panel czytelnika");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        JButton accountManagementButton = new JButton("Zarządzanie kontem");
        JButton reservationsManagementButton = new JButton("Zarządzanie rezerwacjami");
        JButton borrowBookButton = new JButton("Wypożycz");
        JButton reserveBookButton = new JButton("Zarezerwuj");
        JButton historyViewButton = new JButton("Historia");
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        accountManagementButton.addActionListener(e -> new AccountManagementWindow(jdbcUrl, dbUsername, dbPassword, username, this));

        reservationsManagementButton.addActionListener(e -> new ReservationsManagementWindow(jdbcUrl, dbUsername, dbPassword, username));

        borrowBookButton.addActionListener(e -> {
            borrowingBook();
        });

        reserveBookButton.addActionListener(e -> {
            reservingBook();
        });

        historyViewButton.addActionListener(e -> {
            new HistoryViewWindow(jdbcUrl, dbUsername, dbPassword, username);
        });

        buttonsPanel.add(accountManagementButton);
        buttonsPanel.add(reservationsManagementButton);
        buttonsPanel.add(borrowBookButton);
        buttonsPanel.add(reserveBookButton);
        buttonsPanel.add(historyViewButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        initializeTable();

        JScrollPane scrollPane = new JScrollPane(booksTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        setVisible(true);
    }

    private void initializeTable(){
        ArrayList<String[]> tableData = fetchDataFromDatabase();

        String[] columnNames = {"Tytuł książki", "Autor", "Rok wydania", "Dostępność"};

        DefaultTableModel model = new DefaultTableModel(tableData.toArray(new Object[0][0]), columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable = new JTable(model);
    }

    private ArrayList<String[]> fetchDataFromDatabase() {
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

    private int borrowingBook(){
        int selectedRow = booksTable.getSelectedRow();

        if(selectedRow != -1){

            if (hasActiveLoan(username)) {
                JOptionPane.showMessageDialog(this, "Nie możesz wypożyczyć więcej niż jednej książki naraz.", "Błąd", JOptionPane.ERROR_MESSAGE);
                return 0;
            }

            String bookTitle = (String) booksTable.getValueAt(selectedRow, 0);

            if(Objects.equals(getBookStatus(bookTitle), "dostępna")){
                try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)){
                    LocalDate today = LocalDate.now();
                    LocalDate nextMonth = today.plusMonths(1);
                    String insertQuery = "INSERT INTO Loans (UserID, BookID, LoanDate, DueDate, ReturnDate, Status) " +
                            "VALUES (?, ?, ?, ?, NULL, 'aktywne')";

                    try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                        insertStatement.setInt(1, getUserID(username));
                        insertStatement.setInt(2, getBookID(bookTitle));
                        insertStatement.setDate(3, java.sql.Date.valueOf(today));
                        insertStatement.setDate(4, java.sql.Date.valueOf(nextMonth));
                        insertStatement.executeUpdate();
                        String message = "Wypożyczono książkę: " + bookTitle;
                        JOptionPane.showMessageDialog(this, message, "Potwierdzenie wypożyczenia", JOptionPane.INFORMATION_MESSAGE);
                        System.out.println(username + " wypożyczył " + bookTitle);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Błąd podczas wypożyczania książki.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Błąd podczas łączenia z bazą danych.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            } else{
                JOptionPane.showMessageDialog(this, "Wybrana książka niedostępna - możesz ją zarezerwować.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Wybierz książkę, którą chcesz wypożyczyć.", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }

    private int reservingBook(){
        int selectedRow = booksTable.getSelectedRow();

        if(selectedRow != -1){
            String bookTitle = (String) booksTable.getValueAt(selectedRow, 0);

            if (isBookBorrowed(bookTitle)) {
                JOptionPane.showMessageDialog(this, "Nie możesz zarezerwować książki, którą masz już wypożyczoną.", "Błąd", JOptionPane.ERROR_MESSAGE);
                return 0;
            }

            int userReservationsCount = getUserReservationsCount(username);
            if (userReservationsCount >= 3) {
                JOptionPane.showMessageDialog(this, "Nie możesz zarezerwować więcej niż trzech książek.", "Błąd", JOptionPane.ERROR_MESSAGE);
                return 0;
            }

            if (isBookAlreadyReserved(bookTitle, username)) {
                JOptionPane.showMessageDialog(this, "Nie możesz zarezerwować więcej niż jednej tej samej książki.", "Błąd", JOptionPane.ERROR_MESSAGE);
                return 0;
            }

            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)){
                LocalDate today = LocalDate.now();
                String insertQuery = "INSERT INTO Reservations (UserID, BookID, ReservationDate, Status) " +
                        "VALUES (?, ?, ?, 'aktywna')";

                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setInt(1, getUserID(username));
                    insertStatement.setInt(2, getBookID(bookTitle));
                    insertStatement.setDate(3, java.sql.Date.valueOf(today));
                    insertStatement.executeUpdate();
                    String message = "Zarezerwowano książkę: " + bookTitle;
                    JOptionPane.showMessageDialog(this, message, "Potwierdzenie rezerwacji", JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Błąd podczas rezerwowania książki.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Błąd podczas łączenia z bazą danych.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Wybierz książkę, którą chcesz zarezerwować.", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
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

    private String getBookStatus(String bookTitle) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT BookAvailability FROM Books WHERE Title = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, bookTitle);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("BookAvailability");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "błąd";
    }

    public void setUsername(String username){
        this.username = username;
    }

    private boolean isBookBorrowed(String bookTitle) {
        int userID = getUserID(username);
        int bookID = getBookID(bookTitle);

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT * FROM Loans WHERE UserID = ? AND BookID = ? AND Status = 'aktywne'";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userID);
                statement.setInt(2, bookID);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int getUserReservationsCount(String username) {
        int userID = getUserID(username);
        if (userID != -1) {
            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
                String query = "SELECT COUNT(*) AS ReservationCount FROM Reservations WHERE UserID = ? AND Status = 'aktywna'";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, userID);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            return resultSet.getInt("ReservationCount");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private boolean isBookAlreadyReserved(String bookTitle, String username) {
        int userID = getUserID(username);
        int bookID = getBookID(bookTitle);

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT * FROM Reservations WHERE UserID = ? AND BookID = ? AND Status = 'aktywna'";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userID);
                statement.setInt(2, bookID);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean hasActiveLoan(String username) {
        int userID = getUserID(username);
        if (userID != -1) {
            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
                String query = "SELECT * FROM Loans WHERE UserID = ? AND Status = 'aktywne'";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, userID);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        return resultSet.next();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}