package benchmark.files;

public interface IInsertionsFileLogger {
    //  DB name, destination table name, key, measured duration
    void logOperation(String targetDatabase, String targetColumn, String operationDuration);
}
