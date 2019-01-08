package benchmark.jdbc;

import benchmark.common.Constants;
import benchmark.database.BenchmarkSupportingDatabases;
import benchmark.database.DatabaseInfo;
import benchmark.database.components.DatabaseCredentials;
import benchmark.database.components.DatabaseLocation;
import org.junit.Test;

import java.sql.SQLException;

public class DatabaseOperatorDAOTest {


    @Test(expected = SQLException.class)
    public void testUnreachableConnection() throws SQLException {
        final int testPayload = 1;
        new DatabaseOperatorDAO(getTestDatabaseInfo(), testPayload);
    }

    // WARNING: Test is usable with a valid DB connection. You'd have to create a valid database DAO.
    @Test(expected = SQLException.class)
    public void testInsertOnUnreachableConnection() throws SQLException, IllegalArgumentException {
        final int testPayload = 0;
        DatabaseOperatorDAO databaseOperatorDAO = new DatabaseOperatorDAO(getTestDatabaseInfo(), testPayload);

        final String testKey = "key";
        databaseOperatorDAO.insertValueIntoColumn(Constants.KEY_COLUMN_NAME, testKey);

        final String testValue = "value";
        databaseOperatorDAO.insertValueIntoColumn(Constants.VALUE_COLUMN_NAME, testValue);
    }


    private DatabaseInfo getTestDatabaseInfo() {
        // NOTE: Creating constant DB location
        final String testHost = "";
        final String testPort = "";
        final DatabaseLocation testDbLocation = new DatabaseLocation(testHost, testPort);


        // NOTE: Creating constant DB credentials
        final String testUsername = "username";
        final String testPassword = "password";
        final DatabaseCredentials testDbCredentials = new DatabaseCredentials(testUsername, testPassword);

        // NOTE: Creating constant DB info
        final BenchmarkSupportingDatabases referenceDatabase = BenchmarkSupportingDatabases.POSTGRESQL;
        final String testTableName = "table";
        final String testDbName = "db";
        final DatabaseInfo databaseInfo = new DatabaseInfo(testDbLocation, testDbCredentials, testDbName, testTableName, referenceDatabase);
        return databaseInfo;
    }

}