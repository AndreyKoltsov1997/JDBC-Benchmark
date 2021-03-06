package benchmark.jdbc.exceptions;


import benchmark.jdbc.CrudOperationType;

// NOTE: Custom exception class for CRUD operations performed via JDBC.
public class JdbcCrudFailureException extends Exception {

    private final String message;

    // MARK: - Constructor
    public JdbcCrudFailureException(String message, final CrudOperationType type) {
        super(message);
        String misleadingMsg = "";
        switch (type) {
            case CREATE:
                misleadingMsg = "Unable to perform CREATE operation. Reason: " + message;
                break;
            case READ:
                misleadingMsg = "Unable to READ information from the database. Reason: " + message;
                break;
            case UPDATE:
                misleadingMsg = "Unable to UPDATE information within the database. Reason: " + message;
                break;
            case DELETE:
                misleadingMsg = "Unable to DELETE information from the database. Reason: " + message;
                break;
            default:
                break;
        }
        this.message = misleadingMsg;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
