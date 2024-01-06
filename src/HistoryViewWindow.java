import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class HistoryViewWindow extends JFrame {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final String username;

    private final int mainR;
    private final int mainG;
    private final int mainB;

    private JPanel panel;
    private JTable historyTable;

    public HistoryViewWindow(String jdbcUrl, String dbUsername, String dbPassword, String username, int mainR, int mainG, int mainB) {

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
        setTitle("System obsługi biblioteki - historia wypożyczeń");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.DARK_GRAY);
        setVisible(true);
        requestFocusInWindow();

        setVisible(true);

        add(panel);
    }

    private void initComponents() {

        panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setOpaque(false);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.setBackground(Color.LIGHT_GRAY);
        buttonsPanel.setOpaque(false);

        MyButton okButton = new MyButton("OK", mainR, mainG, mainB);
        okButton.setPreferredSize(new Dimension(150, 45));

        okButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new UserWindow(jdbcUrl, dbUsername, dbPassword, username, mainR, mainG, mainB));
            dispose();
        });

        buttonsPanel.add(okButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        initializeTable();

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setForeground(Color.LIGHT_GRAY);
        scrollPane.setBackground(Color.LIGHT_GRAY);
        scrollPane.setBorder(new LineBorder(Color.DARK_GRAY));
        scrollPane.getViewport().setBackground(Color.DARK_GRAY);

        panel.add(scrollPane, BorderLayout.CENTER);
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
        historyTable.setFont(new Font("Roboto", Font.PLAIN, 15));
        historyTable.setRowHeight(20);
        historyTable.setForeground(Color.DARK_GRAY);
        historyTable.setBackground(Color.LIGHT_GRAY);
        historyTable.setBorder(new LineBorder(Color.LIGHT_GRAY));

        historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        historyTable.getColumnModel().getColumn(0).setMinWidth(200);

        historyTable.getTableHeader().setReorderingAllowed(false);
        historyTable.getTableHeader().setFont(new Font("Roboto", Font.PLAIN, 15));
        historyTable.getTableHeader().setForeground(Color.DARK_GRAY);
        historyTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        historyTable.getTableHeader().setBorder(new LineBorder(Color.GRAY));
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
