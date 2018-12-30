import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Benchmark {

    public static void main(String[] args) {
        try {

            try {
                Class.forName( "org.postgresql.Driver" );
            } catch( ClassNotFoundException e ) {
                //my class isn't there!
                System.out.println("Driver hasn't been found");
            }


            Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5431/", "Andrey", "qwerty");
            PreparedStatement preparedStatement = con.prepareStatement("select * from link");
            ResultSet resultSet = preparedStatement.executeQuery();

            // NOTE: Getting info
            while (resultSet.next()) {
                System.out.println(resultSet.getString(2));
            }
        } catch (Exception error) {
            System.err.println("An error has occured: " + error.getMessage());
        }
        System.out.println("Benchmark has been created.");
    }
}
