package benchmark.files;

import benchmark.common.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InsertionFileLogger implements IInsertionsFileLogger {

    // MARK: - Constants
    public static final String TAG = InsertionFileLogger.class.getName();
    private final static String REQUIRED_FILE_EXTENSION = ".csv";


    private Path pathToFile;
    private boolean isActive;
    private BufferedWriter bufferedWriter;

    // MARK: Getters and setters
    public boolean isActive() {
        return this.isActive;
    }


    // MARK: - Constructor
    public InsertionFileLogger(String fileURI) throws IOException {
        if (!this.shouldCreateLogger(fileURI)) {
            // NOTE: If empty URI has been passed, logging is not required
            this.isActive = false;
            return;
        }
        this.isActive = true;
        if (!hasRequiredExtension(fileURI)) {
            fileURI += InsertionFileLogger.REQUIRED_FILE_EXTENSION;
        }
        this.pathToFile = Paths.get(fileURI);
        if (!this.isFileExist(this.pathToFile)) {
            // NOTE: Create file if it doesn't exist
            this.createFile(fileURI);
        }
        this.bufferedWriter = Files.newBufferedWriter(this.pathToFile);
    }

    // MARK: - Public methods

    public void stopWriting() throws IOException {
        if (!this.isActive) {
            // NOTE: Nothing to be stopped in case logger is not active
            return;
        }
        this.bufferedWriter.flush();
        this.bufferedWriter.close();
    }

    // MARK: - Private methods
    private boolean hasRequiredExtension(String filename) {
        return filename.contains(InsertionFileLogger.REQUIRED_FILE_EXTENSION);
    }


    private boolean shouldCreateLogger(String fileURI) {
        return (!fileURI.equals(Constants.NO_OUTPUT_REQUIRED_FILENAME));
    }

    private void createFile(String name) throws IOException {
        File newFile = new File(name);
        BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
        writer.close();
    }

    private boolean isFileExist(Path path) {
        return (path != null);
    }


    // MARK: - Overrides
    @Override
    public void logOperation(String targetDatabase, String targetTable, String insertedKey, String operationDuration) throws IOException {
        if (!this.isActive) {
            System.err.println(InsertionFileLogger.TAG + " Operation couldn't be logged since logger is not active.");
            return;
        }
        this.bufferedWriter.write(targetDatabase + "," + targetTable + "," + insertedKey + "," + operationDuration + "\n");
    }

}
