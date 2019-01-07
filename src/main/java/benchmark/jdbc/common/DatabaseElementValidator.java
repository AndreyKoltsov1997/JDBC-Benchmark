package benchmark.jdbc.common;

import benchmark.database.DatabaseInfo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseElementValidator {
    private Connection connection;
    private DatabaseInfo databaseInfo;

    public DatabaseElementValidator(Connection connection, DatabaseInfo databaseInfo) {
        this.connection = connection;
        this.databaseInfo = databaseInfo;
    }

    public boolean isDatabaseElementNameValid(String name) {
        final String availableCharactersInNameRegEx = "^[a-zA-Z_][a-zA-Z0-9_]*$";
        return name.matches(availableCharactersInNameRegEx);
    }

    public boolean isColumnExistInTable(final String table, final String column) throws SQLException {
        DatabaseMetaData md = connection.getMetaData();
        ResultSet rs = md.getColumns(null, null, table, column);
        if (rs.next()) {
            return true;
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

        try(ResultSet resultSet = this.connection.getMetaData().getCatalogs()) {
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
