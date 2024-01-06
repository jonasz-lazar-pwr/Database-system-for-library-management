import javax.swing.*;
import java.awt.*;

public class AdminWindow extends JFrame {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final String username;

    private final int mainR;
    private final int mainG;
    private final int mainB;

    JPanel buttonsPanel;

    public AdminWindow(String jdbcUrl, String dbUsername, String dbPassword, String username, int mainR, int mainG, int mainB) {

        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.username = username;

        this.mainR = mainR;
        this.mainG = mainG;
        this.mainB = mainB;

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
        buttonsPanel.setBounds(145, 60, 200, 335);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        buttonsPanel.setBackground(Color.GRAY);
        buttonsPanel.setOpaque(false);

        MyButton rentalsManagementButton = new MyButton("Wypożyczenia", mainR, mainG, mainB);

        rentalsManagementButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new RentalsManagementWindow(jdbcUrl, dbUsername, dbPassword, username, mainR, mainG, mainB));
            dispose();
        });

        MyButton viewBooksButton = new MyButton("Zarządzaj książkami", mainR, mainG, mainB);

        viewBooksButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new BooksManagementWindow(jdbcUrl, dbUsername, dbPassword, username, mainR, mainG, mainB));
            dispose();
        });

        MyButton accountManagementButton = new MyButton("Moje konto", mainR, mainG, mainB);

        accountManagementButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new AccountManagementWindow(jdbcUrl, dbUsername, dbPassword, username, mainR, mainG, mainB));
            dispose();
        });

        MyButton reportButton = new MyButton("Raporty", mainR, mainG, mainB);

        reportButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new ReportWindow(jdbcUrl, dbUsername, dbPassword));
        });

        MyButton logoutButton = new MyButton("Wyloguj się", mainR, mainG, mainB);

        logoutButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new LoginWindow(jdbcUrl, dbUsername, dbPassword, mainR, mainG, mainB));
            dispose();
        });

        buttonsPanel.add(rentalsManagementButton);
        buttonsPanel.add(viewBooksButton);
        buttonsPanel.add(accountManagementButton);
        buttonsPanel.add(reportButton);
        buttonsPanel.add(logoutButton);
    }
}
