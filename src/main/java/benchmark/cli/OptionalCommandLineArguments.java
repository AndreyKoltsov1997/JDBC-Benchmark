package benchmark.cli;

import benchmark.Constants;

import java.util.HashMap;
import java.util.Map;

// NOTE: Package-private class
class OptionalCommandLineArguments {

    private final String ARGUMENT_NOT_PROVIDED_VALUE = "";
    private final char ARGUMENT_EQUALS_SIGN = '=';
    private final String HOST_TAG = "host";
    private final char ARGUMENT_PREFIX_ELEMENT = '-';

    private Map<String, String> options;

    // NOTE: Database location
    private String databaseHost = ARGUMENT_NOT_PROVIDED_VALUE;
    private String databasePort = ARGUMENT_NOT_PROVIDED_VALUE;

    // NOTE: Empty constructor
    public OptionalCommandLineArguments() {
        this.options = new HashMap<>();

    }

    public void parseOptionalArguments(String[] args) throws IllegalArgumentException {
        // NOTE: Fetching values from given arguments
        for (int i = 0; i < args.length; ++i) {
            String currentArgument = args[i];
            if (hasOptionalArgumentPrefix(currentArgument)) {
                final String argumentName = this.getArgumentName(currentArgument);
                final String argumentValue = this.getOptionValue(currentArgument);
                System.out.println("Argument name: " + argumentName + " with value : " + argumentValue);
            }
        }
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
        final int optionPrefixIndex = argument.lastIndexOf(this.ARGUMENT_PREFIX_ELEMENT);
        final int equalSignIndex = argument.indexOf(this.ARGUMENT_EQUALS_SIGN);
        final String name = argument.substring(optionPrefixIndex + 1, equalSignIndex); // NOTE: Starting from "--" ...
        // ... to the end of string
        return name;
    }

}
