package benchmark.cli;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommandLineArgsParserTest {

    @Test
    public void parseArguments() {
        CommandLineArgsParser commandLineArgsParser = new CommandLineArgsParser();

        boolean isTestCorrect = false;
        // NOTE: Testing non-optional arguments
        try {
            final String[] emptyArguments = {""};
            commandLineArgsParser.parseArguments(emptyArguments);
        } catch (IllegalArgumentException error) {
            // NOTE: If an exception has been thrown on empty arguments list, methods work correctly.
            isTestCorrect = true;
        }

        // NOTE: Testing optional arguments
        final String[] incorrectOptionalArguments = {"username", "password", "--incorrect=optionalArg"};
        try {
            commandLineArgsParser.parseArguments(incorrectOptionalArguments);
        } catch (IllegalArgumentException error) {
            // NOTE: If an exception has been thrown on arguments list consisting ...
            // ... incorrect optional argument, methods work correctly.
            isTestCorrect = true;
        }
        assertTrue(isTestCorrect);
    }
}