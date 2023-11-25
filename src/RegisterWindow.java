import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;
import java.sql.Connection;

public class RegisterWindow extends JFrame implements FocusListener, ActionListener {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;

    private final int mainR = 55;
    private final int mainG = 88;
    private final int mainB = 159;

    private MyTextField firstNameField;
    private MyTextField lastNameField;
    private MyTextField addressField;
    private MyTextField phoneField;
    private MyTextField cardNumberField;
    private MyTextField mailField;
    private MyTextField usernameField;

    private MyPasswordField passwordField;

    private MyButton registerButton;

    private JPanel textFieldPanel;

    public RegisterWindow(String jdbcUrl, String dbUsername, String dbPassword) {

        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;

        initComponents();

        // Ustawienia okna
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("System obsługi biblioteki - rejestracja");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Color.DARK_GRAY);
        setVisible(true);
        requestFocusInWindow();

        add(textFieldPanel);
    }

    private void initComponents() {

        textFieldPanel = new JPanel();
        textFieldPanel.setBounds(145, 5, 200, 435);
        textFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        textFieldPanel.setBackground(Color.LIGHT_GRAY);
        textFieldPanel.setOpaque(false);

        // Dodanie pól tekstowych
        firstNameField = new MyTextField("Imię", mainR, mainG, mainB);
        firstNameField.setPreferredSize(new Dimension(200, 35));
        firstNameField.addFocusListener(this);
        textFieldPanel.add(firstNameField);

        lastNameField = new MyTextField("Nazwisko", mainR, mainG, mainB);
        lastNameField.setPreferredSize(new Dimension(200, 35));
        lastNameField.addFocusListener(this);
        textFieldPanel.add(lastNameField);

        addressField = new MyTextField("Adres zamieszkania", mainR, mainG, mainB);
        addressField.setPreferredSize(new Dimension(200, 35));
        addressField.addFocusListener(this);
        textFieldPanel.add(addressField);

        phoneField = new MyTextField("Numer telefonu", mainR, mainG, mainB);
        phoneField.setPreferredSize(new Dimension(200, 35));
        phoneField.addFocusListener(this);
        textFieldPanel.add(phoneField);

        mailField = new MyTextField("Adres e-mail", mainR, mainG, mainB);
        mailField.setPreferredSize(new Dimension(200, 35));
        mailField.addFocusListener(this);
        textFieldPanel.add(mailField);

        cardNumberField = new MyTextField("Numer karty bibliotecznej", mainR, mainG, mainB);
        cardNumberField.setPreferredSize(new Dimension(200, 35));
        cardNumberField.addFocusListener(this);
        textFieldPanel.add(cardNumberField);

        usernameField = new MyTextField("Login", mainR, mainG, mainB);
        usernameField.setPreferredSize(new Dimension(200, 35));
        usernameField.addFocusListener(this);
        textFieldPanel.add(usernameField);

        passwordField = new MyPasswordField("Hasło", mainR, mainG, mainB);
        passwordField.setPreferredSize(new Dimension(200,35));
        passwordField.addFocusListener(this);
        textFieldPanel.add(passwordField);

        // Przycisk odpowiedzialny za rejestrowanie nowego użytkownika
        registerButton = new MyButton("Utwórz konto", mainR, mainG, mainB);
        registerButton.addActionListener(this);
        textFieldPanel.add(registerButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String address = addressField.getText();
            String phone = phoneField.getText();
            String cardNumber = cardNumberField.getText();
            String email = mailField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

                String query = """
                        INSERT INTO Users (FirstName, LastName, Address, PhoneNumber, CardNumber, Email, Login, UserPassword, UserRole)
                        VALUES
                        (?, ?, ?, ?, ?, ?, ?, MD5(?), ?)""";

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

                    int rowsAdded = preparedStatement.executeUpdate();
                    System.out.println(rowsAdded + " wiersz(y) dodano.");

                    JOptionPane.showMessageDialog(RegisterWindow.this, "Zarejestrowano użytkownika, teraz możesz się zalogować", "Koniec rejestracji", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (e.getSource() == firstNameField) {
            if (firstNameField.getText().equals("Imię")) {
                firstNameField.setText("");
            }
        } else if (e.getSource() == lastNameField) {
            if (lastNameField.getText().equals("Nazwisko")) {
                lastNameField.setText("");
            }
        } else if (e.getSource() == addressField) {
            if (addressField.getText().equals("Adres zamieszkania")) {
                addressField.setText("");
            }
        } else if (e.getSource() == phoneField) {
            if (phoneField.getText().equals("Numer telefonu")) {
                phoneField.setText("");
            }
        } else if (e.getSource() == mailField) {
            if (mailField.getText().equals("Adres e-mail")) {
                mailField.setText("");
            }
        } else if (e.getSource() == cardNumberField) {
            if (cardNumberField.getText().equals("Numer karty bibliotecznej")) {
                cardNumberField.setText("");
            }
        } else if (e.getSource() == usernameField) {
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
        if (e.getSource() == firstNameField) {
            if (firstNameField.getText().isEmpty()) {
                firstNameField.setText("Imię");
            }
        } else if (e.getSource() == lastNameField) {
            if (lastNameField.getText().isEmpty()) {
                lastNameField.setText("Nazwisko");
            }
        } else if (e.getSource() == addressField) {
            if (addressField.getText().isEmpty()) {
                addressField.setText("Adres zamieszkania");
            }
        } else if (e.getSource() == phoneField) {
            if (phoneField.getText().isEmpty()) {
                phoneField.setText("Numer telefonu");
            }
        } else if (e.getSource() == mailField) {
            if (mailField.getText().isEmpty()) {
                mailField.setText("Adres e-mail");
            }
        } else if (e.getSource() == cardNumberField) {
            if (cardNumberField.getText().isEmpty()) {
                cardNumberField.setText("Numer karty bibliotecznej");
            }
        } else if (e.getSource() == usernameField) {
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
