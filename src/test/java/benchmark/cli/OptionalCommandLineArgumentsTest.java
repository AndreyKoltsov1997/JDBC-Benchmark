package benchmark.cli;

import org.junit.Test;

import static org.junit.Assert.*;

public class OptionalCommandLineArgumentsTest {

    @Test
    public void parseOptionalArguments() {
        OptionalCommandLineArguments optionalCommandLineArguments = new OptionalCommandLineArguments();
        final String testHost = "test_host";
        final String hostTag = "host";
        final String[] optionalArgumentHost = {"--" + hostTag + "=" + testHost};
        optionalCommandLineArguments.parseOptionalArguments(optionalArgumentHost);
        final boolean isTestCorrect = optionalCommandLineArguments.getOptionByTag(hostTag).equals(testHost);
        assertTrue(isTestCorrect);
    }

    @Test
    public void isArgumentOptional() {
        final String correctOptionalArgument = "--host=localhost";
        final String incorrectOptionalArgument = "mycat=isAwesome";
        OptionalCommandLineArguments optionalCommandLineArguments = new OptionalCommandLineArguments();
        final boolean isCorrectArgumentOK = optionalCommandLineArguments.isArgumentOptional(correctOptionalArgument);
        final boolean isIncorrectArgumentOK = optionalCommandLineArguments.isArgumentOptional(incorrectOptionalArgument);
        boolean isTestCorrect = (isCorrectArgumentOK && !isIncorrectArgumentOK);
        assertTrue(isTestCorrect);
    }
}