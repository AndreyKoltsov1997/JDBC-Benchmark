package benchmark;

import benchmark.cli.CommandLineArgsParser;
import benchmark.database.components.DatabaseCredentials;
import benchmark.database.DatabaseInfo;
import benchmark.database.components.DatabaseLocation;

public class Benchmark {



    public static void main(String[] args) {


        final CommandLineArgsParser commandLineArgsParser = new CommandLineArgsParser();

        try {
            commandLineArgsParser.parseArguments(args);
        } catch (IllegalArgumentException error) {
            System.err.println(error.getMessage());
            System.exit(Constants.STATUS_INVALID_ARGUMENT);
        }


        final String FILE_NAME_MOCK = commandLineArgsParser.getFileNameForLogs(); // DEBUG: "results.csv";

        final String PASSED_HOST_MOCK = commandLineArgsParser.getHost(); // DEBUG: "localhost";
        final String PASSED_PORT_MOCK = commandLineArgsParser.getPort(); //"5431";
        DatabaseLocation databaseLocation = new DatabaseLocation(PASSED_HOST_MOCK, PASSED_PORT_MOCK);

        final String USERNAME_MOCK = commandLineArgsParser.getUserName(); // DEBUG: "Andrey";
        final String USER_PASSWORD_MOCK = commandLineArgsParser.getUserPassword(); // DEBUG: "qwerty";
        DatabaseCredentials databaseCredentials = null;
        try {
            databaseCredentials = new DatabaseCredentials(USERNAME_MOCK, USER_PASSWORD_MOCK);
        } catch (IllegalArgumentException error) {
            System.err.println("An error has occured while parsing user credentials: " + error.getMessage());
            System.exit(Constants.STATUS_INVALID_ARGUMENT);
        }

        final String DATABASE_NAME_MOCK = commandLineArgsParser.getDatabaseName(); // DEBUG: "test";
        final String TARGET_TABLE_MOCK = commandLineArgsParser.getTableName();
        DatabaseInfo databaseInfo = new DatabaseInfo(databaseLocation, databaseCredentials, DATABASE_NAME_MOCK, TARGET_TABLE_MOCK);

        System.out.println("Database URL: " + databaseInfo.getDatabaseURL());
        final int AMOUNT_OF_THREADS_MOCK = commandLineArgsParser.getAmountOfThreads(); // DEBUG: 5;
        final int PAYLOAD_MOCK = commandLineArgsParser.getPayload(); // DEBUG: 2;
        final int AMOUNT_OF_INSERTIONS_MOCK = commandLineArgsParser.getAmountOfInsertions(); // DEBUG: 100;
        DatabaseBenchmark databaseBenchmark = new DatabaseBenchmark(PAYLOAD_MOCK, AMOUNT_OF_THREADS_MOCK, AMOUNT_OF_INSERTIONS_MOCK, databaseInfo, FILE_NAME_MOCK);


        databaseBenchmark.performBenchmark();

        System.out.println("benchmark.Benchmark has been created.");
    }
}
