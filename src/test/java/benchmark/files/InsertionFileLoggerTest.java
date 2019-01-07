package benchmark.files;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class InsertionFileLoggerTest {

    @Test
    public void fileShouldContainAddedData() {
        final String testFileName = "testFile.csv";
        boolean isTestSuccessed = false;
        try {
            InsertionFileLogger insertionFileLogger = new InsertionFileLogger(testFileName);
            final String testDbName = "db_name";
            final String testTableName = "test_table";
            final String testKey = "test_key";
            final String testDuration = "test_duration";

            // NOTE: Save logged data in order to compare content of the created file later.
            List<String> possiblyAddedContent = new LinkedList<>();
            possiblyAddedContent.add(testDbName);
            possiblyAddedContent.add(testTableName);
            possiblyAddedContent.add(testKey);
            possiblyAddedContent.add(testDuration);

            insertionFileLogger.logOperation(testDbName, testTableName, testKey, testDuration);
            insertionFileLogger.stopWriting();

            final List<String> valuesFromCsvFile = getValuesFromCsvFile(testFileName);
            isTestSuccessed = valuesFromCsvFile.containsAll(possiblyAddedContent);

        } catch (IOException error) {
            // NOTE: Test is not correct in case of an exception while inserting the data.
            System.err.println("File logger test has failed. Reason: " + error.getMessage());
            isTestSuccessed = false;
            assertTrue(isTestSuccessed);
        }

        assertTrue(isTestSuccessed);
    }


    // NOTE: File logger shouldn't be activated in case empty name has been passed into the constructor, ...
    // ... should be active otherwise.
    @Test
    public void testLoggerActivation() {
        boolean isInactiveLoggerTestCorrect = false;
        boolean isActiveLoggerTestCorrect = true;
        try {
            final String noActivationNeededFileName = "";
            InsertionFileLogger inactiveFileLogger = new InsertionFileLogger(noActivationNeededFileName);
            isInactiveLoggerTestCorrect = !inactiveFileLogger.isActive();
            inactiveFileLogger.stopWriting();

            final String activationNeededFileName = "test";
            InsertionFileLogger activeFileLogger = new InsertionFileLogger(activationNeededFileName);
            isActiveLoggerTestCorrect = activeFileLogger.isActive();
            activeFileLogger.stopWriting();

        } catch (IOException error) {
            // NOTE: Test is not correct in case of an exception while activating the logger.
            System.err.println("Logger activation test has failed. Reason: " + error.getMessage());
            boolean isTestSuccessed = false;
            assertTrue(isTestSuccessed);
        }
        boolean isTestSuccessed = (isInactiveLoggerTestCorrect && isActiveLoggerTestCorrect);
        assertTrue(isTestSuccessed);
    }

    private List<String> getValuesFromCsvFile(final String fileName) throws IOException {
        File createdFile = new File(fileName);
        List<String> createdFileContent = Files.readAllLines(createdFile.toPath(),
                StandardCharsets.UTF_8);

        List<String> fileContentValues = new ArrayList<>();
        final String csvDelimiter = ",";
        for (String lineInCreatedFile: createdFileContent) {
            String[] valuesInFileString = lineInCreatedFile.split(csvDelimiter);
            fileContentValues.addAll(Arrays.asList(valuesInFileString));
        }
        return fileContentValues;
    }
}