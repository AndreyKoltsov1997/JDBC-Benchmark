package benchmark.jdbc.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// NOTE: A class responsible for insertion of values into connected DB via JDBC.
public class DatabaseElementInserter {

    private Connection connection;
    private DatabaseElementValidator databaseElementValidator;

    // MARK: - Constructor
    public DatabaseElementInserter(Connection connection, DatabaseElementValidator databaseElementValidator) {
        if (connection == null) {
            throw new IllegalArgumentException("Unable to create inserter since connection hasn't been established.");
        }
        this.connection = connection;
        this.databaseElementValidator = databaseElementValidator;
    }

    // MARK: - Public methods

    // NOTE: Inserting row into specified column.
    public void insertValueIntoColumn(final String table, final String column, String value) throws SQLException, IllegalArgumentException {

        if (!this.databaseElementValidator.isColumnExistInTable(table, column)) {
            final String misleadingMsg = "Column " + column + " doesn't exist in table \"" + table + "\".";
            throw new IllegalArgumentException(misleadingMsg);
        }

        if (value.isEmpty()) {
            // NOTE: SQL doesn't allow insertion of empty strings, so I'm adding an empty value.
            final String minimalStringAllowed = " ";
            value = minimalStringAllowed;
        }

        String insertSqlQuery = String.format("INSERT INTO \"%s\" (%s) VALUES ('%s');", table, column, value); //"INSERT INTO " + targetTable + "(" + column + ") VALUES ('" + value + "')";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(insertSqlQuery)) {
            // NOTE: (JavaDoc) either (1) the row count for SQL Data Manipulation Language (DML) statements or ...
            // ... (2) 0 for SQL statements that return nothing.
            preparedStatement.executeUpdate();
        }
    }

}
