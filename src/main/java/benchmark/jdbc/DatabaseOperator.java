package benchmark.jdbc;

import benchmark.database.DatabaseInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


// TODO: Add necessary connection closing
public class DatabaseOperator {

    private final DatabaseInfo databaseInfo;
    private Connection connection;
    private List<String> processingTableColumnNames;

    private boolean hasCreatedCustomDatabase = false;

    // MARK: - Constructor
    public DatabaseOperator(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
        final String SCHEMA_MOCK = "public";
        final String TABLE_NAME = this.databaseInfo.getTargetTable();
        System.out.println("Table name: " + TABLE_NAME);
    }

    public void establishConnection() throws SQLException, JdbcCrudFailureException {

        final String POSTGRES_CLASS_MOCK =  "org.postgresql.Driver";
        // TODO: Replace Postgres to some dynamic class
        try {
            Class.forName( POSTGRES_CLASS_MOCK );
        } catch (ClassNotFoundException error) {
            System.out.println("POSTGRESQL Driver hasn't been found");
        }
        final String databaseURL = databaseInfo.getDatabaseURL();
        System.out.println("Connecting to database: " + databaseURL);
        final String databaseUsername = databaseInfo.getUsername();
        final String databaseUserPassword = databaseInfo.getPassword();
        this.connection = DriverManager.getConnection(databaseURL, databaseUsername, databaseUserPassword);

        final String targetDatabase = this.databaseInfo.getTargetDatabaseName();

        if (!isDatabaseExistInServer(targetDatabase)) {

            try {
                this.createDatabase(targetDatabase);
            } catch (SQLException error) {
                // NOTE: I check if DB exist this way since PostgreSQL returns an empty list of catalogs, ...
                // ... yet still throwing errors when I'm trying to create a DB with an existing name.
                this.hasCreatedCustomDatabase = false;
                System.out.println("Database " + targetDatabase + " exists. Using it for benchmark.");
            }
        } else {
            this.processingTableColumnNames = this.getColumnNames(this.databaseInfo.getTargetTable(), this.databaseInfo.getTargetDatabaseName());
            System.out.println("Table column names: "+ processingTableColumnNames.toString());
        }


    }


    // NOTE: Deleting temprorary created databases and tables if needed.
    public void shutDownConnection() throws SQLException {
        try {

            // NOTE: Deletion of created database
            if (this.hasCreatedCustomDatabase) {
                this.dropDatabase(this.databaseInfo.getTargetDatabaseName());
            }

            // TODO: Add deletion of created tables, etc
        } catch (JdbcCrudFailureException deleteError) {
            System.err.println("An error has occured while deleting: " + deleteError.getMessage());
        }
    }

    private void dropDatabase(String name) throws SQLException, JdbcCrudFailureException {
        String dropDatabaseSQLquery = "DROP DATABASE " + name;
        Statement statement = this.connection.createStatement();
        final int amountOfSuccessOperations = statement.executeUpdate(dropDatabaseSQLquery);
        if (!this.isStatementExcecutionCorrect(amountOfSuccessOperations)) {
            throw new JdbcCrudFailureException("Database " + name + " couldn't be dropped.", CrudOperationType.DELETE);
        }
    }

    private void createDatabase(String name) throws SQLException, JdbcCrudFailureException {
        System.err.println("CREATING DATABASE.....");
        if (!this.isDatabaseElementNameValid(name)) {
            final String nameFormatMisleadingMsg = "Name should contain only latin letters, numbers and an underscore.";
            throw new JdbcCrudFailureException("\"" + name + "\" is not a valid database name." + nameFormatMisleadingMsg, CrudOperationType.CREATE);
        }
        try(Statement statement = this.connection.createStatement()) {
            final String createDatabaseSqlQuery = "CREATE DATABASE IF" + name + "name";
            statement.executeUpdate(createDatabaseSqlQuery);

        }
        this.hasCreatedCustomDatabase = true;
    }

    private boolean isDatabaseElementNameValid(String name) {
        final String avaliableCharactersInNameRegEx = "^[a-zA-Z_][a-zA-Z0-9_]*$";
        return name.matches(avaliableCharactersInNameRegEx);
    }


    // NOTE: This method doesn't work with PostgreSQL: it returns an empty list of catalogs, ...
    // ... but throwing errors when I try to create DB with an existing name
    private boolean isDatabaseExistInServer(String databaseName) throws SQLException {
        if (this.connection == null) {
            System.err.println("Unable to fetch catalog: connection hasn't been established.");
            return false;
        }
        boolean isDatabaseExist = false;

        try(ResultSet resultSet = this.connection.getMetaData().getCatalogs()) {
            while (resultSet.next()) {
                final int databaseNameIndex = 1;
                final String processingDatabaseName = resultSet.getString(databaseNameIndex);
                isDatabaseExist = (processingDatabaseName.equals(databaseName));
            }
        }
        return isDatabaseExist;
    }


    // TODO: Move it onto a separate object may be?
    private List<String> getColumnNames (String tableName, String schemaName) throws SQLException {

        ResultSet resultSet = null;

        ResultSetMetaData resultSetMetaData = null;
        PreparedStatement preparedStatement = null;
        List<String> columnNames = new ArrayList<String>();
        String qualifiedSchemaName = this.databaseInfo.getTargetDatabaseName(); // TEST: this.getQualifiedSchemaName(schemaName, tableName);
        try {
            preparedStatement = this.connection.prepareStatement("select * from " + qualifiedSchemaName + " where 0=1");
            //NOTE: we're getting empty result set, yet meta data would still be avaliable
            resultSet = preparedStatement.executeQuery();
            resultSetMetaData = resultSet.getMetaData();
            for(int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                columnNames.add(resultSetMetaData.getColumnLabel(i));
            }
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
            if (preparedStatement != null)
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


//    private boolean isDatabaseExist(String name) throws SQLException {
//        System.out.println("Finding name: " + name + ", is connection null: " + this.connection.getCatalog());
//        ResultSet resultSet = this.connection.getMetaData().getCatalogs();
//
//        //iterate each catalog in the ResultSet
//        boolean isDatabaseExist = false;
//        while (resultSet.next()) {
//            System.out.println("TABLE_CAT = " + resultSet.getString("key") );
//
////            // Get the database name, which is at position 1
////            final int databaseNamePosition = 1;
////
////            String databaseName = resultSet.getString(1);
////            System.out.println("databaseName: " + databaseName);
////            if (databaseName != null) {
////                isDatabaseExist = true;
////            }
//        }
//        resultSet.close();
//        return isDatabaseExist;
//    }

    private boolean isDBexistMetdatata(String name) throws SQLException {
        DatabaseMetaData md = this.connection.getMetaData();
        ResultSet rs = md.getTables(null, null, "table_name", null);
        if (rs.next()) {
            System.out.println("exist?");
            //Table Exist
        }
        return true;
    }


    public void createColumn(final String tableName, final String columnName, final String type) throws SQLException, JdbcCrudFailureException {
        if (this.connection == null) {
            final String misleadingMsg = "Connection to required database hasn't been extablishes.";
            throw new IllegalArgumentException(misleadingMsg);
        }
        if (!this.isDatabaseElementNameValid(columnName)) {
            final String nameFormatMisleadingMsg = "Name should contain only latin letters, numbers and an underscore.";
            throw new JdbcCrudFailureException("\"" + columnName + "\" is not a valid database name." + nameFormatMisleadingMsg, CrudOperationType.CREATE);
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
        if (!isColumnExistInCurrentDB(column)) {
            final String misleadingMsg = "Column " + column + " doesn't exist in database " + this.databaseInfo.getTargetDatabaseName();
            throw new IllegalArgumentException(misleadingMsg);
        }

        final String targetTable = this.databaseInfo.getTargetTable();
        // TODO: Repalce to string formatter
        
        String insertSQLstatement = "INSERT INTO public." + targetTable + "(" + column + ") VALUES ('" + value + "')";
        PreparedStatement preparedStatement = this.connection.prepareStatement(insertSQLstatement);
        // NOTE: (JavaDoc) either (1) the row count for SQL Data Manipulation Language (DML) statements or ...
        // ... (2) 0 for SQL statements that return nothing.
        preparedStatement.executeUpdate();


    }

    private boolean isStatementExcecutionCorrect(int amountOfOperations) {
        return (amountOfOperations > 0);
    }


    private Boolean isColumnExistInCurrentDB(final String column) {
        return this.processingTableColumnNames.contains(column);
    }

}
