import javax.swing.*;
import java.awt.*;

public class UserWindow extends JFrame {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final String username;

    JPanel buttonsPanel;

    public UserWindow(String jdbcUrl, String dbUsername, String dbPassword, String username) {

        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.username = username;

        initComponents();

        // Ustawienia okna
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("System obsługi biblioteki - panel czytelnika");
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
        buttonsPanel.setBounds(145, 60, 200, 335);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        buttonsPanel.setBackground(Color.GRAY);
        buttonsPanel.setOpaque(false);

        MyButton browsingBooksButton = new MyButton("Przeglądaj książki");

        browsingBooksButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new BrowseBooksWindow(jdbcUrl, dbUsername, dbPassword, username));
            dispose();
        });

        MyButton accountManagementButton = new MyButton("Moje konto");

        accountManagementButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new AccountManagementWindow(jdbcUrl, dbUsername, dbPassword, username));
            dispose();
        });

        MyButton reservationsButton = new MyButton("Moje rezerwacje");

        reservationsButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new ReservationsManagementWindow(jdbcUrl, dbUsername, dbPassword, username));
            dispose();
        });

        MyButton rentalHistoryButton = new MyButton("Moje wypożyczenia");

        rentalHistoryButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new HistoryViewWindow(jdbcUrl, dbUsername, dbPassword, username));
            dispose();
        });

        MyButton logoutButton = new MyButton("Wyloguj się");

        logoutButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new LoginWindow(jdbcUrl, dbUsername, dbPassword));
            dispose();
        });

        buttonsPanel.add(browsingBooksButton);
        buttonsPanel.add(accountManagementButton);
        buttonsPanel.add(reservationsButton);
        buttonsPanel.add(rentalHistoryButton);
        buttonsPanel.add(logoutButton);
    }
}
