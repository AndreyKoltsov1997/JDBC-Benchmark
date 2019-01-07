package benchmark.database;

import benchmark.common.Constants;
import benchmark.database.components.DatabaseCredentials;
import benchmark.database.components.DatabaseLocation;

public class DatabaseInfo {


    // MARK: - Constants
    private static final String POSTGRESQL_JDBC_NOTATION = "postgresql";

    // NOTE: Locations
    private final DatabaseLocation location;
    private final DatabaseCredentials credentials;

    private final String targetDatabaseName;
    private String targetTable;
    private BenchmarkSupportingDatabases benchmarkSupportingDatabases;

    // MARK: Getters and setters

    public final String getUsername() {
        return this.credentials.getUsername();
    }

    public final String getPassword() {
        return this.credentials.getPassword();
    }

    public String getTargetTable() {
        return targetTable;
    }

    public String getTargetDatabaseName() {
        return targetDatabaseName;
    }

    // MARK: - Constructor
    public DatabaseInfo(DatabaseLocation location, DatabaseCredentials credentials, String targetDatabaseName, String targetTable, BenchmarkSupportingDatabases database) {
        this.location = location;
        this.credentials = credentials;

        if (!this.isParameterValid(targetDatabaseName)) {
            targetDatabaseName = Constants.DEFAULT_DATABASE_NAME;
        }
        this.targetDatabaseName = targetDatabaseName;
        this.targetTable = targetTable;

        this.benchmarkSupportingDatabases = database;
    }



    // MARK: - Public methods

    public final String getDatabaseJdbcUrl() {
        final String jdbcNotation = "jdbc";
        final String currentDatabaseNameNotation = getJdbcNameNotation(this.benchmarkSupportingDatabases);
        if (currentDatabaseNameNotation == null) {
            System.err.println("Unable to find JDBC notation for " + this.getTargetDatabaseName());
            System.exit(Constants.STATUS_INVALID_ARGUMENT);
        }
        return String.format("%s:%s://%s/", jdbcNotation, currentDatabaseNameNotation, this.location.toString());
    }

    // MARK: - Private
    // TODO: Potential copy-paste of parameters (see Database Location - fix that
    private Boolean isParameterValid(String parameter) {
        final String emptyString = "";
        return ((parameter != null) && (!parameter.equals(emptyString)));
    }


    // NOTE: Retrieving JDBC notation for required database. Supported ...
    // ... databases are listen within BenchmarkSupportingDatabases enum.
    private String getJdbcNameNotation(BenchmarkSupportingDatabases database) {
        switch (database) {
            case POSTGRESQL:
                return DatabaseInfo.POSTGRESQL_JDBC_NOTATION;
        }
        return null;
    }




}
