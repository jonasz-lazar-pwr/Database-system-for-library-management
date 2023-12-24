import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class HistoryViewWindow extends JFrame {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private String username;
    private JTable historyTable;

    public HistoryViewWindow(String jdbcUrl, String dbUsername, String dbPassword, String username) {
        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.username = username;

        setTitle("Historia wypożyczeń");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        initializeTable();

        JScrollPane scrollPane = new JScrollPane(historyTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        setVisible(true);
    }

    private void initializeTable() {
        ArrayList<String[]> tableData = fetchHistoryFromDatabase();

        String[] columnNames = {"Tytuł książki", "Data wypożyczenia", "Data zwrotu", "Status"};

        DefaultTableModel model = new DefaultTableModel(tableData.toArray(new Object[0][0]), columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(model);

        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private ArrayList<String[]> fetchHistoryFromDatabase() {
        ArrayList<String[]> data = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT login, title, loan_date, return_date, status FROM UserLoansView WHERE login = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {

                    while (resultSet.next()) {
                        //String userLogin = resultSet.getString("login");
                        String bookTitle = resultSet.getString("title");
                        Date loanDate = resultSet.getDate("loan_date");
                        Date returnDate = resultSet.getDate("return_date");
                        String status = resultSet.getString("status");
                        data.add(new String[]{bookTitle, loanDate.toString(), String.valueOf(returnDate), status});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }
}
