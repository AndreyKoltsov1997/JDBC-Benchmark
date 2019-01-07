package benchmark.database.components;

public class DatabaseCredentials {

    private final String username;
    private final String password;

    // MARK: - Getters and Setters

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // MARK: - Constructor

    public DatabaseCredentials(String username, String password) throws IllegalArgumentException {
        if (!this.isUserLoginParameterValid(username) || !this.isUserLoginParameterValid(password)) {
            final String misleadingMessage = "User must have username and password set.";
            throw new IllegalArgumentException(misleadingMessage);
        }
        this.username = username;
        this.password = password;
    }

    // MARK: - Private
    private Boolean isUserLoginParameterValid(String parameter) {
        final String emptyString = "";
        return ((parameter != null) && (!parameter.equals(emptyString)));
    }

}
