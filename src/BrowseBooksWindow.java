import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class BrowseBooksWindow extends JFrame {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final String username;

    JTable booksTable;
    JPanel panel;
    JPanel topButtonsPanel;
    JPanel bottomButtonsPanel;

    public BrowseBooksWindow(String jdbcUrl, String dbUsername, String dbPassword, String username) {

        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.username = username;

        initComponents();

        // Ustawienia okna
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("System obsługi biblioteki - przeglądanie książek");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.DARK_GRAY);
        setVisible(true);
        requestFocusInWindow();

        add(panel);
        setVisible(true);
    }

    private void initComponents() {

        panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setOpaque(false);

        topButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topButtonsPanel.setBackground(Color.LIGHT_GRAY);
        topButtonsPanel.setOpaque(false);

        bottomButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomButtonsPanel.setBackground(Color.LIGHT_GRAY);
        bottomButtonsPanel.setOpaque(false);

        MyButton cancelButton = new MyButton("Powrót");
        cancelButton.setPreferredSize(new Dimension(150, 45));

        MyButton borrowBookButton = new MyButton("Wypożycz");
        borrowBookButton.setPreferredSize(new Dimension(130, 45));
        borrowBookButton.setFont(new Font("Roboto", Font.PLAIN, 16));
        borrowBookButton.setForeground(Color.DARK_GRAY);
        borrowBookButton.setBackground(Color.LIGHT_GRAY);
        borrowBookButton.setBorder(new LineBorder(Color.GRAY));

        MyButton reserveBookButton = new MyButton("Zarezerwuj");
        reserveBookButton.setPreferredSize(new Dimension(130, 45));
        reserveBookButton.setFont(new Font("Roboto", Font.PLAIN, 16));
        reserveBookButton.setForeground(Color.DARK_GRAY);
        reserveBookButton.setBackground(Color.LIGHT_GRAY);
        reserveBookButton.setBorder(new LineBorder(Color.GRAY));

        cancelButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new UserWindow(jdbcUrl, dbUsername, dbPassword, username));
            dispose();
        });

        borrowBookButton.addActionListener(e -> {
            borrowingBook();
        });

        reserveBookButton.addActionListener(e -> {
            reservingBook();
        });

        bottomButtonsPanel.add(cancelButton);
        topButtonsPanel.add(borrowBookButton);
        topButtonsPanel.add(reserveBookButton);

        panel.add(topButtonsPanel, BorderLayout.NORTH);
        panel.add(bottomButtonsPanel, BorderLayout.SOUTH);

        initializeTable();

        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setForeground(Color.LIGHT_GRAY);
        scrollPane.setBackground(Color.LIGHT_GRAY);
        scrollPane.setBorder(new LineBorder(Color.DARK_GRAY));
        scrollPane.getViewport().setBackground(Color.DARK_GRAY);

        panel.add(scrollPane, BorderLayout.CENTER);
    }

    private void initializeTable(){
        ArrayList<String[]> tableData = fetchDataFromDatabase();

        String[] columnNames = {"Tytuł książki", "Autor", "Rok wydania", "Dostępność", "Ilość"};

        DefaultTableModel model = new DefaultTableModel(tableData.toArray(new Object[0][0]), columnNames){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable = new JTable(model);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.setFont(new Font("Roboto", Font.PLAIN, 15));
        booksTable.setRowHeight(20);
        booksTable.setForeground(Color.DARK_GRAY);
        booksTable.setBackground(Color.LIGHT_GRAY);
        booksTable.setBorder(new LineBorder(Color.LIGHT_GRAY));

        booksTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        booksTable.getColumnModel().getColumn(0).setMinWidth(250);
        booksTable.getColumnModel().getColumn(1).setMinWidth(180);
        booksTable.getColumnModel().getColumn(2).setMinWidth(100);
        booksTable.getColumnModel().getColumn(3).setMinWidth(130);

        booksTable.getTableHeader().setReorderingAllowed(false);
        booksTable.getTableHeader().setFont(new Font("Roboto", Font.PLAIN, 15));
        booksTable.getTableHeader().setForeground(Color.DARK_GRAY);
        booksTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        booksTable.getTableHeader().setBorder(new LineBorder(Color.GRAY));
    }

    private ArrayList<String[]> fetchDataFromDatabase() {

        ArrayList<String[]> data = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT BookTitle, AuthorFullName, PublicationYear, BookAvailability, Amount FROM BookView";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String bookTitle = resultSet.getString("BookTitle");
                    String authorFullName = resultSet.getString("AuthorFullName");
                    String publicationYear = resultSet.getString("PublicationYear");
                    String availability = resultSet.getString("BookAvailability");
                    String amount = resultSet.getString("Amount");
                    data.add(new String[]{bookTitle, authorFullName, publicationYear, availability, amount});
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

                        // Zmiana ilości w bibliotece
                        int currentAmount = getBookAmount(bookTitle);

                        String updateQuery = "UPDATE Books SET Amount = Amount - 1";
                        if (currentAmount - 1 == 0) {
                            updateQuery += ", BookAvailability = 'niedostępna'";
                        }
                        updateQuery += " WHERE BookID = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setInt(1, getBookID(bookTitle));
                            updateStatement.executeUpdate();
                            refreshTable();
                        }

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

    private void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) booksTable.getModel();
        model.setRowCount(0);
        ArrayList<String[]> tableData = fetchDataFromDatabase();
        for (String[] rowData : tableData) {
            model.addRow(rowData);
        }
        model.fireTableDataChanged();
    }

    private int getBookAmount(String bookTitle) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT Amount FROM Books WHERE Title = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, bookTitle);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("Amount");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
