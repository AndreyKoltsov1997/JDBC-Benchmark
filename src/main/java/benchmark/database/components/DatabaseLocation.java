package benchmark.database.components;

public class DatabaseLocation {

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
    public DatabaseLocation() {
        this.host = this.DEFAULT_HOST;
        this.port = this.DEFAULT_PORT;
    }

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

    // MARK: - Private methdos
    private Boolean isLocationParameterValid(final String parameter) {
        final String emptyString = "";
        return ((parameter != null) && (!parameter.equals(emptyString)));
    }


    @Override
    public String toString() {
        final String localtionDelimiter = ":";
        return this.host + localtionDelimiter + this.port;
    }
}
