import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ReportWindow extends JFrame {
    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;

    public ReportWindow(String jdbcUrl, String dbUsername, String dbPassword) {
        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;

        setTitle("Generowanie raportów");
        setSize(400, 250);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 1));

        JButton buttonA = new JButton("Lista książek wg. ilości wypożyczeń"); // mostPopularBooksView
        JButton buttonB = new JButton("Lista czytelników wg. ilości wypożyczonych książek"); // mostActiveUsersView
        JButton buttonC = new JButton("Lista autorów wg. liczby wypożyczonych książek"); // mostPopularAuthorsView
        JButton buttonD = new JButton("Lista najdłużej niezwróconych książek"); // longestNotReturnedBooksView

        buttonA.addActionListener(e -> {
            openReportView("mostPopularBooksView");
        });
        buttonB.addActionListener(e -> {
            openReportView("mostActiveUsersView");
        });
        buttonC.addActionListener(e -> {
            openReportView("mostPopularAuthorsView");
        });
        buttonD.addActionListener(e -> {
            openReportView("longestNotReturnedBooksView");
        });

        mainPanel.add(buttonA);
        mainPanel.add(buttonB);
        mainPanel.add(buttonC);
        mainPanel.add(buttonD);

        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void openReportView(String viewName) {
        String query = "SELECT * FROM " + viewName;
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            JFrame reportFrame = new JFrame("Raport");
            JTable reportTable = new JTable(buildTableModel(resultSet));
            JScrollPane scrollPane = new JScrollPane(reportTable);
            reportFrame.setLocationRelativeTo(this);
            reportFrame.add(scrollPane);
            reportFrame.setSize(800, 400);
            reportFrame.setVisible(true);

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