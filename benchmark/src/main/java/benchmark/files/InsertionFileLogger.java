package benchmark.files;

import benchmark.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InsertionFileLogger implements IInsertionsFileLogger {

    private Path pathToFile;
    private final static String REQUIRED_FILE_EXTENSION = ".csv";
    private boolean isActive;
    private BufferedWriter bufferedWriter;

    public InsertionFileLogger(String fileURI) throws IOException {
        System.err.println("Creating file logger, URL: " + fileURI);
        if (!this.shouldCreateLogger(fileURI)) {
            System.err.println("Shuldn't be created");
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

    private boolean hasRequiredExtension(String filename) {
        return filename.contains(InsertionFileLogger.REQUIRED_FILE_EXTENSION);
    }

    public boolean isActive() {
        return this.isActive;
    }

    private boolean shouldCreateLogger(String fileURI) {
        return (!fileURI.equals(Constants.NO_OUTPUT_REQUIRED_FILENAME));
    }

    private void createFile(String name) throws IOException {
        File newFile = new File(name);
        BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
        writer.close();
    }


    @Override
    public void logOperation(String targetDatabase, String targetTable, String insertedKey, String operationDuration) throws IOException {
        System.out.println("Logged");
        this.bufferedWriter.write(targetDatabase + "," + targetTable + "," + insertedKey + "," + operationDuration + "\n");
    }

    public void stopWriting() throws IOException {
        this.bufferedWriter.flush();
        this.bufferedWriter.close();
    }

    private boolean isFileExist(Path path) {
        return (path != null);
    }
}
