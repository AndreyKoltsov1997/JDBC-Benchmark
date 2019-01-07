package benchmark.common;

public class Constants {

    // MARK: - Exit statuses
    public static final int EXIT_STATUS_SUCCESS = 0;
    public static final Integer EXIT_STATUS_INVALID_ARGUMENT = -2;
    public static final Integer EXIT_STATUS_CONNECTION_ERROR = -3;

    public static final String CLI_OPTION_PREFIX = "--";
    public static final int INFINITE_AMOUNT_OF_INSERTIONS = -9;
    public static final String NO_OUTPUT_REQUIRED_FILENAME = "";


    public static final String KEY_COLUMN_NAME = "key";
    public static final String VALUE_COLUMN_NAME = "value";

    // NOTE: Default values
    public static final int DEFAULT_AMOUNT_OF_THREADS = 1;
    public static final String DEFAULT_HOST = "localhost";
    public static final String DEFAULT_PORT = "8080";
    public static final String DEFAULT_DATABASE_NAME = "jdbcBenchmarkDb";


    public static final int DEFAULT_PAYLOAD = 0;
}
