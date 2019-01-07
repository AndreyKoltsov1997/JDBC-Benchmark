package benchmark;

import benchmark.cli.CommandLineArgsParser;
import benchmark.common.Constants;
import benchmark.database.BenchmarkSupportingDatabases;
import benchmark.database.components.DatabaseCredentials;
import benchmark.database.DatabaseInfo;
import benchmark.database.components.DatabaseLocation;

// NOTE: Benchmark is a main class of JDBC benchmark app
public class Benchmark {

    // MARK: - Program entry point
    public static void main(String[] args) {

        final CommandLineArgsParser commandLineArgsParser = new CommandLineArgsParser();

        try {
            commandLineArgsParser.parseArguments(args);
        } catch (IllegalArgumentException error) {
            System.err.println(error.getMessage());
            System.exit(Constants.EXIT_STATUS_INVALID_ARGUMENT);
        }

        // NOTE: Fetching Database location from CL arguments
        DatabaseLocation databaseLocation = null;
        try {
            final String host = commandLineArgsParser.getHost();
            final String port = commandLineArgsParser.getPort();
            databaseLocation = new DatabaseLocation(host, port);
        } catch (NumberFormatException error) {
            System.err.println("Unable to parse entered database location. Reason: " + error.getMessage());
            System.exit(Constants.EXIT_STATUS_INVALID_ARGUMENT);
        }


        // NOTE: Fetching Database credentials from CL arguments
        DatabaseCredentials databaseCredentials = null;
        try {
            final String username = commandLineArgsParser.getUserName();
            final String password = commandLineArgsParser.getUserPassword();
            databaseCredentials = new DatabaseCredentials(username, password);
        } catch (IllegalArgumentException error) {
            System.err.println("Unable to parse entered database credentials: " + error.getMessage());
            System.exit(Constants.EXIT_STATUS_INVALID_ARGUMENT);
        }

        // NOTE: Fetching database info from CL arguments
        final String databaseName = commandLineArgsParser.getDatabaseName();
        final String databaseTargetTable = commandLineArgsParser.getTableName();
        // NOTE: In this version, only PostgreSQL is supported (we had an agreement in Slack).
        // TODO: Determine databaseType dynamicly and fetch required dependencies.
        final BenchmarkSupportingDatabases databaseType = BenchmarkSupportingDatabases.POSTGRESQL;
        DatabaseInfo databaseInfo = new DatabaseInfo(databaseLocation, databaseCredentials, databaseName, databaseTargetTable, databaseType);

        // NOTE: Fetching benchmark parameters from CL arguments
        JdbcBenchmark jdbcBenchmark = null;

        try {
            final int amountOfThreads = commandLineArgsParser.getAmountOfThreads();
            final int payload = commandLineArgsParser.getPayload();
            final int amountOfInsertions = commandLineArgsParser.getAmountOfInsertions();
            final String fileName = commandLineArgsParser.getFileNameForLogs();
            jdbcBenchmark = new JdbcBenchmark(payload, amountOfThreads, amountOfInsertions, databaseInfo, fileName);
        } catch (NumberFormatException error) {
            System.err.println("Unable to parse entered benchmark configurations. Reason: " + error.getMessage());
            System.exit(Constants.EXIT_STATUS_INVALID_ARGUMENT);
        }

        jdbcBenchmark.performBenchmark();
    }
}
