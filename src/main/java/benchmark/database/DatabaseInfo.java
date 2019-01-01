package benchmark.database;

import benchmark.database.components.DatabaseCredentials;
import benchmark.database.components.DatabaseLocation;

public class DatabaseInfo {

    // NOTE: Default database name
    private final String DEFAULT_NAME = "test";

    // NOTE: Locations
    private final DatabaseLocation location;
    private final DatabaseCredentials credentials;
    private final String name;

    // MARK: - Constructor
    public DatabaseInfo(DatabaseLocation location, DatabaseCredentials credentials, String name) {
        this.location = location;
        this.credentials = credentials;

        if (this.isParameterValid(name)) {
            name = this.DEFAULT_NAME;
        }
        this.name = name;
    }

    public DatabaseInfo(DatabaseLocation location, DatabaseCredentials credentials) {
        this.location = location;
        this.credentials = credentials;
        this.name = this.DEFAULT_NAME;
    }

    public final String getDatabaseURL() {
        // jdbc:postgresql://localhost:5431/
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

    // MARK: - Private
    // TODO: Potential copy-paste of parameters (see Database Location - fix that
    private Boolean isParameterValid(String parameter) {
        final String emptyString = "";
        return ((parameter != null) && (!parameter.equals(emptyString)));
    }


}
