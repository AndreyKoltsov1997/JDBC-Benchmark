package benchmark.cli;

import benchmark.Constants;

import java.util.*;

// NOTE: Package-private class
class OptionalCommandLineArguments {

    private final String ARGUMENT_NOT_PROVIDED_VALUE = "";
    private final char ARGUMENT_EQUALS_SIGN = '=';
    private final String HOST_TAG = "host";
    private final char ARGUMENT_PREFIX_ELEMENT = '-';

    private Map<String, String> options;

    // NOTE: Option duplicates are not allowed, order is not important
    private Set<String> avaliableOptions;

    // NOTE: Database location
    private String DB_HOST_TAG = "host";
    private String DB_NAME_TAG = "name";


    // NOTE: Empty constructor
    public OptionalCommandLineArguments() {
        this.options = new HashMap<>();
        this.avaliableOptions = new HashSet<>();
        this.initAvaliableOptions();

    }

    private void initAvaliableOptions() {
        this.avaliableOptions.add(DB_HOST_TAG);
        this.avaliableOptions.add(DB_NAME_TAG);
    }

    public void parseOptionalArguments(String[] args) throws IllegalArgumentException {
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
        return this.avaliableOptions.contains(argument);
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
