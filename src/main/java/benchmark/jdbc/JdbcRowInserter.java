package benchmark.jdbc;

import benchmark.Constants;
import benchmark.database.DatabaseInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


// TODO: Add necessary connection closing
public class JdbcRowInserter {
    private final DatabaseInfo databaseInfo;
    private Connection connection;

    // MARK: - Constructor
    public JdbcRowInserter(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
        try {
            this.establishConnection();
            final String SCHEMA_MOCK = "public";
            final String TABLE_NAME = this.databaseInfo.getTargetTable();
            System.out.println("Table name: " + TABLE_NAME);
            List<String> columnNames = this.getColumnNames(TABLE_NAME, SCHEMA_MOCK);
            for (String name : columnNames) {
                System.out.println("Column exist: " + name);
            }
        } catch (SQLException error) {
            System.out.println("Unable to establish conection with database.");
            System.exit(Constants.CONNECTION_ERROR);
        }
    }

    private void establishConnection() throws SQLException {
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

    }

    private List<String> getColumnNames (String tableName, String schemaName) throws SQLException {

        ResultSet resultSet = null;

        ResultSetMetaData resultSetMetaData = null;
        PreparedStatement preparedStatement = null;
        List<String> columnNames = new ArrayList<String>();
        String qualifiedSchemaName = this.getQualifiedSchemaName(schemaName, tableName);
        try{
            preparedStatement=this.connection.prepareStatement("select * from " + qualifiedSchemaName + " where 0=1");
            //NOTE: we're getting empty result set, yet meta data would still be avaliable
            resultSet=preparedStatement.executeQuery();
            resultSetMetaData=resultSet.getMetaData();
            for(int i=1;i<=resultSetMetaData.getColumnCount();i++)
                columnNames.add(resultSetMetaData.getColumnLabel(i));
        } catch(SQLException error) {
            final String misleadingMsg = "An error has occured while fetching metadata from " + tableName + ". Reason: " + error.getMessage();
            throw new SQLException(misleadingMsg);
        }
        finally {
            if (resultSet != null)
                try {
                    resultSet.close();
                } catch (SQLException error) {
                    throw error;
                }
            if(preparedStatement != null)
                try {
                    preparedStatement.close();
                } catch (SQLException error) {
                    throw error;
                }
        }
        return columnNames;
    }

    private final String getQualifiedSchemaName(final String targetSchema, final String targetTable) {
        return (targetSchema!=null && !targetSchema.isEmpty()) ? (targetSchema + "." + targetTable) : targetTable;
    }


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


    public void createColumn(final String tableName, final String columnName, final String type) throws SQLException {
        if (this.connection == null) {
            final String misleadingMsg = "Connection to required database hasn't been extablishes.";
            throw new IllegalArgumentException(misleadingMsg);
        }
        // NOTE: Adding new columns
        Statement statement = this.connection.createStatement();
        final String keyColumnName = "key";
        String insetKeySql = "ALTER TABLE " + tableName + " ADD " + keyColumnName + " " + type;
        statement.execute(insetKeySql);
        System.out.println(keyColumnName + " column has been inserted.");
    }


    public void insertValueIntoColumn(final String column, final String value) throws SQLException, IllegalArgumentException {
        if (this.connection == null) {
            final String misleadingMsg = "Connection to required database hasn't been extablishes.";
            throw new IllegalArgumentException(misleadingMsg);
        }
        // NOTE: Adding new columns
        Statement statement = this.connection.createStatement();
        final String keyColumnName = "key";
        String insetKeySql = "ALTER TABLE link ADD " + keyColumnName + " VARCHAR(10)";
        statement.execute(insetKeySql);
        System.out.println(keyColumnName + " column has been inserted.");

        final String valueColumnName = "value";
        String insertValueSql = "ALTER TABLE link ADD " + valueColumnName + " VARCHAR(10)";
        statement.execute(insertValueSql);
        System.out.println(valueColumnName + " column has been inserted.");

    }






}
