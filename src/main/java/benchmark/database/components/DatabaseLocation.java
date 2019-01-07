package benchmark.database.components;

import benchmark.common.Constants;

public class DatabaseLocation {

    // MARK: - Constants
    // NOTE: Default parameters are set if passed parameters are null

    private final String host;
    private final String port;

    // MARK: - Getters and Setters
    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    // MARK: - Constructor

    public DatabaseLocation(String host, String port) {
        if (!isLocationParameterValid(host)) {
            host = Constants.DEFAULT_HOST;
        }
        if (!isLocationParameterValid(host)) {
            port = Constants.DEFAULT_PORT;
        }

        this.host = host;
        this.port = port;
    }

    // MARK: - Private methods
    private Boolean isLocationParameterValid(final String parameter) {
        final String emptyString = "";
        return ((parameter != null) && (!parameter.equals(emptyString)));
    }

    // MARK: Overrides
    @Override
    public String toString() {
        final String locationDelimiter = ":";
        return this.host + locationDelimiter + this.port;
    }
}
