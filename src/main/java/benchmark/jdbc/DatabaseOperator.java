package benchmark.jdbc;

import benchmark.Constants;
import benchmark.database.DatabaseInfo;
import benchmark.jdbc.common.DatabaseElementCreator;
import benchmark.jdbc.common.DatabaseElementEraser;
import benchmark.jdbc.common.DatabaseElementInserter;
import benchmark.jdbc.common.DatabaseElementValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DatabaseOperator {

    private final static String POSTGRES_DRIVER_CLASS_NAME = "org.postgresql.Driver";
    private final DatabaseInfo databaseInfo;
    private Connection connection;

    private DatabaseElementValidator databaseElementValidator;
    private DatabaseElementCreator databaseElementCreator;
    private DatabaseElementEraser databaseElementEraser;
    private DatabaseElementInserter databaseElementInserter;

    private static List<String> processingTableColumnNames = new ArrayList<String>() {{
        add(Constants.KEY_COLUMN_NAME);
        add(Constants.VALUE_COLUMN_NAME);
    }};

    private boolean hasCreatedCustomDatabase = false;

    // MARK: - Constructor
    public DatabaseOperator(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
    }

    public void establishConnection() throws SQLException {

        // NOTE: Connection to PostgreSQL Database
        try {
            Class.forName(DatabaseOperator.POSTGRES_DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException error) {
            System.err.println("PostgreSQL Driver hasn't been found");
        }
        final String databaseURL = databaseInfo.getDatabaseURL();
        final String databaseUsername = databaseInfo.getUsername();
        final String databaseUserPassword = databaseInfo.getPassword();
        this.connection = DriverManager.getConnection(databaseURL, databaseUsername, databaseUserPassword);

        // NOTE: Creating DB element validator if connection has been established.
        this.createDatabaseSupportingObjects();

        try {
            this.createMissingDatabaseElements();

        } catch (JdbcCrudFailureException error) {
            System.err.println("Unable to create required DB element, reason: " + error.getMessage());
            System.exit(Constants.CONNECTION_ERROR);
        }
    }

    // NOTE: Creating objects for CRUD operations if connection has been established.
    private void createDatabaseSupportingObjects() {
        if (this.connection == null) {
            throw new IllegalArgumentException("Connection hasn't been established. Unable to create supporting objects.");
        }
        this.databaseElementValidator = new DatabaseElementValidator(this.connection);
        this.databaseElementCreator = new DatabaseElementCreator(this.connection, databaseElementValidator);
        this.databaseElementEraser = new DatabaseElementEraser(this.connection, databaseElementValidator);
        this.databaseElementInserter = new DatabaseElementInserter(this.connection, databaseElementValidator);
    }

    // NOTE: Creating database elements (catalog, table, column) if necessary
    private void createMissingDatabaseElements() throws JdbcCrudFailureException {
        final String targetDatabase = this.databaseInfo.getTargetDatabaseName();
        try {
            this.databaseElementCreator.createDatabase(targetDatabase);
            this.hasCreatedCustomDatabase = true;
        } catch (SQLException error) {
            // NOTE:  Going into this block means database is already exist...
            // ... I check if DB exist this way since PostgreSQL returns an empty list of catalogs, ...
            // ... yet still throwing errors when I'm trying to create a DB with an existing name.
            this.hasCreatedCustomDatabase = false;
        }

        final String targetTableName = this.databaseInfo.getTargetTable();
        try {
            this.databaseElementCreator.createTable(targetTableName);
        } catch (SQLException error) {
            // NOTE: I check if DB exist this way since PostgreSQL returns an empty list of catalogs, ...
            // ... yet still throwing errors when I'm trying to create a DB with an existing name.
            this.hasCreatedCustomDatabase = false;
            System.out.println("Table " + targetTableName + " exists. Using it for benchmark.");
        }


        final String varcharType = "VARCHAR(10)";
        String processingColumnName = "";
        try {
            for (String columnName : DatabaseOperator.processingTableColumnNames) {
                processingColumnName = columnName;
                this.databaseElementCreator.createColumnIfNotExists(this.databaseInfo.getTargetTable(), columnName, varcharType);
            }
        } catch (SQLException error) {
            System.out.println("Column " + processingColumnName + " exists. Using it for benchmark.");
        } catch (JdbcCrudFailureException error) {
            System.err.println("Unable to create column \"" + processingColumnName + "\". Reason: " + error.getMessage());
        }
    }


    // NOTE: Closing the established connection and deleting temporary created databases and tables if needed.
    public void shutDownConnection() throws SQLException {
        if (this.connection == null) {
            return;
        }
        try {
            // NOTE: Deletion of created database
            if (this.hasCreatedCustomDatabase) {
                this.databaseElementEraser.dropDatabase(this.databaseInfo.getTargetDatabaseName());
                return;
            }
            // TODO: Add deletion of created tables

            // NOTE: Deletion of created columns
            for (String createdColumn : DatabaseOperator.processingTableColumnNames) {
                this.databaseElementEraser.dropColumnWithinTable(this.databaseInfo.getTargetTable(), createdColumn);
            }
            this.connection.close();

        } catch (JdbcCrudFailureException deleteError) {
            System.err.println("An error has occurred while deleting: " + deleteError.getMessage());
        }
    }


    // NOTE: Parameter "value" - a pair which contains target column name and value of it's row.
    public void insertSpecifiedValue(Map.Entry<String, String> value) throws SQLException, IllegalArgumentException {
        if (this.connection == null) {
            final String misleadingMsg = "Connection to required database hasn't been established.";
            throw new IllegalArgumentException(misleadingMsg);
        }
        final String columnName = value.getKey();
        final String columnValue = value.getValue();
        this.databaseElementInserter.insertValueIntoColumn(this.databaseInfo.getTargetTable(), columnName, columnValue);
    }

}
