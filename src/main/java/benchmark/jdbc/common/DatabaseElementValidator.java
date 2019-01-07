package benchmark.jdbc.common;

import benchmark.database.DatabaseInfo;

import java.sql.*;

public class DatabaseElementValidator {

    private Connection connection;

    // MARK: - Constructor
    public DatabaseElementValidator(Connection connection) {
        this.connection = connection;
    }

    // MARK: - Public methods

    public boolean isDatabaseElementNameValid(String name) {
        final String availableCharactersInNameRegEx = "^[a-zA-Z_][a-zA-Z0-9_]*$";
        return name.matches(availableCharactersInNameRegEx);
    }

    public boolean isColumnExistInTable(final String table, final String column) throws SQLException {
        if (this.connection == null) {
            throw new SQLException("Connection with target database hasn't bee established.");
        }
        ResultSet resultSet = connection.createStatement().executeQuery(String.format("SELECT * FROM \"%s\" WHERE 1<0;", table));
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        // WARNING: Column indexes are started from 1
        for (int i = 1; i <= resultSetMetaData.getColumnCount(); ++i) {
            if (column.equals(resultSetMetaData.getColumnLabel(i))) {
                return true;
            }
        }
        return false;
    }

    // NOTE: This method doesn't work with PostgreSQL: it returns an empty list of catalogs, ...
    // ... but throwing errors when I try to create DB with an existing name
    public boolean isDatabaseExistInServer(String databaseName) throws SQLException {
        if (this.connection == null) {
            System.err.println("Unable to fetch catalog: connection hasn't been established.");
            return false;
        }
        boolean isDatabaseExist = false;

        try (ResultSet resultSet = this.connection.getMetaData().getCatalogs()) {
            while (resultSet.next()) {
                final int databaseNameIndex = 1;
                final String processingDatabaseName = resultSet.getString(databaseNameIndex);
                isDatabaseExist = (processingDatabaseName.equals(databaseName));
            }
        }
        return isDatabaseExist;
    }

    public boolean isStatementExecutionCorrect(int amountOfOperations) {
        return (amountOfOperations > 0);
    }
}
