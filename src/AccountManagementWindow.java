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

    private MyTextField usernameField;
    private MyPasswordField passwordField;

    private JPanel textFieldPanel;
    private JPanel buttonPanel;

    public AccountManagementWindow(String jdbcUrl, String dbUsername, String dbPassword, String username) {

        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.username = username;

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

        usernameField = new MyTextField("Nowy login");
        usernameField.setPreferredSize(new Dimension(200, 35));
        usernameField.addFocusListener(this);
        textFieldPanel.add(usernameField);

        passwordField = new MyPasswordField("Nowe hasło");
        passwordField.setPreferredSize(new Dimension(200, 35));
        passwordField.addFocusListener(this);
        textFieldPanel.add(passwordField);

        // Panel z przyciskami
        buttonPanel = new JPanel();
        buttonPanel.setBounds(145, 225, 200, 140);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        buttonPanel.setOpaque(false);

        MyButton confirmButton = new MyButton("Potwierdź");

        confirmButton.addActionListener(e -> handleConfirmButton());
        buttonPanel.add(confirmButton);

        MyButton cancelButton = new MyButton("Anuluj");

        cancelButton.addActionListener(e -> handleCancelButton());
        buttonPanel.add(cancelButton);

    }

    private void handleConfirmButton() {

        String validationMessage = validateFields();
        if (validationMessage.isEmpty()) {
            String newUsername = usernameField.getText();
            String newPassword = new String(passwordField.getPassword());

            boolean isDefaultUsername = newUsername.equals("Nowy login");
            boolean isDefaultPassword = newPassword.equals("Nowe hasło");

            if (isDefaultUsername && isDefaultPassword) {
                JOptionPane.showMessageDialog(this, "Wprowadź poprawne dane.", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isDefaultUsername && doesUserExist(newUsername)) {
                JOptionPane.showMessageDialog(this, "Użytkownik o takiej nazwie już istnieje!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String password = getPasswordForConfirmation();

            if (password != null && checkUserPassword(username, password)) {
                if (!isDefaultUsername) {
                    updateUsername(username, newUsername);
                    setUsername(newUsername);
                }

                // Sprawdzamy, czy hasło zostało zmienione
                if (!isDefaultPassword) {
                    // Tutaj dokonujemy aktualizacji hasła i sprawdzamy, czy operacja się powiodła
                    boolean passwordUpdated = updatePassword(this.username, newPassword);

                    // Jeśli hasło zostało zmienione poprawnie
                    if (passwordUpdated) {
                        JOptionPane.showMessageDialog(this, "Zmieniono hasło.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Nie udało się zmienić hasła.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }

                navigateToNextWindow();

            } else {
                JOptionPane.showMessageDialog(this, "Wprowadzono niepoprawne hasło.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Wyświetl komunikat o błędzie walidacji pól
            JOptionPane.showMessageDialog(this, validationMessage, "Błąd walidacji", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String validateFields() {
        StringBuilder validationMessage = new StringBuilder();

        String newUsername = usernameField.getText();
        String newPassword = new String(passwordField.getPassword());

        if (!validateNonDefault(usernameField, "Nowy login") && (newUsername.length() < 4 || newUsername.length() > 20 || containsSpaces(newUsername))) {
            validationMessage.append("Błędny login. Login nie może zawierać spacji na końcu i początku oraz musi mieć od 4 do 20 znaków.\n");
        }

        if (!validateNonDefault(passwordField, "Nowe hasło") && (!validatePassword(newPassword) || containsSpaces(newPassword))) {
            validationMessage.append("Błędne hasło. Hasło musi zawierać minimum 8 znaków, przynajmniej jedną cyfrę, jedną małą literę, jedną dużą literę, jeden znak specjalny oraz brak spacji.\n");
        }

        return validationMessage.toString();
    }

    private boolean validateNonDefault(JTextField field, String defaultValue) {
        return field.getText().trim().equals(defaultValue);
    }

    private boolean containsSpaces(String text) {
        return text.trim().length() != text.length();
    }

    private boolean validateUsername(String username) {
        // Dodaj swoją walidację dla loginu, np. sprawdzanie długości
        return username.length() >= 4 && username.length() <= 20;
    }

    private boolean validatePassword(String password) {
        // Dodaj swoją walidację dla hasła, używając podanego wzoru
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(regex);
    }

    private void handleCancelButton() {
        if (Objects.equals(getUserRole(username), "czytelnik")) {
            SwingUtilities.invokeLater(() -> new UserWindow(jdbcUrl, dbUsername, dbPassword, username));
            dispose();
        } else if (Objects.equals(getUserRole(username), "bibliotekarz")) {
            SwingUtilities.invokeLater(() -> new AdminWindow(jdbcUrl, dbUsername, dbPassword, username));
            dispose();
        }
    }

    private String getPasswordForConfirmation() {
        JPasswordField passwordField = new JPasswordField();
        int option = JOptionPane.showOptionDialog(
                this,
                passwordField,
                "Podaj hasło do potwierdzenia zmian",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null);

        if (option == JOptionPane.OK_OPTION) {
            return new String(passwordField.getPassword());
        } else {
            return null;
        }
    }


    private void navigateToNextWindow() {
        if (Objects.equals(getUserRole(username), "czytelnik")) {
            SwingUtilities.invokeLater(() -> new UserWindow(jdbcUrl, dbUsername, dbPassword, username));
            dispose();
        } else if (Objects.equals(getUserRole(username), "bibliotekarz")) {
            SwingUtilities.invokeLater(() -> new AdminWindow(jdbcUrl, dbUsername, dbPassword, username));
            dispose();
        }
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
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT UserPassword, UserRole FROM Users WHERE Login = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);

                // Wykonaj zapytanie
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // Sprawdź, czy znaleziono użytkownika
                    if (resultSet.next()) {
                        String storedPassword = resultSet.getString("UserPassword");
                        String role = resultSet.getString("UserRole");

                        // Sprawdź, czy hasła się zgadzają
                        if (passwordsMatch(password, storedPassword)) {
                            return true;
                        }
                    } else {
                        System.out.println("Nie znaleziono użytkownika." + username);
                    }
                }
            }
        } catch (SQLException e) {
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
        return null;
    }

    private void updateUsername(String username, String newUsername) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String updateQuery = "UPDATE Users SET Login = ? WHERE Login = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, newUsername);
                preparedStatement.setString(2, username);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(AccountManagementWindow.this, "Zaktualizowano nazwę użytkownika.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(AccountManagementWindow.this, "Nie udało się zmienić nazwy użytkownika.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean updatePassword(String username, String newPassword) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String updateQuery = "UPDATE Users SET UserPassword = MD5(?) WHERE Login = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, newPassword);
                preparedStatement.setString(2, username);

                // Wykonaj zapytanie
                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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