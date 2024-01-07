import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddBookWindow extends JFrame {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final BooksManagementWindow parentWindow;

    public AddBookWindow(String jdbcUrl, String dbUsername, String dbPassword, BooksManagementWindow parentWindow) {
        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.parentWindow = parentWindow;

        JTextField titleField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField publisherField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField amountField = new JTextField();

        setTitle("Dodawanie książki");
        setSize(440, 330);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(8, 2));

        mainPanel.add(new JLabel("Tytuł książki:"));
        mainPanel.add(titleField);

        mainPanel.add(new JLabel("Imię autora:"));
        mainPanel.add(firstNameField);

        mainPanel.add(new JLabel("Nazwisko autora:"));
        mainPanel.add(lastNameField);

        mainPanel.add(new JLabel("Nazwa wydawcy:"));
        mainPanel.add(publisherField);

        mainPanel.add(new JLabel("Rok wydania:"));
        mainPanel.add(yearField);

        mainPanel.add(new JLabel("ISBN:"));
        mainPanel.add(isbnField);

        mainPanel.add(new JLabel("Ilość:"));
        mainPanel.add(amountField);

        JButton cancelButton = new JButton("Anuluj");
        JButton confirmButton = new JButton("Potwierdź");

        mainPanel.add(cancelButton);
        mainPanel.add(confirmButton);

        cancelButton.addActionListener(e -> dispose());
        confirmButton.addActionListener(e -> {
            String title = titleField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String publisher = publisherField.getText();
            String year = yearField.getText();
            String isbn = isbnField.getText();
            String amount = amountField.getText();
            addBookToDatabase(title, lastName, firstName, publisher, Integer.parseInt(year), isbn, "dostępna", amount);
            parentWindow.refreshBooksTable();
        });

        mainPanel.setFocusable(true);
        mainPanel.requestFocusInWindow();

        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addBookToDatabase(String title, String authorLastName, String authorFirstName,
                                   String publisher, int publicationYear, String isbn, String availability, String amount) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "INSERT INTO Books (Title, AuthorLastName, AuthorFirstName, Publisher, " +
                    "PublicationYear, ISBN, BookAvailability, Amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, title);
                statement.setString(2, authorLastName);
                statement.setString(3, authorFirstName);
                statement.setString(4, publisher);
                statement.setInt(5, publicationYear);
                statement.setString(6, isbn);
                statement.setString(7, availability);
                statement.setString(8, amount);

                int affectedRows = statement.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Dodano książkę!", "Potwierdzenie dodania", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Wystąpił błąd podczas dodawania książki.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                JOptionPane.showMessageDialog(this, "Podany ISBN już istnieje w bazie danych.", "Błąd", JOptionPane.ERROR_MESSAGE);
            } else {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Wystąpił błąd podczas łączenia z bazą danych.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
