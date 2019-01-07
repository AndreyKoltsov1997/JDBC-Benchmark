package benchmark.cli;

import benchmark.common.Constants;

import java.sql.Timestamp;
import java.util.*;

// NOTE: A class responsible for parsing OPTIONAL command line arguments.
// Warning: Package-private class
class OptionalCommandLineArguments {

    // MARK: - Constants


    private final String ARGUMENT_NOT_PROVIDED_VALUE = "";
    private final char ARGUMENT_EQUALS_SIGN = '=';
    private final char OPTIONAL_ARGUMENT_PREFIX_ELEMENT = '-';


    // NOTE: Optional CLI arguments tags. WARNING: MAKE SURE to modify initializing of avaliable options container ...
    // ... in case of adding a new one.
    private static final String DB_HOST_TAG = "host";
    private final String DB_PORT_TAG = "port";
    private final String DB_NAME_TAG = "name";
    private final String DB_TABLE_TAG = "table";

    private final String PAYLOAD_TAG = "payload";
    private final String INSERT_AMOUNT_TAG = "insertions";

    private final String OUTPUT_FILE_TAG = "file";
    private final String AMOUNT_OF_THREADS_TAG = "threads";


    private Map<String, String> options;

    // NOTE: Option duplicates are not allowed, order is not important
    private Set<String> availableOptions;




    // NOTE: Empty constructor
    public OptionalCommandLineArguments() {
        this.options = new HashMap<>();
        this.availableOptions = new HashSet<>();
        this.initAvailableOptions();

    }

    private void initAvailableOptions() {
        this.availableOptions.add(DB_HOST_TAG);
        this.availableOptions.add(DB_NAME_TAG);
        this.availableOptions.add(DB_PORT_TAG);
        this.availableOptions.add(DB_TABLE_TAG);
        this.availableOptions.add(PAYLOAD_TAG);
        this.availableOptions.add(INSERT_AMOUNT_TAG);
        this.availableOptions.add(OUTPUT_FILE_TAG);
        this.availableOptions.add(AMOUNT_OF_THREADS_TAG);

    }

    void parseOptionalArguments(String[] args) throws IllegalArgumentException {
        // NOTE: Fetching values from given arguments
        for (int i = 0; i < args.length; ++i) {
            String currentArgument = args[i];
            if (hasOptionalArgumentPrefix(currentArgument)) {
                final String argumentName = this.getArgumentName(currentArgument);
                if (!isArgumentExist(argumentName)) {
                    throw new IllegalArgumentException("Argument \"" + argumentName + "\" doesn't exist. ");
                }
                final String argumentValue = this.getOptionValue(currentArgument);
                this.options.put(argumentName, argumentValue);
            }
        }
        System.out.println("Option arguments: " + this.options.toString());
    }

    // NOTE: Optional arguments syntax: "--argumentName=arugmentValue", thus it...
    // ... should have both "--" and "=" signs
    public boolean isArgumentOptional(String argument) {
        return (hasOptionalArgumentPrefix(argument) && hasEqualSign(argument));
    }

    private boolean hasOptionalArgumentPrefix(String argument) {
        final int optionHyphenStartIndex = 0;
        final int optionHyphenEndIndex = 2;
        final String possibleOptionSpecificator = argument.substring(optionHyphenStartIndex, optionHyphenEndIndex);
        return possibleOptionSpecificator.equals(Constants.CLI_OPTION_PREFIX);
    }

    private boolean hasEqualSign(String argument) {
        final char equalChar = this.ARGUMENT_EQUALS_SIGN; // NOTE: Parameter value starts from equal char
        final int equalCharacterIndex = argument.indexOf(equalChar);
        final int charNotPresentedCode = -1;
        return (equalCharacterIndex != charNotPresentedCode);
    }

    // NOTE: Checks if optional argument name is valid and does exist (e.g.: "--myAwesomeCat=.." is not a valid argument)
    private boolean isArgumentExist(String argument) {
        return this.availableOptions.contains(argument);
    }

    private String getOptionValue(String option) throws IllegalArgumentException {
        if (!this.hasEqualSign(option)) {
            throw new IllegalArgumentException("Argument should have a value: " + option);
        }
        final int equalCharIndex = option.indexOf(this.ARGUMENT_EQUALS_SIGN);
        final String value = option.substring(equalCharIndex + 1); // NOTE: Starting from "=" ...
        // ... to the end of string
        return value;
    }

    private String getArgumentName(String argument) {
        final int optionPrefixIndex = argument.lastIndexOf(this.OPTIONAL_ARGUMENT_PREFIX_ELEMENT);
        final int equalSignIndex = argument.indexOf(this.ARGUMENT_EQUALS_SIGN);
        final String name = argument.substring(optionPrefixIndex + 1, equalSignIndex); // NOTE: Starting from "--" ...
        // ... to the end of string
        return name;
    }


    // NOTE: Fetching CLI argument value by its name
    public String getOptionByTag(final String tag) throws IllegalArgumentException {
        if (!isArgumentExist(tag)) {
            throw new IllegalArgumentException("Option \"" + tag + "\" doesn't exist.");
        }

        // TODO: Update switching since it looks gross
        switch (tag) {
            case DB_NAME_TAG:
                String processingTag = DB_NAME_TAG;
                if (!isArgumentSet(processingTag)) {
                    return this.generateDatabaseName();
                }
                return this.options.get(processingTag);

            case DB_HOST_TAG:
                processingTag = DB_HOST_TAG;

                if (!isArgumentSet(processingTag)) {
                    final String defaultHost = Constants.DEFAULT_HOST;
                    return defaultHost;
                }
                return this.options.get(processingTag);

            case DB_PORT_TAG:
                processingTag = DB_PORT_TAG;
                if (!isArgumentSet(processingTag)) {
                    final String defaultPort = Constants.DEFAULT_PORT;
                    return defaultPort;
                }
                return this.options.get(processingTag);

            case DB_TABLE_TAG:
                processingTag = DB_TABLE_TAG;
                if (!isArgumentSet(processingTag)) {
                    return this.generateTableName();
                }
                return this.options.get(processingTag);
            case PAYLOAD_TAG:
                processingTag = PAYLOAD_TAG;
                if (!isArgumentSet(processingTag)) {
                    final String defaultPayload = "0";
                    return defaultPayload;
                }
                return this.options.get(processingTag);
            case INSERT_AMOUNT_TAG:
                processingTag = INSERT_AMOUNT_TAG;
                if (!isArgumentSet(INSERT_AMOUNT_TAG)) {
                    return String.valueOf(Constants.INFINITE_AMOUNT_OF_INSERTIONS);
                }
                return this.options.get(processingTag);
            case OUTPUT_FILE_TAG:
                processingTag = OUTPUT_FILE_TAG;
                if (!isArgumentSet(processingTag)) {
                    return this.ARGUMENT_NOT_PROVIDED_VALUE;
                }
                return this.options.get(processingTag);
            case AMOUNT_OF_THREADS_TAG:
                processingTag = AMOUNT_OF_THREADS_TAG;
                if (!isArgumentSet(processingTag)) {
                    return String.valueOf(Constants.DEFAULT_AMOUNT_OF_THREADS);
                }
                return this.options.get(processingTag);
            default:
                throw new IllegalArgumentException("Option \"" + tag + "\" doesn't exist.");
        }
    }

    private boolean isArgumentSet(String argumentTag) {
        return this.options.containsKey(argumentTag);
    }

    public String getHost() {
        final String requiredTag = this.DB_HOST_TAG;
        if (this.options.containsKey(requiredTag)) {
            return this.options.get(requiredTag);
        }
        final String defaultHost = "localhost";
        return defaultHost;
    }


    private String generateDatabaseName() {
        final String defaultName = Constants.DEFAULT_DATABASE_NAME;
        return defaultName;
    }


    // NOTE: If table name is not set, generating it with timestamp
    private String generateTableName() {
        String timeStamp = new Timestamp(System.currentTimeMillis()).toString();
        final String nonNumberSymbolsRegEx = "[^0-9]";
        final String emptyValue = "";
        final String result = timeStamp.replaceAll(nonNumberSymbolsRegEx, emptyValue);
        return result;
    }
}
