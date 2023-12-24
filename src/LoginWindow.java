import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.sql.Connection;
import java.util.Objects;

public class LoginWindow extends JFrame implements FocusListener {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;

    private final int mainR;
    private final int mainG;
    private final int mainB;

    private MyTextField usernameField;
    private MyPasswordField passwordField;

    private JPanel textFieldPanel;
    private JPanel buttonPanel;


    public LoginWindow(String jdbcUrl, String dbUsername, String dbPassword, int mainR, int mainG, int mainB) {
        // Ustawienia połączenia z bazą danych
        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;

        this.mainR = mainR;
        this.mainG = mainG;
        this.mainB = mainB;

        initComponents();

        // Ustawienia okna
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("System obsługi biblioteki - logowanie");
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

        // Panel z polami tekstowymi
        textFieldPanel = new JPanel();
        textFieldPanel.setBounds(145, 100, 200, 125);
        textFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
        textFieldPanel.setBackground(Color.LIGHT_GRAY);
        textFieldPanel.setOpaque(false);

        // Pole tekstowe przyjmujące login użytkownika
        usernameField = new MyTextField("Login", mainR, mainG, mainB);
        usernameField.addFocusListener(this);
        textFieldPanel.add(usernameField);

        // Pole tekstowe przyjmujące hasło użytkownika
        passwordField = new MyPasswordField("Hasło", mainR, mainG, mainB);
        passwordField.addFocusListener(this);
        textFieldPanel.add(passwordField);

        // Panel z przyciskami
        buttonPanel = new JPanel();
        buttonPanel.setBounds(145, 225, 200, 125);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
        buttonPanel.setBackground(Color.GRAY);
        buttonPanel.setOpaque(false);

        // Przycisk odpowiedzialny za logowanie
        MyButton loginButton = getMyButton();
        buttonPanel.add(loginButton);

        // Przycisk odpowiedzialny za rejestrowanie nowego użytkownika
        MyButton registerButton = new MyButton("Zarejestruj się", mainR, mainG, mainB);

        registerButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new RegisterWindow(jdbcUrl, dbUsername, dbPassword));
        });
        buttonPanel.add(registerButton);
    }

    private MyButton getMyButton() {
        MyButton loginButton = new MyButton("Zaloguj się", mainR, mainG, mainB);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);

            System.out.println("Login: " + username);
            System.out.println("Hasło: " + password);

            // Logowanie
            if (checkIfUserExists(username)){
                int check = checkUserPassword(username, password);
                if (check==1){
                    System.out.println("Zalogowano jako czytelnik");
                    dispose();
                    SwingUtilities.invokeLater(() -> {
                        UserWindow userWindow = new UserWindow(jdbcUrl, dbUsername, dbPassword, username);
                    });
                    dispose();
                } else if (check == 2) {
                    System.out.println("Zalogowano jako bibliotekarz");
                    dispose();
                    SwingUtilities.invokeLater(AdminWindow::new);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginWindow.this,
                            "Niepoprawne hasło!",
                            "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
            else {
                JOptionPane.showMessageDialog(LoginWindow.this,
                        "Nieprawidłowe dane logowania!",
                        "Błąd", JOptionPane.ERROR_MESSAGE);
            }

        });
        return loginButton;
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
            String query = "SELECT UserPassword, UserRole FROM Users WHERE Login = ?";

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

    @Override
    public void focusGained(FocusEvent e) {
        if (e.getSource() == usernameField) {
            if (usernameField.getText().equals("Login")) {
                usernameField.setText("");
            }
        } else if (e.getSource() == passwordField) {
            String passwordText = new String(passwordField.getPassword());

            if (passwordText.equals("Hasło")) {
                passwordField.setEchoChar('\u2022');
                passwordField.setText("");
            }
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (e.getSource() == usernameField) {
            if (usernameField.getText().isEmpty()) {
                usernameField.setText("Login");
            }
        } else if (e.getSource() == passwordField) {
            String passwordText = new String(passwordField.getPassword());

            if (passwordText.isEmpty()) {
                passwordField.setEchoChar((char) 0);
                passwordField.setText("Hasło");
            }
        }
    }
}
