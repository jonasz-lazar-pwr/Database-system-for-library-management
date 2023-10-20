import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost/testDB";
        String username = "root";
        String password = "root!!!!";

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM Osoby";

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String name = resultSet.getString("Imię");
                String surname = resultSet.getString("Nazwisko");
                int age = resultSet.getInt("Wiek");
                String nationality = resultSet.getString("Narodowość");

                System.out.println("ID: " + id + ", Imię: " + name + ", Nazwisko: " + surname + ", Wiek: " + age + ", Narodowość: " + nationality);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
