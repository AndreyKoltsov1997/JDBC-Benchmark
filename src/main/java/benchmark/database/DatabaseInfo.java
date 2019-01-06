package benchmark.database;

import benchmark.database.components.DatabaseCredentials;
import benchmark.database.components.DatabaseLocation;

public class DatabaseInfo {

    // NOTE: Default database targetDatabaseName
    private final String DEFAULT_NAME = "jdbc_benchmark";

    // NOTE: Locations
    private final DatabaseLocation location;
    private final DatabaseCredentials credentials;


    private final String targetDatabaseName;
    private String targetTable;

    // MARK: - Constructor
    public DatabaseInfo(DatabaseLocation location, DatabaseCredentials credentials, String targetDatabaseName, String targetTable) {
        this.location = location;
        this.credentials = credentials;

        if (!this.isParameterValid(targetDatabaseName)) {
            targetDatabaseName = this.DEFAULT_NAME;
        }
        this.targetDatabaseName = targetDatabaseName;
        this.targetTable = targetTable;
    }

    public DatabaseInfo(DatabaseLocation location, DatabaseCredentials credentials) {
        this.location = location;
        this.credentials = credentials;
        this.targetDatabaseName = this.DEFAULT_NAME;
    }

    public final String getDatabaseURL() {
        final String jdbcNotation = "jdbc";
        final String DATABASE_NOTATION_MOCK = "postgresql";
        // TODO: Look up better ways of formatting
        return String.format("%s:%s://%s/", jdbcNotation, DATABASE_NOTATION_MOCK, this.location.toString());
    }

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

    // MARK: - Private
    // TODO: Potential copy-paste of parameters (see Database Location - fix that
    private Boolean isParameterValid(String parameter) {
        final String emptyString = "";
        return ((parameter != null) && (!parameter.equals(emptyString)));
    }



}
