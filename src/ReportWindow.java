import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ReportWindow extends JFrame {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private String username;

    private JPanel panel;
    private JTable reportsTable;

    public ReportWindow(String jdbcUrl, String dbUsername, String dbPassword, String username) {

        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.username = username;

        initComponents();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("System obsługi biblioteki - generowanie raportów");
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

        MyButton returnButton = new MyButton("Powrót");
        returnButton.setPreferredSize(new Dimension(150, 45));

        returnButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new AdminWindow(jdbcUrl, dbUsername, dbPassword, username));
            dispose();
        });

        MyButton generateReportsButton = new MyButton("Generuj raport");
        generateReportsButton.setPreferredSize(new Dimension(150, 45));

        generateReportsButton.addActionListener(e -> {
            int selectedRow = reportsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String viewName = getViewNameForSelectedRow(selectedRow);
                openReportView(viewName);
            } else {
                JOptionPane.showMessageDialog(ReportWindow.this, "Wybierz raport do wygenerowania.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonsPanel.add(returnButton);
        buttonsPanel.add(generateReportsButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        initializeReportsTable();

        JScrollPane scrollPane = new JScrollPane(reportsTable);
        scrollPane.setForeground(Color.LIGHT_GRAY);
        scrollPane.setBackground(Color.LIGHT_GRAY);
        scrollPane.setBorder(new LineBorder(Color.DARK_GRAY));
        scrollPane.getViewport().setBackground(Color.DARK_GRAY);

        panel.add(scrollPane, BorderLayout.CENTER);
    }

    private void initializeReportsTable() {

        // Utwórz model tabeli z jedną kolumną
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Raporty");

        // Dodaj dane do modelu tabeli
        tableModel.addRow(new Object[]{"Lista książek wg. liczby wypożyczeń"});
        tableModel.addRow(new Object[]{"Lista czytelników wg. lczby wypożyczonych książek"});
        tableModel.addRow(new Object[]{"Lista autorów wg. liczby wypożyczonych książek"});
        tableModel.addRow(new Object[]{"Lista najdłużej niezwróconych książek"});

        reportsTable = new JTable(tableModel);
        reportsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportsTable.setFont(new Font("Roboto", Font.PLAIN, 15));
        reportsTable.setRowHeight(20);
        reportsTable.setForeground(Color.DARK_GRAY);
        reportsTable.setBackground(Color.LIGHT_GRAY);
        reportsTable.setBorder(new LineBorder(Color.LIGHT_GRAY));

        reportsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        reportsTable.getTableHeader().setReorderingAllowed(false);
        reportsTable.getTableHeader().setFont(new Font("Roboto", Font.PLAIN, 15));
        reportsTable.getTableHeader().setForeground(Color.DARK_GRAY);
        reportsTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        reportsTable.getTableHeader().setBorder(new LineBorder(Color.GRAY));
    }

    private String getViewNameForSelectedRow(int selectedRow) {
        return switch (selectedRow) {
            case 0 -> "mostPopularBooksView";
            case 1 -> "mostActiveUsersView";
            case 2 -> "mostPopularAuthorsView";
            case 3 -> "longestNotReturnedBooksView";
            default -> throw new IllegalArgumentException("Nieprawidłowy numer wiersza: " + selectedRow);
        };
    }

    private void openReportView(String viewName) {
        String query = "SELECT * FROM " + viewName;
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            JFrame reportFrame = new JFrame("System obsługi biblioteki - raport");
            reportFrame.setSize(750, 500);
            reportFrame.setLocationRelativeTo(null);
            reportFrame.setResizable(false);
            reportFrame.getContentPane().setBackground(Color.DARK_GRAY);
            reportFrame.setVisible(true);
            reportFrame.requestFocusInWindow();

            JTable reportTable = new JTable(buildTableModel(resultSet));
            reportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            reportTable.setFont(new Font("Roboto", Font.PLAIN, 15));
            reportTable.setRowHeight(20);
            reportTable.setForeground(Color.DARK_GRAY);
            reportTable.setBackground(Color.LIGHT_GRAY);
            reportTable.setBorder(new LineBorder(Color.LIGHT_GRAY));

            reportTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            reportTable.getTableHeader().setReorderingAllowed(false);
            reportTable.getTableHeader().setFont(new Font("Roboto", Font.PLAIN, 15));
            reportTable.getTableHeader().setForeground(Color.DARK_GRAY);
            reportTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
            reportTable.getTableHeader().setBorder(new LineBorder(Color.GRAY));

            JScrollPane scrollPane = new JScrollPane(reportTable);
            scrollPane.setForeground(Color.LIGHT_GRAY);
            scrollPane.setBackground(Color.LIGHT_GRAY);
            scrollPane.setBorder(new LineBorder(Color.DARK_GRAY));
            scrollPane.getViewport().setBackground(Color.DARK_GRAY);

            reportFrame.add(scrollPane);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas pobierania danych z bazy.", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private DefaultTableModel buildTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        Vector<String> columnNames = new Vector<>();
        Vector<Vector<Object>> data = new Vector<>();

        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        while (resultSet.next()) {
            Vector<Object> row = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                row.add(resultSet.getObject(columnIndex));
            }
            data.add(row);
        }

        return new DefaultTableModel(data, columnNames);
    }
}
