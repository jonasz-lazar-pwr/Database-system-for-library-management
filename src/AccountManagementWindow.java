import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Arrays;
import java.util.Objects;

public class AccountManagementWindow extends JFrame {

    private JTextField usernameTextField;
    private JPasswordField passwordTextField;

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private String username;
    private final UserWindow userWindowReference;

    public AccountManagementWindow(String jdbcUrl, String dbUsername, String dbPassword, String username, UserWindow userWindowReference) {
        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.username = username;
        this.userWindowReference = userWindowReference;

        setTitle("Zarządzanie kontem");
        setSize(440, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 1));

        usernameTextField = new JTextField();
        passwordTextField = new JPasswordField();

        JLabel newUsernameLabel = new JLabel("Nowa nazwa użytkownika");
        JLabel newPasswordLabel = new JLabel("Nowe hasło");
        JButton cancelButton = new JButton("Anuluj");
        JButton confirmButton = new JButton("Potwierdź");

        mainPanel.add(newUsernameLabel);
        mainPanel.add(usernameTextField);
        mainPanel.add(newPasswordLabel);
        mainPanel.add(passwordTextField);
        mainPanel.add(new JLabel());
        mainPanel.add(new JLabel());
        mainPanel.add(cancelButton);
        mainPanel.add(confirmButton);

        confirmButton.addActionListener(e -> {
            String newUsername = usernameTextField.getText();
            String newPassword = new String(passwordTextField.getPassword());

            if (doesUserExist(newUsername)) {
                JOptionPane.showMessageDialog(AccountManagementWindow.this, "Użytkownik o takiej nazwie już istnieje!", "Błąd", JOptionPane.ERROR_MESSAGE);
            } else {
                String password = JOptionPane.showInputDialog("Podaj hasło do potwierdzenia zmian");

                if (password != null && !password.isEmpty() && checkUserPassword(username, password)) {
                    if (!newUsername.isEmpty()) {
                        updateUsername(username, newUsername);
                        setUsername(newUsername);
                        userWindowReference.setUsername(newUsername);
                        // JOptionPane.showMessageDialog(AccountManagementWindow.this, "Zmieniono nazwę użytkownika", "Informacja", JOptionPane.INFORMATION_MESSAGE);
                    }

                    if (!newPassword.isEmpty()) {
                        updatePassword(newUsername, newPassword);
                        // JOptionPane.showMessageDialog(AccountManagementWindow.this, "Zmieniono hasło", "Informacja", JOptionPane.INFORMATION_MESSAGE);
                    }

                    dispose();
                } else{
                    JOptionPane.showMessageDialog(AccountManagementWindow.this, "Wprowadzono niepoprawne hasło.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(e -> dispose());

        mainPanel.setFocusable(true);
        mainPanel.requestFocusInWindow();

        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
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
                    System.out.println("Nie znaleziono użytkownika.");
                    resultSet.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateUsername(String username, String newUsername) {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

            // PreparedStatement, by uniknąć ataków SQL Injection
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

    private void setUsername(String username){
        this.username = username;
    }

}