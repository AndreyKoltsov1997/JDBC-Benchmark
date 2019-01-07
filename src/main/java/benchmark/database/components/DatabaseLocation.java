package benchmark.database.components;

public class DatabaseLocation {

    // MARK: - Constants
    // NOTE: Default parameters are set if passed parameters are null
    private final String DEFAULT_HOST = "localhost";
    private final String DEFAULT_PORT = "8080";


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
            host = this.DEFAULT_HOST;
        }
        if (!isLocationParameterValid(host)) {
            port = this.DEFAULT_PORT;
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
        final String localtionDelimiter = ":";
        return this.host + localtionDelimiter + this.port;
    }
}
