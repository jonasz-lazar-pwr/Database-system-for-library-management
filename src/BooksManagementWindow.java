import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class BooksManagementWindow extends JFrame {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private String username;

    private final int mainR;
    private final int mainG;
    private final int mainB;

    private JPanel panel;
    private JTable booksTable;

    public BooksManagementWindow(String jdbcUrl, String dbUsername, String dbPassword, String username, int mainR, int mainG, int mainB) {

        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.username = username;

        this.mainR = mainR;
        this.mainG = mainG;
        this.mainB = mainB;

        initComponents();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("System obsługi biblioteki - zarządzeanie książkami");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        //setLayout(null);
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

        MyButton returnButton = new MyButton("Powrót", mainR, mainG, mainB);
        returnButton.setPreferredSize(new Dimension(150, 45));

        returnButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new AdminWindow(jdbcUrl, dbUsername, dbPassword, username, mainR, mainG, mainB));
            dispose();
        });

        MyButton addBookButton = new MyButton("Dodaj książkę", mainR, mainG, mainB);
        addBookButton.setPreferredSize(new Dimension(150, 45));

        addBookButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new AddBookWindow(jdbcUrl,dbUsername,dbPassword));
        });

        MyButton removeBookButton = new MyButton("Usuń książkę", mainR, mainG, mainB);
        removeBookButton.setPreferredSize(new Dimension(150, 45));

        removeBookButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new RemoveBookWindow(jdbcUrl, dbUsername, dbPassword));
        });


        buttonsPanel.add(returnButton);
        buttonsPanel.add(addBookButton);
        buttonsPanel.add(removeBookButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        initializeBooksTable();

        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setForeground(Color.LIGHT_GRAY);
        scrollPane.setBackground(Color.LIGHT_GRAY);
        scrollPane.setBorder(new LineBorder(Color.DARK_GRAY));
        scrollPane.getViewport().setBackground(Color.DARK_GRAY);

        panel.add(scrollPane, BorderLayout.CENTER);
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


    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }
}
