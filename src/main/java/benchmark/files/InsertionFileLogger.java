package benchmark.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InsertionFileLogger implements IInsertionsFileLogger {
    private Path pathToFile;

    public InsertionFileLogger(String fileURI) throws IOException {
        this.pathToFile = Paths.get(fileURI);
        if (this.isFileExist(this.pathToFile)) {
            final String misleadingMsg = "File hasn't been found at URI: " + fileURI;
            this.createFile(fileURI);
        }


    }

    private void createFile(String name) throws IOException {
        File newFile = new File(name);
        BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
        writer.close();
    }

    @Override
    public void logOperation(String targetDatabase, String targetColumn, String operationDuration) {
        // TODO: Impliment method
    }

    private boolean isFileExist(Path path) {
        return (path != null);
    }
}
