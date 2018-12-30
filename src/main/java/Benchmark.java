import benchmark.DatabaseInfo;
import benchmark.DatabaseLocation;

import javax.xml.crypto.Data;
import java.sql.*;

public class Benchmark {

    public static void main(String[] args) {

        final String PASSED_HOST_MOCK = "localhost";
        final String PASSED_PORT_MOCK = "5431";

        DatabaseLocation databaseLocation = new DatabaseLocation(PASSED_HOST_MOCK, PASSED_PORT_MOCK);
        DatabaseInfo databaseInfo = new DatabaseInfo(databaseLocation);

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

            // NOTE: Adding new columns
            Statement statement = con.createStatement();
            final String keyColumnName = "key";
            String insetKeySql = "ALTER TABLE link ADD " + keyColumnName + " VARCHAR(10)";
            statement.execute(insetKeySql);
            System.out.println(keyColumnName + " column has been inserted.");

            final String valueColumnName = "value";
            String insertValueSql = "ALTER TABLE link ADD " + valueColumnName + " VARCHAR(10)";
            statement.execute(insertValueSql);
            System.out.println(valueColumnName + " column has been inserted.");


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
