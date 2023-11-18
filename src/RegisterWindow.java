import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterWindow extends JFrame {
    private String jdbcUrl;
    private String dbUsername;
    private String dbPassword;
    private JTextField firstNameField, lastNameField, addressField, phoneField, cardNumberField, emailField, usernameField;
    private JPasswordField passwordField;

    public RegisterWindow(String jdbcUrl, String dbUsername, String dbPassword) {
        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;

        setTitle("Książkowość - rejestracja");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(9, 2, 10, 10));

        // Dodaj etykiety i pola tekstowe
        panel.add(new JLabel("Imię:"));
        firstNameField = new JTextField();
        panel.add(firstNameField);

        panel.add(new JLabel("Nazwisko:"));
        lastNameField = new JTextField();
        panel.add(lastNameField);

        panel.add(new JLabel("Adres:"));
        addressField = new JTextField();
        panel.add(addressField);

        panel.add(new JLabel("Telefon:"));
        phoneField = new JTextField();
        panel.add(phoneField);

        panel.add(new JLabel("Numer karty:"));
        cardNumberField = new JTextField();
        panel.add(cardNumberField);

        panel.add(new JLabel("E-mail:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Login:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Hasło:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton registerButton = new JButton("Zarejestruj się");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Tutaj możesz dodać kod obsługujący proces rejestracji
                // Pobierz dane z pól tekstowych
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String address = addressField.getText();
                String phone = phoneField.getText();
                String cardNumber = cardNumberField.getText();
                String email = emailField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                try {
                    Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

                    // Używamy PreparedStatement, aby uniknąć ataków SQL Injection
                    String query = "INSERT INTO Users (FirstName, LastName, Address, PhoneNumber, CardNumber, Email, Login, UserPassword, UserRole)\n" +
                            "VALUES\n" +
                            "    (?, ?, ?, ?, ?, ?, ?, MD5(?), ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, firstName);
                        preparedStatement.setString(2, lastName);
                        preparedStatement.setString(3, address);
                        preparedStatement.setString(4, phone);
                        preparedStatement.setString(5, cardNumber);
                        preparedStatement.setString(6, email);
                        preparedStatement.setString(7, username);
                        preparedStatement.setString(8, password);
                        preparedStatement.setString(9, "czytelnik");

                        // Wykonaj zapytanie
                        int rowsAdded = preparedStatement.executeUpdate();
                        System.out.println(rowsAdded + " wiersz(y) dodano.");

                        JOptionPane.showMessageDialog(RegisterWindow.this, "Zarejestrowano użytkownika, teraz możesz się zalogować", "Koniec rejestracji", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        panel.add(new JLabel());
        panel.add(registerButton);

        add(panel);

        setVisible(true);
    }
}
