package benchmark.jdbc.common;

import benchmark.jdbc.CrudOperationType;
import benchmark.jdbc.exceptions.JdbcCrudFailureException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

// NOTE: A class responsible for creating elements within connected DB via JDBC.

public class DatabaseElementCreator {

    // MARK: - Constants
    private final static String TAG = DatabaseElementCreator.class.getName();

    private Connection connection;
    private final DatabaseElementValidator databaseElementValidator;

    // MARK: - Constructor
    public DatabaseElementCreator(Connection connection, DatabaseElementValidator databaseElementValidator) {
        if (connection == null) {
            throw new IllegalArgumentException(DatabaseElementCreator.TAG + "Unable to get database connection.");
        }
        this.connection = connection;
        this.databaseElementValidator = databaseElementValidator;
    }


    // MARK: - Public methods

    // NOTE: Creating a column into current database.
    public void createColumnIfNotExists(final String table, final String column, final String type) throws JdbcCrudFailureException, SQLException {
        if (this.databaseElementValidator.isColumnExistInTable(table, column)) {
            // NOTE: Do nothing if column is already exist.
            return;
        }
        if (!this.databaseElementValidator.isDatabaseElementNameValid(column)) {
            throw new JdbcCrudFailureException(DatabaseElementCreator.TAG + "\"" + column + "\" is not a valid column name.", CrudOperationType.CREATE);
        }

        final String addColumnSqlQuery = String.format(DatabaseElementCreator.TAG + "ALTER TABLE \"%s\" ADD %s %s;", table, column, type);
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(addColumnSqlQuery)) {
            preparedStatement.executeUpdate();
        }
    }

    public void createDatabase(String name) throws SQLException, JdbcCrudFailureException {
        if (!this.databaseElementValidator.isDatabaseElementNameValid(name)) {
            final String nameFormatMisleadingMsg = "Name should contain only latin letters, numbers and an underscore.";
            throw new JdbcCrudFailureException(DatabaseElementCreator.TAG + "\"" + name + "\" is not a valid database name." + nameFormatMisleadingMsg, CrudOperationType.CREATE);
        }
        try (Statement statement = this.connection.createStatement()) {
            final String createDatabaseSqlQuery = String.format("CREATE DATABASE \"%s\";", name);
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
