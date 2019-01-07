package benchmark.database.components;

import org.junit.Test;

import static org.junit.Assert.*;

public class DatabaseCredentialsTest {

    @Test
    public void isParameterAcceptanceCorrect() {

        final String incorrectUsername = "";
        final String incorrectPassword = null;

        final String correctUsername = "Marquise";
        final String correctPassword = "The Cat";

        boolean isUsernameParsingCorrect = false;
        try {
            new DatabaseCredentials(incorrectUsername, correctPassword);
        } catch (Exception error) {
            // NOTE: If an exception is thrown, test is correct.
            isUsernameParsingCorrect = true;
        }

        boolean isPasswordParsingCorrect = false;
        try {
            new DatabaseCredentials(correctUsername, incorrectPassword);
        } catch (Exception error) {
            // NOTE: If an exception is thrown, test is correct.
            isPasswordParsingCorrect = true;
        }

        boolean isValidParametersParsingCorrect = false;
        try {
            new DatabaseCredentials(correctUsername, correctPassword);
            isValidParametersParsingCorrect = true;
        } catch (Exception error) {
            // NOTE: If an exception is thrown, test is NOT correct.
            isValidParametersParsingCorrect = false;
        }

        boolean isTestCorrect = (isUsernameParsingCorrect && isPasswordParsingCorrect && isValidParametersParsingCorrect);

        assertTrue(isTestCorrect);
    }

    @Test
    public void getPassword() {
    }
}