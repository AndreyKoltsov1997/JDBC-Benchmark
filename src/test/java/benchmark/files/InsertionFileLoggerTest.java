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
        boolean isTestSuccess = false;
        try {
            InsertionFileLogger insertionFileLogger = new InsertionFileLogger(testFileName);
            final String testDbName = "db_name";
            final String testTableName = "test_table";
            final String testKey = "test_key";
            final String testDuration = "test_duration";
            insertionFileLogger.logOperation(testDbName, testTableName, testKey, testDuration);
            insertionFileLogger.stopWriting();

            // NOTE: Retrieving data from the created file
            List<String> addedContent = new LinkedList<>();
            addedContent.add(testDbName);
            addedContent.add(testTableName);
            addedContent.add(testKey);
            addedContent.add(testDuration);

            File createdFile = new File(testFileName);
            List<String> createdFileContent = Files.readAllLines(createdFile.toPath(),
                    StandardCharsets.UTF_8);

            List<String> fileContentValues = new ArrayList<>();
            final String csvDelimiter = ",";
            for (String lineInCreatedFile: createdFileContent) {
                String[] valuesInFileString = lineInCreatedFile.split(csvDelimiter);
                fileContentValues.addAll(Arrays.asList(valuesInFileString));
            }
            isTestSuccess = fileContentValues.containsAll(addedContent);

        } catch (IOException error) {
            System.err.println("File logger test has failed. Reason: " + error.getMessage());
            isTestSuccess = false;
            assertTrue(isTestSuccess);
        }

        assertTrue(isTestSuccess);

    }
}