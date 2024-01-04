import javax.swing.*;
import java.awt.*;


public class UserWindow extends JFrame {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final String username;

    private final int mainR;
    private final int mainG;
    private final int mainB;

    JPanel buttonsPanel;

    public UserWindow(String jdbcUrl, String dbUsername, String dbPassword, String username, int mainR, int mainG, int mainB) {

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

        MyButton browsingBooksButton = new MyButton("Przeglądaj książki", mainR, mainG, mainB);

        browsingBooksButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new BrowseBooksWindow(jdbcUrl, dbUsername, dbPassword, username, mainR, mainG, mainB));
            dispose();
        });

        MyButton accountManagementButton = new MyButton("Moje konto", mainR, mainG, mainB);

        accountManagementButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new AccountManagementWindow(jdbcUrl, dbUsername, dbPassword, username, mainR, mainG, mainB));
            dispose();
        });

        MyButton reservationsButton = new MyButton("Moje rezerwacje", mainR, mainG, mainB);

        reservationsButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new ReservationsManagementWindow(jdbcUrl, dbUsername, dbPassword, username, mainR, mainG, mainB));
            dispose();
        });

        MyButton rentalHistoryButton = new MyButton("Moje wypożyczenia", mainR, mainG, mainB);

        rentalHistoryButton.addActionListener(e -> {
            new HistoryViewWindow(jdbcUrl, dbUsername, dbPassword, username);
        });

        MyButton logoutButton = new MyButton("Wyloguj się", mainR, mainG, mainB);

        logoutButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new LoginWindow(jdbcUrl, dbUsername, dbPassword, mainR, mainG, mainB));
            dispose();
        });

        buttonsPanel.add(browsingBooksButton);
        buttonsPanel.add(accountManagementButton);
        buttonsPanel.add(reservationsButton);
        buttonsPanel.add(rentalHistoryButton);
        buttonsPanel.add(logoutButton);
    }
}
