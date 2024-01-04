import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveBookWindow extends JFrame {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;

    public RemoveBookWindow(String jdbcUrl, String dbUsername, String dbPassword) {
        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;

        JTextField isbnField = new JTextField();

        setTitle("Usuwanie książki");
        setSize(400, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 2));

        mainPanel.add(new JLabel("ISBN książki do usunięcia:"));
        mainPanel.add(isbnField);

        JButton cancelButton = new JButton("Anuluj");
        JButton confirmButton = new JButton("Potwierdź");

        mainPanel.add(cancelButton);
        mainPanel.add(confirmButton);

        cancelButton.addActionListener(e -> dispose());
        confirmButton.addActionListener(e -> {
            String isbn = isbnField.getText();
            removeBookFromDatabase(isbn);
        });

        mainPanel.setFocusable(true);
        mainPanel.requestFocusInWindow();

        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void removeBookFromDatabase(String isbn) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "DELETE FROM Books WHERE ISBN = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, isbn);

                int affectedRows = statement.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Usunięto książkę!", "Potwierdzenie usunięcia", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Nie znaleziono książki o podanym ISBN.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
