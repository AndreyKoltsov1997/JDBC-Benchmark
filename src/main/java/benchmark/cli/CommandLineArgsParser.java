package benchmark.cli;

public class CommandLineArgsParser {


    private final int REQUIRED_MINIMUM_OF_ARGUMENTS = 2; // Database's credentials (username, password)
    private final int MAXIMAL_AVALIABLE_ARGUMENTS = 10;

    // NOTE: Database location
    private String databaseHost;
    private String databasePort;

    // NOTE: Database credentials
    private String userName;
    private String userPassword;

    // NOTE: Database target components
    private String databaseName;
    private String targetTableName;

    // NOTE: Benchmark components
    private String payloadSize;
    private String amountOfInsertOperations;
    private String fileNameForLogs;
    private String amountOfThreads;




    // NOTE: Empty constructor
    public CommandLineArgsParser()  { }

    public void parseArguments(String[] args) throws IllegalArgumentException {
        if (!isAmountOfArgsSatysfying(args)) {
            // TODO: Replace to string formatter
            final String delimiter = " ";
            final String errorCause = "Invalid amount of arguments : " + args.length + ".";
            final String misleadingMsg = errorCause + delimiter + "Amount of arguments should stay in range: [" + this.REQUIRED_MINIMUM_OF_ARGUMENTS +
                    ":" + this.MAXIMAL_AVALIABLE_ARGUMENTS + "].";
            throw new IllegalArgumentException(misleadingMsg);
        }
    }


    private boolean isAmountOfArgsSatysfying(String [] args) {
        return ((args.length >= this.REQUIRED_MINIMUM_OF_ARGUMENTS) && (args.length <= this.MAXIMAL_AVALIABLE_ARGUMENTS));
    }
}
