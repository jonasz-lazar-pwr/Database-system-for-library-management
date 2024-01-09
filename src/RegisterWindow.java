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
    private JPanel buttonsPanel;

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
        add(buttonsPanel);
    }

    private void initComponents() {

        textFieldPanel = new JPanel();
        textFieldPanel.setBounds(145, 15, 200, 365);
        textFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        textFieldPanel.setBackground(Color.LIGHT_GRAY);
        textFieldPanel.setOpaque(false);

        // Dodanie pól tekstowych
        firstNameField = new MyTextField("Imię");
        firstNameField.setPreferredSize(new Dimension(200, 35));
        firstNameField.addFocusListener(this);
        textFieldPanel.add(firstNameField);

        lastNameField = new MyTextField("Nazwisko");
        lastNameField.setPreferredSize(new Dimension(200, 35));
        lastNameField.addFocusListener(this);
        textFieldPanel.add(lastNameField);

        addressField = new MyTextField("Adres zamieszkania");
        addressField.setPreferredSize(new Dimension(200, 35));
        addressField.addFocusListener(this);
        textFieldPanel.add(addressField);

        phoneField = new MyTextField("Numer telefonu");
        phoneField.setPreferredSize(new Dimension(200, 35));
        phoneField.addFocusListener(this);
        textFieldPanel.add(phoneField);

        mailField = new MyTextField("Adres e-mail");
        mailField.setPreferredSize(new Dimension(200, 35));
        mailField.addFocusListener(this);
        textFieldPanel.add(mailField);

        cardNumberField = new MyTextField("Numer karty bibliotecznej");
        cardNumberField.setPreferredSize(new Dimension(200, 35));
        cardNumberField.addFocusListener(this);
        textFieldPanel.add(cardNumberField);

        usernameField = new MyTextField("Login");
        usernameField.setPreferredSize(new Dimension(200, 35));
        usernameField.addFocusListener(this);
        textFieldPanel.add(usernameField);

        passwordField = new MyPasswordField("Hasło");
        passwordField.setPreferredSize(new Dimension(200,35));
        passwordField.addFocusListener(this);
        textFieldPanel.add(passwordField);

        buttonsPanel = new JPanel();
        buttonsPanel.setBounds(80, 380, 340, 65);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
        buttonsPanel.setBackground(Color.LIGHT_GRAY);
        buttonsPanel.setOpaque(false);

        MyButton cancelButton = new MyButton("Anuluj");
        cancelButton.setPreferredSize(new Dimension(150, 45));

        cancelButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new LoginWindow(jdbcUrl, dbUsername, dbPassword));
            dispose();
        });
        buttonsPanel.add(cancelButton);

        // Przycisk odpowiedzialny za rejestrowanie nowego użytkownika
        registerButton = new MyButton("Utwórz konto");
        registerButton.setPreferredSize(new Dimension(150, 45));
        registerButton.addActionListener(this);
        buttonsPanel.add(registerButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            String validationMessage = validateFields();
            if (validationMessage.isEmpty()) {
                // Walidacja pól zakończyła się sukcesem
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

                    String query = "INSERT INTO Users (FirstName, LastName, Address, PhoneNumber, CardNumber, Email, Login, UserPassword, UserRole) VALUES (?, ?, ?, ?, ?, ?, ?, MD5(?), ?)";

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
                        SwingUtilities.invokeLater(() -> new LoginWindow(jdbcUrl, dbUsername, dbPassword));
                        dispose();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Wystąpił błąd podczas rejestracji użytkownika.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Wyświetl komunikat o błędzie walidacji pól
                JOptionPane.showMessageDialog(this, validationMessage, "Błąd walidacji", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String validateFields() {
        StringBuilder validationMessage = new StringBuilder();

        if (validateNonDefault(firstNameField, "Imię")) {
            validationMessage.append("Pole Imię nie może pozostać puste.\n");
        } else if (!validateName(firstNameField.getText()) || containsSpaces(firstNameField.getText())) {
            validationMessage.append("Błędne imię. Imię powinno zaczynać się z dużej litery i składać z liter. Nie może zawierać spacji na początku lub na końcu.\n");
        }

        if (validateNonDefault(lastNameField, "Nazwisko")) {
            validationMessage.append("Pole Nazwisko nie może pozostać puste.\n");
        } else if (!validateName(lastNameField.getText()) || containsSpaces(lastNameField.getText())) {
            validationMessage.append("Błędne nazwisko. Nazwisko powinno zaczynać się z dużej litery i składać z liter. Nie może zawierać spacji na początku lub na końcu.\n");
        }

        if (validateNonDefault(addressField, "Adres zamieszkania")) {
            validationMessage.append("Pole Adres zamieszkania nie może pozostać puste.\n");
        } else if (!validateAddress(addressField.getText()) || containsSpaces(addressField.getText())) {
            validationMessage.append("Błędny adres zamieszkania. Poprawny format: ul./al./pl./os. nazwa numer, miasto. Nie może zawierać spacji na początku lub na końcu.\n");
        }

        if (validateNonDefault(phoneField, "Numer telefonu")) {
            validationMessage.append("Pole Numer telefonu nie może pozostać puste.\n");
        } else if (!validatePhone(phoneField.getText()) || containsSpaces(phoneField.getText())) {
            validationMessage.append("Błędny numer telefonu. Poprawny format: +48 xxx xxx xxx. Nie może zawierać spacji na początku lub na końcu.\n");
        }

        if (validateNonDefault(mailField, "Adres e-mail")) {
            validationMessage.append("Pole Adres e-mail nie może pozostać puste.\n");
        } else if (!validateEmail(mailField.getText()) || containsSpaces(mailField.getText())) {
            validationMessage.append("Błędny adres email. Poprawny format: user@example.com. Nie może zawierać spacji na początku lub na końcu.\n");
        }

        if (validateNonDefault(cardNumberField, "Numer karty bibliotecznej")) {
            validationMessage.append("Pole Numer karty bibliotecznej nie może pozostać puste.\n");
        } else if (!validateCardNumber(cardNumberField.getText()) || containsSpaces(cardNumberField.getText())) {
            validationMessage.append("Błędny numer karty bibliotecznej. Poprawny format: Axxxxx. Nie może zawierać spacji na początku lub na końcu.\n");
        }

        if (validateNonDefault(usernameField, "Login")) {
            validationMessage.append("Pole Login nie może pozostać puste.\n");
        } else if (!validateUsername(usernameField.getText()) || containsSpaces(usernameField.getText())) {
            validationMessage.append("Błędny login. Login nie może zawierać spacji i musi mieć od 4 do 20 znaków. Nie może zawierać spacji na początku lub na końcu.\n");
        }

        if (validateNonDefault(passwordField, "Hasło")) {
            validationMessage.append("Pole Hasło nie może pozostać puste.\n");
        } else if (!validatePassword(new String(passwordField.getPassword())) || containsSpaces(new String(passwordField.getPassword()))) {
            validationMessage.append("Błędne hasło. Hasło musi zawierać minimum 8 znaków, przynajmniej jedną cyfrę, jedną małą literę, jedną dużą literę, jeden znak specjalny oraz brak spacji.\n");
        }

        return validationMessage.toString();
    }

    private boolean containsSpaces(String text) {
        return text.trim().length() != text.length();
    }

    private boolean validateNonDefault(JTextField field, String defaultValue) {
        return field.getText().trim().equals(defaultValue);
    }

    private boolean validateName(String name) {
        return name.matches("[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]+");
    }

    private boolean validateAddress(String address) {
        return address.matches("^(ul\\.|al\\.|pl\\.|os\\.) [A-ZĄĆĘŁŃÓŚŹŻa-ząćęłńóśźż]+( \\d+[a-z]*)*, [A-ZĄĆĘŁŃÓŚŹŻa-ząćęłńóśźż]+");
    }

    private boolean validatePhone(String phone) {
        return phone.matches("\\+48 \\d{3} \\d{3} \\d{3}");
    }

    private boolean validateEmail(String email) {
        return email.matches(".*@.*\\..*");
    }

    private boolean validateCardNumber(String cardNumber) {
        return cardNumber.matches("[A-Z]\\d{5}");
    }

    private boolean validateUsername(String username) {
        return !username.contains(" ") && username.length() >= 4 && username.length() <= 20;
    }

    private boolean validatePassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");
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
