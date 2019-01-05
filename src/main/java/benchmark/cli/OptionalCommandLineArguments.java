package benchmark.cli;

import benchmark.Constants;

// NOTE: Package-private class
class OptionalCommandLineArguments {


    // NOTE: Empty constructor
    public OptionalCommandLineArguments(String[] args) {

        // NOTE: Fetching values from given arguments
        for (int i = 0; i < args.length; ++i) {
            String currentArgument = args[i];
            if (isOptionName(currentArgument)) {
                // TODO: Handle options here
            }
        }
    }

    private boolean isOptionName(String argument) {
        final int optionHyphenStartIndex = 0;
        final int optionHyphenEndIndex = 2;
        final String possibleOptionSpecificator = argument.substring(optionHyphenStartIndex, optionHyphenEndIndex);
        return possibleOptionSpecificator.equals(Constants.CLI_OPTION_PREFIX);
    }




}
