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
            System.exit(Constants.STATUS_INVALID_ARGUMENT);
        }

        // NOTE: Fetching Database location from CL arguments
        final String host = commandLineArgsParser.getHost();
        final String port = commandLineArgsParser.getPort();
        DatabaseLocation databaseLocation = new DatabaseLocation(host, port);

        // NOTE: Fetching Database credentials from CL arguments
        final String username = commandLineArgsParser.getUserName();
        final String password = commandLineArgsParser.getUserPassword();
        DatabaseCredentials databaseCredentials = null;
        try {
            databaseCredentials = new DatabaseCredentials(username, password);
        } catch (IllegalArgumentException error) {
            System.err.println("An error has occurred while parsing user credentials: " + error.getMessage());
            System.exit(Constants.STATUS_INVALID_ARGUMENT);
        }

        // NOTE: Fetching database info from CL arguments
        final String databaseName = commandLineArgsParser.getDatabaseName();
        final String databaseTargetTable = commandLineArgsParser.getTableName();
        // NOTE: In this version, only PostgreSQL is supported (we had an agreement in Slack).
        // TODO: Determine databaseType dynamicly and fetch required dependencies.
        final BenchmarkSupportingDatabases databaseType = BenchmarkSupportingDatabases.POSTGRESQL;
        DatabaseInfo databaseInfo = new DatabaseInfo(databaseLocation, databaseCredentials, databaseName, databaseTargetTable, databaseType);

        // NOTE: Fetching benchmark parameters from CL arguments
        final int amountOfThreads = commandLineArgsParser.getAmountOfThreads();
        final int payload = commandLineArgsParser.getPayload();
        final int amountOfInsertions = commandLineArgsParser.getAmountOfInsertions();
        final String fileName = commandLineArgsParser.getFileNameForLogs();
        JdbcBenchmark jdbcBenchmark = new JdbcBenchmark(payload, amountOfThreads, amountOfInsertions, databaseInfo, fileName);

        jdbcBenchmark.performBenchmark();
    }
}
