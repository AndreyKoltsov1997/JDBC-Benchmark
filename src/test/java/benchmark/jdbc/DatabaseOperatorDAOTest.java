package benchmark.jdbc;

import benchmark.database.BenchmarkSupportingDatabases;
import benchmark.database.DatabaseInfo;
import benchmark.database.components.DatabaseCredentials;
import benchmark.database.components.DatabaseLocation;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DatabaseOperatorDAOTest {


    @Test(expected = SQLException.class)
    public void testUnreachableConnection() throws SQLException {
        new DatabaseOperatorDAO(getTestDatabaseInfo());
    }

    // WARNING: Test is usable with a valid DB connection. You'd have to create a valid database DAO.
    @Test(expected = SQLException.class)
    public void testInsertOnUnreachableConnection() throws SQLException, IllegalArgumentException {
        DatabaseOperatorDAO databaseOperatorDAO = new DatabaseOperatorDAO(getTestDatabaseInfo());

        Map<String, String> testParameters = new HashMap<>();
        final String testKey = "key";
        final String testValue = "value";
        testParameters.put(testKey, testValue);

        Map.Entry<String, String> parametersEntry = testParameters.entrySet().iterator().next();
        databaseOperatorDAO.insertSpecifiedValue(parametersEntry);

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