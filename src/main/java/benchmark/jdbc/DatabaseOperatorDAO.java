package benchmark.jdbc;

import benchmark.JdbcBenchmark;
import benchmark.common.Constants;
import benchmark.database.DatabaseInfo;
import benchmark.jdbc.common.DatabaseElementCreator;
import benchmark.jdbc.common.DatabaseElementEraser;
import benchmark.jdbc.common.DatabaseElementInserter;
import benchmark.jdbc.common.DatabaseElementValidator;
import benchmark.jdbc.exceptions.JdbcCrudFailureException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


// NOTE: A class that provides connection to specified DB via JDBC and performing operations with it.
public class DatabaseOperatorDAO {

    // MARK: - Constants
    private final static String POSTGRES_DRIVER_CLASS_NAME = "org.postgresql.Driver";
    private final static List<String> processingTableColumnNames = new ArrayList<String>() {{
        add(Constants.KEY_COLUMN_NAME);
        add(Constants.VALUE_COLUMN_NAME);
    }};


    private final DatabaseInfo databaseInfo;
    private Connection connection;

    private DatabaseElementCreator databaseElementCreator;
    private DatabaseElementEraser databaseElementEraser;
    private DatabaseElementInserter databaseElementInserter;


    private boolean hasCreatedCustomDatabase = false;

    // MARK: - Constructor
    public DatabaseOperatorDAO(DatabaseInfo databaseInfo, final int minimalPayload) throws SQLException {
        this.databaseInfo = databaseInfo;

        this.establishConnection();

        // NOTE: Creating DB element validator if connection has been established.
        this.createDatabaseSupportingObjects();

        try {
            this.createMissingDatabaseElements(minimalPayload);

        } catch (JdbcCrudFailureException error) {
            System.err.println("Unable to create required DB element, reason: " + error.getMessage());
            System.exit(Constants.EXIT_STATUS_CONNECTION_ERROR);
        }
    }


    // MARK: - Public methods

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
            for (String createdColumn : DatabaseOperatorDAO.processingTableColumnNames) {
                System.out.println("Dropping column: " + createdColumn);
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

    // TODO: Refactor duplicate methods
    public void insertValueIntoColumn(final String column, final String value) throws SQLException {
        this.databaseElementInserter.insertValueIntoColumn(this.databaseInfo.getTargetTable(), column, value);
    }


    // MARK: - Private methods

    // NOTE: Creating database elements (catalog, table, column) if necessary
    private void createMissingDatabaseElements(final int minimalPayload) throws JdbcCrudFailureException {
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

        // NOTE: Creating columns.
        try {
            // NOTE: Creating "key" column. Length is static.
            final String keyColumnTag = Constants.KEY_COLUMN_NAME;
            this.databaseElementCreator.createEmptyColumn(this.databaseInfo.getTargetTable(), keyColumnTag, getDatabaseChatTypeTag(JdbcBenchmark.KEY_LENGTH));

            // NOTE: Creating "value" column. Length is based on payload.
            final String valueColumnTag = Constants.VALUE_COLUMN_NAME;
            this.databaseElementCreator.createEmptyColumn(this.databaseInfo.getTargetTable(), valueColumnTag, getDatabaseChatTypeTag(minimalPayload));

        } catch (SQLException error) {
            System.out.println("Column  exists. Using it for benchmark. " + error.getMessage());
        } catch (JdbcCrudFailureException error) {
            System.err.println("Unable to create required column. Reason: " + error.getMessage());
        }
    }


    // NOTE: Returns type tag for specified length (amount of characters)
    private String getDatabaseChatTypeTag(final int length) {
        return String.format("VARCHAR(%s)", length);
    }


    // NOTE: Creating objects for CRUD operations if connection has been established.
    private void createDatabaseSupportingObjects() {
        if (this.connection == null) {
            throw new IllegalArgumentException("Connection hasn't been established. Unable to create supporting objects.");
        }
        DatabaseElementValidator databaseElementValidator = new DatabaseElementValidator(this.connection);
        this.databaseElementCreator = new DatabaseElementCreator(this.connection, databaseElementValidator);
        this.databaseElementEraser = new DatabaseElementEraser(this.connection, databaseElementValidator);
        this.databaseElementInserter = new DatabaseElementInserter(this.connection, databaseElementValidator);
    }

    private void establishConnection() throws SQLException, IllegalArgumentException {

        // NOTE: Connection to PostgreSQL Database
        try {
            Class.forName(DatabaseOperatorDAO.POSTGRES_DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException error) {
            System.err.println("PostgreSQL Driver hasn't been found");
        }
        final String databaseURL = databaseInfo.getDatabaseJdbcUrl();
        final String databaseUsername = databaseInfo.getUsername();
        final String databaseUserPassword = databaseInfo.getPassword();
        this.connection = DriverManager.getConnection(databaseURL, databaseUsername, databaseUserPassword);
    }


}
