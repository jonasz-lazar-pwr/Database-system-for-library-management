import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class ReservationsViewWindow extends JFrame {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final String username;

    private JPanel panel;
    private JTable reservationsTable;
    private boolean isDataFiltered;

    public ReservationsViewWindow(String jdbcUrl, String dbUsername, String dbPassword, String username) {

        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.username = username;

        this.isDataFiltered = false;

        initComponents();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("System obsługi biblioteki - przeglądanie rezerwacji");
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
                    filterReservationsByUser(selectedUsername);
                }
                isDataFiltered = true;
                selectUserButton.setText("Usuń filtr");
            } else{
                updateReservationsTable();
                isDataFiltered = false;
                selectUserButton.setText("Filtruj");
            }

        });

        buttonsPanel.add(menuReturnButton);
        buttonsPanel.add(selectUserButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        initializeReservationsTable();

        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        scrollPane.setForeground(Color.LIGHT_GRAY);
        scrollPane.setBackground(Color.LIGHT_GRAY);
        scrollPane.setBorder(new LineBorder(Color.DARK_GRAY));
        scrollPane.getViewport().setBackground(Color.DARK_GRAY);

        panel.add(scrollPane, BorderLayout.CENTER);

    }

    private void initializeReservationsTable() {
        ArrayList<String[]> tableData = fetchDataFromDatabaseReservations();

        String[] columnNames = {"Czytelnik", "Tytuł książki", "Data rezerwacji", "Status"};

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

        reservationsTable.getTableHeader().setReorderingAllowed(false);
        reservationsTable.getTableHeader().setFont(new Font("Roboto", Font.PLAIN, 15));
        reservationsTable.getTableHeader().setForeground(Color.DARK_GRAY);
        reservationsTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        reservationsTable.getTableHeader().setBorder(new LineBorder(Color.GRAY));
    }

    private ArrayList<String[]> fetchDataFromDatabaseReservations(){
        ArrayList<String[]> data = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT login, title, reservation_date, status FROM UserReservationsView";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()){
                    String login = resultSet.getString("login");
                    String bookTitle = resultSet.getString("title");
                    String reservationDate = resultSet.getString("reservation_date");
                    String status = resultSet.getString("status");
                    data.add(new String[]{login, bookTitle, reservationDate, status});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }


    private void updateReservationsTable() {
        ArrayList<String[]> tableData = fetchDataFromDatabaseReservations();

        DefaultTableModel model = (DefaultTableModel) reservationsTable.getModel();
        model.setRowCount(0);

        for (String[] rowData : tableData) {
            model.addRow(rowData);
        }
    }

    private void updateReservationsTable(ArrayList<String[]> tableData) {
        DefaultTableModel model = (DefaultTableModel) reservationsTable.getModel();
        model.setRowCount(0);

        for (String[] rowData : tableData) {
            model.addRow(rowData);
        }
    }

    private void filterReservationsByUser(String selectedUsername) {
        ArrayList<String[]> filteredData = fetchDataForUserFromDatabaseReservations(selectedUsername);
        updateReservationsTable(filteredData);
    }

    private ArrayList<String[]> fetchDataForUserFromDatabaseReservations(String selectedUsername) {
        ArrayList<String[]> data = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT login, title, reservation_date, status FROM UserReservationsView WHERE login = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, selectedUsername);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()){
                        String login = resultSet.getString("login");
                        String bookTitle = resultSet.getString("title");
                        String reservationDate = resultSet.getString("reservation_date");
                        String status = resultSet.getString("status");
                        data.add(new String[]{login, bookTitle, reservationDate, status});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }
}
