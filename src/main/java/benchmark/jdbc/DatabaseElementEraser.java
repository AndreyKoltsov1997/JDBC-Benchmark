package benchmark.jdbc;

import benchmark.database.DatabaseInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseElementEraser  {

    private Connection connection;
    private DatabaseInfo databaseInfo;
    private DatabaseElementValidator databaseElementValidator;

    public DatabaseElementEraser(Connection connection, DatabaseInfo databaseInfo, DatabaseElementValidator databaseElementValidator) {
        this.connection = connection;
        this.databaseInfo = databaseInfo;
        this.databaseElementValidator = databaseElementValidator;
    }

    // NOTE: Deleting column within specified table.
    public void dropColumnWithinTable(String table, String column) throws SQLException {
        final String dropColumnSqlQuery = String.format("ALTER TABLE \"%s\" DROP %s;", table, column);
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(dropColumnSqlQuery)) {
            preparedStatement.executeUpdate();
        }
    }

    // NOTE: Deleting database with specified name.
    public void dropDatabase(String name) throws SQLException, JdbcCrudFailureException {
        final String dropDatabaseSqlQuery = String.format("DROP DATABASE %s", name);
        Statement statement = this.connection.createStatement();
        final int amountOfSuccessOperations = statement.executeUpdate(dropDatabaseSqlQuery);
        if (!this.databaseElementValidator.isStatementExecutionCorrect(amountOfSuccessOperations)) {
            throw new JdbcCrudFailureException("Database " + name + " couldn't be dropped.", CrudOperationType.DELETE);
        }
    }

}
