package benchmark.jdbc;

import benchmark.Constants;
import benchmark.database.DatabaseInfo;

import java.sql.*;


// TODO: Add necessary connection closing
public class JdbcRowInserter {
    private final DatabaseInfo databaseInfo;
    private Connection connection;

    // MARK: - Constructor
    public JdbcRowInserter(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
        try {
            this.establishConnectionTest();
        } catch (SQLException error) {
            System.out.println("Unable to establish conection with database.");
            System.exit(Constants.CONNECTION_ERROR);
        }
    }

    private void establishConnectionTest() throws SQLException {
        final String POSTGRES_CLASS_MOCK =  "org.postgresql.Driver";
        // TODO: Replace Postgres to some dynamic class
        try {
            Class.forName( POSTGRES_CLASS_MOCK );
        } catch (ClassNotFoundException error) {
            System.out.println("POSTGRESQL Driver hasn't been found");
        }
        final String databaseURL = databaseInfo.getDatabaseURL();
        final String databaseUsername = databaseInfo.getUsername();
        final String databaseUserPassword = databaseInfo.getPassword();
        this.connection = DriverManager.getConnection(databaseURL, databaseUsername, databaseUserPassword);
//        this.isDatabaseExist(databaseInfo.getName());
        this.isDBexistMetdatata(databaseInfo.getName());

    }

//    private Boolean isConnectionCorrect() {
//
//
//
//    }

    private boolean isDatabaseExist(String name) throws SQLException {
        System.out.println("Finding name: " + name + ", is connection null: " + this.connection.getCatalog());
        ResultSet resultSet = this.connection.getMetaData().getCatalogs();

        //iterate each catalog in the ResultSet
        boolean isDatabaseExist = false;
        while (resultSet.next()) {
            System.out.println("TABLE_CAT = " + resultSet.getString("key") );

//            // Get the database name, which is at position 1
//            final int databaseNamePosition = 1;
//
//            String databaseName = resultSet.getString(1);
//            System.out.println("databaseName: " + databaseName);
//            if (databaseName != null) {
//                isDatabaseExist = true;
//            }
        }
        resultSet.close();
        return isDatabaseExist;
    }

    private boolean isDBexistMetdatata(String name) throws SQLException {
        DatabaseMetaData md = this.connection.getMetaData();
        ResultSet rs = md.getTables(null, null, "table_name", null);
        if (rs.next()) {
            System.out.println("exist?");
            //Table Exist
        }
        return true;
    }

//    public Boolean insertKeyValue() throws SQLException {
//        // NOTE: Adding new columns
//        Statement statement = this.connection.createStatement();
//        final String keyColumnName = "key";
//        String insetKeySql = "ALTER TABLE link ADD " + keyColumnName + " VARCHAR(10)";
//        statement.execute(insetKeySql);
//        System.out.println(keyColumnName + " column has been inserted.");
//
//        final String valueColumnName = "value";
//        String insertValueSql = "ALTER TABLE link ADD " + valueColumnName + " VARCHAR(10)";
//        statement.execute(insertValueSql);
//        System.out.println(valueColumnName + " column has been inserted.");
//
//
////        // NOTE: Getting info
////        while (resultSet.next()) {
////            System.out.println(resultSet.getString(2));
////        }
//    }

    public void establishConnection() {
        try {

            try {
                Class.forName( "org.postgresql.Driver" );
            } catch (ClassNotFoundException error) {
                //my class isn't there!
                System.out.println("POSTGRESQL Driver hasn't been found");
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
    }




}
