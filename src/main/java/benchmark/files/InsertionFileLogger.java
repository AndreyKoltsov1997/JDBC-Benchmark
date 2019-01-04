package benchmark.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InsertionFileLogger implements IInsertionsFileLogger {
    private Path pathToFile;
    private BufferedWriter bufferedWriter;

    public InsertionFileLogger(String fileURI) throws IOException {
        this.pathToFile = Paths.get(fileURI);
        if (!this.isFileExist(this.pathToFile)) {
            final String misleadingMsg = "File hasn't been found at URI: " + fileURI;
            this.createFile(fileURI);
        }
        this.bufferedWriter = Files.newBufferedWriter(this.pathToFile);


    }

    private void createFile(String name) throws IOException {
        File newFile = new File(name);
        BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
        writer.close();
    }


    @Override
    public void logOperation(String targetDatabase, String targetTable, String insertedKey, String operationDuration) throws IOException {
        // TODO: Impliment method
        System.out.println("Logged");

        this.bufferedWriter.write(targetDatabase + "," + targetTable + "," + insertedKey + "," + operationDuration + "\n");
//        this.stopWriting();
    }

    public void stopWriting() throws IOException {
        this.bufferedWriter.flush();
        this.bufferedWriter.close();
    }

    private boolean isFileExist(Path path) {
        return (path != null);
    }
}
