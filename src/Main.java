import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {

        String jdbcUrl = "jdbc:mysql://localhost/libraryDB";
        String username = "root";
        String password = "root";

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            Statement statement = connection.createStatement();
/*
            // WYBIERZ WSZYSTKIE REKORDY I JE WYŚWIETL
            System.out.println("1.");
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

            // DODAJ NOWY REKORD
//            System.out.println("2.");
//            String queryInsert = "INSERT INTO Osoby (ID, Imię, Nazwisko, Wiek, Narodowość) VALUES (88, 'Jan', 'Kowalski', 30, 'polska')";
//            statement.executeUpdate(queryInsert);

            // PONOWNIE WYŚWIETL WSZYSTKIE REKORDY
            System.out.println("3.");
            ResultSet resultSet2 = statement.executeQuery(query);

            while (resultSet2.next()) {
                int id = resultSet2.getInt("ID");
                String name = resultSet2.getString("Imię");
                String surname = resultSet2.getString("Nazwisko");
                int age = resultSet2.getInt("Wiek");
                String nationality = resultSet2.getString("Narodowość");

                System.out.println("ID: " + id + ", Imię: " + name + ", Nazwisko: " + surname + ", Wiek: " + age + ", Narodowość: " + nationality);
            }

            resultSet.close();
            resultSet2.close();*/
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
