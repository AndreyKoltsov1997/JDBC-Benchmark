package benchmark;

import benchmark.database.components.DatabaseCredentials;
import benchmark.database.DatabaseInfo;
import benchmark.database.components.DatabaseLocation;
import benchmark.files.InsertionFileLogger;

import java.io.IOException;
import java.sql.*;

public class Benchmark {



    public static void main(String[] args) {


        final String FILE_NAME_MOCK = "results.csv";

        final String PASSED_HOST_MOCK = "localhost";
        final String PASSED_PORT_MOCK = "5431";

        DatabaseLocation databaseLocation = new DatabaseLocation(PASSED_HOST_MOCK, PASSED_PORT_MOCK);

        final String USERNAME_MOCK = "Andrey";
        final String USER_PASSWORD_MOCK = "qwerty";
        DatabaseCredentials databaseCredentials = null;
        try {
            databaseCredentials = new DatabaseCredentials(USERNAME_MOCK, USER_PASSWORD_MOCK);
        } catch (IllegalArgumentException error) {
            System.err.println("An error has occured while parsing user credentials: " + error.getMessage());
            System.exit(Constants.STATUS_INVALID_ARGUMENT);
        }

        final String DATABASE_NAME_MOCK = "test";

        DatabaseInfo databaseInfo = new DatabaseInfo(databaseLocation, databaseCredentials, DATABASE_NAME_MOCK);

        System.out.println("Database URL: " + databaseInfo.getDatabaseURL());
        final int AMOUNT_OF_THREADS_MOCK = 5;
        final int PAYLOAD_MOCK = 10;
        final int AMOUNT_OF_INSERTIONS_MOCK = 100;
        DatabaseBenchmark databaseBenchmark = new DatabaseBenchmark(PAYLOAD_MOCK, AMOUNT_OF_THREADS_MOCK, AMOUNT_OF_INSERTIONS_MOCK, databaseInfo, FILE_NAME_MOCK);

        databaseBenchmark.performBenchmark();
//
//        try {
//
//            try {
//                Class.forName( "org.postgresql.Driver" );
//            } catch( ClassNotFoundException e ) {
//                //my class isn't there!
//                System.out.println("Driver hasn't been found");
//            }
//
//
//            Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5431/", "Andrey", "qwerty");
//            PreparedStatement preparedStatement = con.prepareStatement("select * from link");
//            ResultSet resultSet = preparedStatement.executeQuery();
//
////            // NOTE: Adding new columns
////            Statement statement = con.createStatement();
////            final String keyColumnName = "key";
////            String insetKeySql = "ALTER TABLE link ADD " + keyColumnName + " VARCHAR(10)";
////            statement.execute(insetKeySql);
////            System.out.println(keyColumnName + " column has been inserted.");
////
////            final String valueColumnName = "value";
////            String insertValueSql = "ALTER TABLE link ADD " + valueColumnName + " VARCHAR(10)";
////            statement.execute(insertValueSql);
////            System.out.println(valueColumnName + " column has been inserted.");
//
//
//            // NOTE: Getting info
//            while (resultSet.next()) {
////                System.out.println("next row");
//                System.out.println("Key:" + resultSet.getString("key"));
//                System.out.println("value:" + resultSet.getString("value"));
//
//            }
//        } catch (Exception error) {
//            System.err.println("An error has occured: " + error.getMessage());
//        }
        System.out.println("benchmark.Benchmark has been created.");
    }
}
