import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Arrays;
import java.sql.Connection;
import java.util.Objects;

public class LoginWindow extends JFrame {

    String jdbcUrl;
    String dbUsername;
    String dbPassword;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginWindow(String jdbcUrl, String dbUsername, String dbPassword) {
        // Ustawienia połączenia z bazą danych
        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;

        // Ustawienia okna
        setTitle("Logowanie do systemu Książkowość");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ustawienia panelu
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        // Komponenty
        JLabel usernameLabel = new JLabel("Nazwa użytkownika:");
        JLabel passwordLabel = new JLabel("Hasło:");

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Zaloguj");
        JButton registerButton = new JButton("Nie masz konta? Zarejestruj");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] passArray = passwordField.getPassword();
                String password = new String(passArray);
                System.out.println("Login: " + username);
                System.out.println("Hasło: " + new String(password));

                // Logowanie
                if (checkIfUserExists(username)){
                    int check = checkUserPassword(username, password);
                    if (check==1){
                        System.out.println("Zalogowano jako czytelnik");
                        UserWindow user = new UserWindow();
                        dispose();
                    } else if (check == 2) {
                        System.out.println("Zalogowano jako bibliotekarz");
                        AdminWindow admin = new AdminWindow();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginWindow.this, "Niepoprawne hasło", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(LoginWindow.this, "Nieprawidłowe dane logowania!", "Błąd", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        // Dodawanie komponentów do panelu
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);
        panel.add(new JLabel());
        panel.add(registerButton);

        // Dodanie panelu do okna
        add(panel);

        // Widoczność okna
        setVisible(true);
    }

    private boolean checkIfUserExists(String username) {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

            // Używamy PreparedStatement, aby uniknąć ataków SQL Injection
            String query = "SELECT * FROM Users WHERE Login=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);

                // Wykonaj zapytanie
                ResultSet resultSet = preparedStatement.executeQuery();

                // Jeżeli resultSet.next() zwróci true, oznacza to, że istnieje rekord z podanym loginem
                boolean userExists = resultSet.next();

                resultSet.close();
                return userExists;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


// return 0 - błędne hasło
// return 1 - czytelnik
// return 2 - bibliotekarz
private int checkUserPassword(String username, String password) {
    try {
        Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

        // PreparedStatement, by uniknąć ataków SQL Injection
        String query = "SELECT UserPassword, UserRole FROM Users WHERE Login=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            // Wykonaj zapytanie
            ResultSet resultSet = preparedStatement.executeQuery();

            // Sprawdź, czy znaleziono użytkownika
            if (resultSet.next()) {
                String userPassword = resultSet.getString("UserPassword");
                String role = resultSet.getString("UserRole");

                resultSet.close();

                // Tutaj się dzieje jakaś magia szyfrująca od ziomeczka ze stackoverflow
                MessageDigest m = MessageDigest.getInstance("MD5");
                m.reset();
                m.update(password.getBytes());
                byte[] digest = m.digest();
                BigInteger bigInt = new BigInteger(1, digest);
                String hashed = bigInt.toString(16);

                if (Objects.equals(hashed, userPassword)){
                    if(Objects.equals(role, "czytelnik")){
                        return 1;
                    }
                    else if (Objects.equals(role, "bibliotekarz")){
                        return 2;
                    }
                }

            } else {
                System.out.println("Nie znaleziono użytkownika.");
                resultSet.close();
                return 0;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return 0;
    }
}
