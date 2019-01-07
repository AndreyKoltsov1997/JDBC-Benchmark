package benchmark.jdbc;

import benchmark.database.DatabaseInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseElementCreator {
    private final static String TAG = DatabaseElementCreator.class.getName();

    private Connection connection;
    private final DatabaseInfo databaseInfo;
    private final DatabaseElementValidator databaseElementValidator;

    public DatabaseElementCreator(Connection connection, DatabaseInfo databaseInfo, DatabaseElementValidator databaseElementValidator) {
        if (connection == null) {
            throw new IllegalArgumentException(DatabaseElementCreator.TAG + "Unable to get database connection.");
        }
        this.connection = connection;
        this.databaseInfo = databaseInfo;
        this.databaseElementValidator = databaseElementValidator;
    }


    // NOTE: Creating a column into current database.
    public void createColumn(final String name, final String type) throws JdbcCrudFailureException, SQLException {
        if (this.databaseElementValidator.isColumnExistInTable(this.databaseInfo.getTargetTable(), name)) {
            // NOTE: Do nothing if column is already exist.
            return;
        }
        if (!this.databaseElementValidator.isDatabaseElementNameValid(name)) {
            throw new JdbcCrudFailureException(DatabaseElementCreator.TAG + "\"" + name + "\" is not a valid column name.", CrudOperationType.CREATE);
        }

        final String addColumnSqlQuery = String.format(DatabaseElementCreator.TAG + "ALTER TABLE \"%s\" ADD %s %s;", this.databaseInfo.getTargetTable(), name, type);
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(addColumnSqlQuery)) {
            preparedStatement.executeUpdate();
        }
    }

    public void createDatabase(String name) throws SQLException, JdbcCrudFailureException {
        System.err.println("CREATING DATABASE.....");
        if (!this.databaseElementValidator.isDatabaseElementNameValid(name)) {
            final String nameFormatMisleadingMsg = "Name should contain only latin letters, numbers and an underscore.";
            throw new JdbcCrudFailureException(DatabaseElementCreator.TAG + "\"" + name + "\" is not a valid database name." + nameFormatMisleadingMsg, CrudOperationType.CREATE);
        }
        try(Statement statement = this.connection.createStatement()) {
            final String createDatabaseSqlQuery = "CREATE DATABASE " + name + "name";
            statement.executeUpdate(createDatabaseSqlQuery);

        }
    }


    public void createTable(final String name) throws SQLException {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS \"" + name + "\""
                + "  (key           VARCHAR(10),"
                + "   value            VARCHAR(10));";

        try (Statement statement = this.connection.createStatement()) {
            statement.execute(sqlCreate);
        }
    }

}
