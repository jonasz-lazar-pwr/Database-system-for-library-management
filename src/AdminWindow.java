import javax.swing.*;
import java.awt.*;

public class AdminWindow extends JFrame {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final String username;

    JPanel buttonsPanel;

    public AdminWindow(String jdbcUrl, String dbUsername, String dbPassword, String username) {

        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.username = username;

        initComponents();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("System obsługi biblioteki - panel administratora");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Color.DARK_GRAY);
        setVisible(true);
        requestFocusInWindow();

        // Dodanie panelu do okna
        add(buttonsPanel);

        System.out.println(username);
    }

    private void initComponents() {

        // Panel z przyciskami
        buttonsPanel = new JPanel();
        buttonsPanel.setBounds(145, 35, 200, 400);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        buttonsPanel.setBackground(Color.GRAY);
        buttonsPanel.setOpaque(false);

        MyButton rentalsManagementButton = new MyButton("Wypożyczenia");

        rentalsManagementButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new RentalsManagementWindow(jdbcUrl, dbUsername, dbPassword, username));
            dispose();
        });

        MyButton reservationsButton = new MyButton("Rezerwacje");

        reservationsButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new ReservationsViewWindow(jdbcUrl, dbUsername, dbPassword, username));
            dispose();
        });

        MyButton viewBooksButton = new MyButton("Zarządzaj książkami");

        viewBooksButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new BooksManagementWindow(jdbcUrl, dbUsername, dbPassword, username));
            dispose();
        });

        MyButton accountManagementButton = new MyButton("Moje konto");

        accountManagementButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new AccountManagementWindow(jdbcUrl, dbUsername, dbPassword, username));
            dispose();
        });

        MyButton reportButton = new MyButton("Raporty");

        reportButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new ReportWindow(jdbcUrl, dbUsername, dbPassword, username));
            dispose();
        });

        MyButton logoutButton = new MyButton("Wyloguj się");

        logoutButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new LoginWindow(jdbcUrl, dbUsername, dbPassword));
            dispose();
        });

        buttonsPanel.add(rentalsManagementButton);
        buttonsPanel.add(reservationsButton);
        buttonsPanel.add(viewBooksButton);
        buttonsPanel.add(accountManagementButton);
        buttonsPanel.add(reportButton);
        buttonsPanel.add(logoutButton);
    }
}
