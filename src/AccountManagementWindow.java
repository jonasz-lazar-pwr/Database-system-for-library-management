import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Objects;

public class AccountManagementWindow extends JFrame implements FocusListener {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private String username;

    private final int mainR;
    private final int mainG;
    private final int mainB;

    private MyTextField usernameField;
    private MyPasswordField passwordField;

    private JPanel textFieldPanel;
    private JPanel buttonPanel;

    public AccountManagementWindow(String jdbcUrl, String dbUsername, String dbPassword, String username, int mainR, int mainG, int mainB/*, UserWindow userWindowReference, AdminWindow adminWindowReference*/) {

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
        setTitle("System obsługi biblioteki - zarządzanie kontem");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Color.DARK_GRAY);
        setVisible(true);
        requestFocusInWindow();

        // Dodanie panelu do okna
        add(textFieldPanel);
        add(buttonPanel);
    }

    private void initComponents() {

        textFieldPanel = new JPanel();
        textFieldPanel.setBounds(145, 100, 200, 100);
        textFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        textFieldPanel.setBackground(Color.LIGHT_GRAY);
        textFieldPanel.setOpaque(false);

        usernameField = new MyTextField("Nowy login", mainR, mainG, mainB);
        usernameField.setPreferredSize(new Dimension(200, 35));
        usernameField.addFocusListener(this);
        textFieldPanel.add(usernameField);

        passwordField = new MyPasswordField("Nowe hasło", mainR, mainG, mainB);
        passwordField.setPreferredSize(new Dimension(200, 35));
        passwordField.addFocusListener(this);
        textFieldPanel.add(passwordField);

        // Panel z przyciskami
        buttonPanel = new JPanel();
        buttonPanel.setBounds(145, 225, 200, 140);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        buttonPanel.setOpaque(false);

        MyButton confirmButton = getMyButton();
        buttonPanel.add(confirmButton);

        MyButton cancelButton = new MyButton("Anuluj", mainR, mainG, mainB);

        cancelButton.addActionListener(e -> {
            if (Objects.equals(getUserRole(username), "czytelnik")) {
                SwingUtilities.invokeLater(() -> new UserWindow(jdbcUrl, dbUsername, dbPassword, username, mainR, mainG, mainB));
                dispose();
            } else if (Objects.equals(getUserRole(username), "bibliotekarz")) {
                SwingUtilities.invokeLater(() -> new AdminWindow(jdbcUrl, dbUsername, dbPassword, username, mainR, mainG, mainB));
                dispose();
            }
        });

        buttonPanel.add(cancelButton);
    }

    private MyButton getMyButton() {

        MyButton confirmButton = new MyButton("Potwierdź", mainR, mainG, mainB);

        confirmButton.addActionListener(e -> {

            String newUsername = usernameField.getText();
            String newPassword = new String(passwordField.getPassword());

            if (doesUserExist(newUsername)) {
                JOptionPane.showMessageDialog(AccountManagementWindow.this, "Użytkownik o takiej nazwie już istnieje!", "Błąd", JOptionPane.ERROR_MESSAGE);
            } else {

                String password = JOptionPane.showInputDialog("Podaj hasło do potwierdzenia zmian");

                if (password != null && !password.isEmpty() && !password.equals("Nowe hasło") && checkUserPassword(username, password)) {

                    if (!newUsername.isEmpty() && !newUsername.equals(username) && !newUsername.equals("Nowy login")) {

                        updateUsername(username, newUsername);
                        setUsername(newUsername);
                    }

                    if (!newPassword.isEmpty()) {
                        updatePassword(newUsername, newPassword);
                    }

                    if (Objects.equals(getUserRole(username), "czytelnik")) {
                        SwingUtilities.invokeLater(() -> new UserWindow(jdbcUrl, dbUsername, dbPassword, username, mainR, mainG, mainB));
                        dispose();
                    }

                    if (Objects.equals(getUserRole(username), "bibliotekarz")) {
                        SwingUtilities.invokeLater(() -> new AdminWindow(jdbcUrl, dbUsername, dbPassword, username, mainR, mainG, mainB));
                        dispose();
                    }

                } else {
                    JOptionPane.showMessageDialog(AccountManagementWindow.this, "Wprowadzono niepoprawne hasło.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return confirmButton;
    }

    private boolean doesUserExist(String username) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT COUNT(*) AS UserCount FROM Users WHERE Login = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int userCount = resultSet.getInt("UserCount");
                        return userCount > 0;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean passwordsMatch(String givenPassword, String storedPassword) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(givenPassword.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashedGivenPassword = bigInt.toString(16);

            return Objects.equals(hashedGivenPassword, storedPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkUserPassword(String username, String password) {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

            String query = "SELECT UserPassword, UserRole FROM Users WHERE Login = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);

                // Wykonaj zapytanie
                ResultSet resultSet = preparedStatement.executeQuery();

                // Sprawdź, czy znaleziono użytkownika
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("UserPassword");
                    String role = resultSet.getString("UserRole");

                    resultSet.close();

                    if (passwordsMatch(password, storedPassword)) {
                        if (Objects.equals(role, "czytelnik")) {
                            return true;
                        } else if (Objects.equals(role, "bibliotekarz")) {
                            return true;
                        }
                    }
                } else {
                    System.out.println("Nie znaleziono użytkownika." + username);
                    resultSet.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Metoda do sprawdzania roli użytkownika
    public String getUserRole(String username) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT UserRole FROM Users WHERE Login = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("UserRole");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Zwracanie null w przypadku błędu lub braku informacji
    }

    private void updateUsername(String username, String newUsername) {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

            String updateQuery = "UPDATE Users SET Login = ? WHERE Login = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, newUsername);
                preparedStatement.setString(2, username);

                // Wykonaj zapytanie
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(AccountManagementWindow.this, "Zaktualizowano nazwę użytkownika.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(AccountManagementWindow.this, "Nie udało się zmienić nazwy użytkownika.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePassword(String username, String newPassword) {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

            String updateQuery = "UPDATE Users SET UserPassword = MD5(?) WHERE Login = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, newPassword);
                preparedStatement.setString(2, username);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(AccountManagementWindow.this, "Zmieniono hasło.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(AccountManagementWindow.this, "Nie udało się zmienić hasła.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUsername(String username){
        this.username = username;
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (e.getSource() == usernameField) {
            if (usernameField.getText().equals("Nowy login")) {
                usernameField.setText("");
            }
        } else if (e.getSource() == passwordField) {
            String passwordText = new String(passwordField.getPassword());

            if (passwordText.equals("Nowe hasło")) {
                passwordField.setEchoChar('\u2022');
                passwordField.setText("");
            }
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (e.getSource() == usernameField) {
            if (usernameField.getText().isEmpty()) {
                usernameField.setText("Nowy login");
            }
        } else if (e.getSource() == passwordField) {
            String passwordText = new String(passwordField.getPassword());

            if (passwordText.isEmpty()) {
                passwordField.setEchoChar((char) 0);
                passwordField.setText("Nowe hasło");
            }
        }
    }
}