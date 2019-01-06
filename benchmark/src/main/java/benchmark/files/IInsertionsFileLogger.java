package benchmark.files;

import java.io.IOException;

public interface IInsertionsFileLogger {
    // NOTE: Required parameters are: DB name, destination table name, key, measured duration
    void logOperation(String targetDatabase, String targetTable, String insertedKey, String operationDuration) throws IOException;
}
