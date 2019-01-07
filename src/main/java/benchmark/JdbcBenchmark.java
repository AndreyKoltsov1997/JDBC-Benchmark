package benchmark;

import benchmark.common.RandomAsciiStringGenerator;
import benchmark.database.DatabaseInfo;
import benchmark.files.InsertionFileLogger;
import benchmark.database.ColumnType;
import benchmark.jdbc.DatabaseOperator;
import benchmark.jdbc.JdbcCrudFailureException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class JdbcBenchmark {

    private final int DEFAULT_PAYLOAD = 0;
    private final int DEFAULT_AMOUNT_OF_THREDS = 1;
    private final String NO_OUTPUT_FILE_REQUIRED_FILENAME = "";
    // TODO: Replace this valid with value from Constants class
    private final int INFINITE_AMOUNT_OF_INSERTIONS = -9;
    private final static int KEY_LENGTH = 10;


    private DatabaseInfo databaseInfo;
    private final AtomicInteger totalPayload;
    private final int amountOfThreads;
    private final AtomicInteger amountOfInsertions;
    private final String outputFileName;
    private final int minimalPayloadPerInsertion;
    private BenchmarkMetricsCalculator benchmarkMetricsCalculator = new BenchmarkMetricsCalculator();


    // MARK: - Constructors

    public JdbcBenchmark(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;

        this.totalPayload = new AtomicInteger(this.DEFAULT_PAYLOAD);
        this.amountOfThreads = this.DEFAULT_AMOUNT_OF_THREDS;
        this.outputFileName = this.NO_OUTPUT_FILE_REQUIRED_FILENAME;
        this.amountOfInsertions = new AtomicInteger(this.INFINITE_AMOUNT_OF_INSERTIONS);
        this.minimalPayloadPerInsertion = this.getMinimalPayloadPerInsertion();
    }

    public JdbcBenchmark(int totalPayload, int amountOfThreads, int amountOfInsertions, DatabaseInfo databaseInfo) {
        this.totalPayload = new AtomicInteger(totalPayload);
        this.amountOfThreads = amountOfThreads;
        this.amountOfInsertions = new AtomicInteger(amountOfInsertions);
        this.databaseInfo = databaseInfo;
        this.outputFileName = this.NO_OUTPUT_FILE_REQUIRED_FILENAME;
        this.minimalPayloadPerInsertion = this.getMinimalPayloadPerInsertion();

    }

    public JdbcBenchmark(int totalPayload, int amountOfThreads, int amountOfInsertions, DatabaseInfo databaseInfo, String outputFileName) {
        // NOTE: Random ASCII generator uses UTF-8 encoding, that means we have 1 byte per symbol in a string
        this.totalPayload = new AtomicInteger(totalPayload);
        this.amountOfThreads = amountOfThreads;
        this.amountOfInsertions = new AtomicInteger(amountOfInsertions);
        this.databaseInfo = databaseInfo;
        this.outputFileName = outputFileName;
        this.minimalPayloadPerInsertion = this.getMinimalPayloadPerInsertion();
        System.out.println("Minimal payload per insertion: " + this.getMinimalPayloadPerInsertion());
    }


    // MARK: - Public methods

    public Boolean isFileOutputRequired() {
        return (outputFileName.equals(this.NO_OUTPUT_FILE_REQUIRED_FILENAME));
    }

    public void performBenchmark() {

        DatabaseOperator databaseOperator = new DatabaseOperator(this.databaseInfo);
        try {
            databaseOperator.establishConnection();
        } catch (SQLException error) {
            System.err.println("Unable to establish connection with the database at " + this.databaseInfo.getDatabaseURL() + ", reason: " + error.getMessage());
            System.exit(Constants.CONNECTION_ERROR);
        } catch (JdbcCrudFailureException error) {
            System.err.println("An error has occurred while performing CRUD operations with database: " + error.getMessage());
        }
        //        final String DB_NAME_MOCK = "test";
        final String TABLE_NAME_MOCK = this.databaseInfo.getTargetTable(); // DEBUG: "link";
        final String COLUMN_KEY_NAME_MOCK = "key";
        final String COLUMN_VALUE_NAME_MOCK = "value";
        final String VARCHAR_TYPE = "VARCHAR(10)";


        try {
            this.performInsertionTest(databaseOperator);
        } catch (IOException error) {
            final String misleadingMsg = "An error has occurred while working with file: " + error.getMessage();
            System.err.println(misleadingMsg);
        }

        try {
            databaseOperator.shutDownConnection();
        } catch (SQLException error) {
            System.err.println("Unexcpected error has occured while shutting down the connrection: " + error.getMessage());
        }

    }



    private String getRandomStringForBenchmark(RandomAsciiStringGenerator randomAsciiStringGenerator, ColumnType columnType) {
        String randomString = "";
        randomAsciiStringGenerator = new RandomAsciiStringGenerator();
        final int payloadLeft = this.decrementPayload();

        switch (columnType) {
            case KEY:
                // NOTE: Key has fixed length. So, even if there's not enough payload, it will be inserted.
                randomString = randomAsciiStringGenerator.getRandomString(JdbcBenchmark.KEY_LENGTH);
                break;
            case VALUE:
                if (payloadLeft < this.minimalPayloadPerInsertion) {
                    // NOTE: If the payload left is smaller than the required minimum (e.g.: when ..
                    // ... left payload is equal to reminder of the division
                    randomString = randomAsciiStringGenerator.getRandomString(this.totalPayload.get());
                } else {
                    randomString = randomAsciiStringGenerator.getRandomString(this.minimalPayloadPerInsertion);
                }
                break;
        }

        return randomString;
    }



    // NOTE: Testing INSERT operations via JDBC connector into the specified database.
    // ... DatabaseOperator is an object which perform insert operations
    private void performInsertionTest(DatabaseOperator databaseOperator) throws IOException {

        // TODO: Do something with insertion file logger - it shouldn't be created if not needed
        InsertionFileLogger insertionFileLogger = new InsertionFileLogger(this.outputFileName);
        Runnable insertTask = () -> {
            // NOTE: Creating random string generator once so it won't be created each insertion
            // WARNING: Insertions could be infinite
            RandomAsciiStringGenerator randomAsciiStringGenerator = new RandomAsciiStringGenerator();
            while (this.shouldContinueInserting()) {

                // NOTE: Not using JDBC Batch since we're trying to get average insertion time, ...
                // ... thus we should calculate each insertion operation.

                Map<String, String> insertingValues = new HashMap<String, String>();
                final String key = this.getRandomStringForBenchmark(randomAsciiStringGenerator, ColumnType.KEY);
                insertingValues.put(Constants.KEY_COLUMN_NAME, key);
                final String value = this.getRandomStringForBenchmark(randomAsciiStringGenerator, ColumnType.VALUE);
                insertingValues.put(Constants.VALUE_COLUMN_NAME, value);


                for (Map.Entry<String, String> insertingRow: insertingValues.entrySet()) {
                    // NOTE: Inserting key and value separately. It's 2 different INSERT operations and ...
                    // ... should be logged separately.
                    try {
                        final String insertingString = insertingRow.getValue();
                        Long insertionStartTime = System.nanoTime();
                        databaseOperator.insertSpecifiedValue(insertingRow);
                        Long currentInsertionTime = System.nanoTime() - insertionStartTime;
                        System.out.println("Total insertion time time: " + this.convertNanoSecondToMicroseconds(currentInsertionTime) + " microseconds.");
                        if ((insertionFileLogger).isActive()) {
                            insertionFileLogger.logOperation(this.databaseInfo.getTargetDatabaseName(), this.databaseInfo.getTargetTable(), insertingString, String.valueOf(currentInsertionTime));
                        }
                        final int payloadInserted = randomAsciiStringGenerator.getPayloadOfUTF8String(insertingString);
                        this.updateMetrics(payloadInserted, currentInsertionTime);

                    } catch (SQLException error) {
                        this.logFailedOperation(this.databaseInfo.getTargetDatabaseName(), this.databaseInfo.getTargetTable(), insertingRow.getValue(), error.getMessage());
                    } catch (Exception error) {
                        final String misleadingMsg = "An error has occurred while inserting new value into column: " + error.getMessage();
                        System.err.println(misleadingMsg);
                    }
                }

            }
        };

        final int amountOfThreads = this.amountOfThreads;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < amountOfThreads; ++i) {
            executorService.execute(insertTask);
        }

        executorService.shutdown();
        try {
            boolean hasInsertionFinished = executorService.awaitTermination(2, TimeUnit.MINUTES);
            if (hasInsertionFinished) {
                // TODO: Create a better way to stop writing. Maybe try-with-resources?
                ((InsertionFileLogger) insertionFileLogger).stopWriting();
                this.printBenchmarkResults();
            }
        } catch (Exception error) {
            System.err.println("Unable to finish executor service. Reason: " + error.getMessage());
            error.printStackTrace();
        }

    }


    // MARK: - Private methods


    private void updateMetrics(final int insertedPayload, final Long macrosecondsSpentOnInsertion) throws IllegalArgumentException {
        if (this.benchmarkMetricsCalculator == null) {
            throw new IllegalArgumentException("Benchmark metrics calculator hasn't been created, unable to update metrics.");
        }
        benchmarkMetricsCalculator.addBytesInserted(insertedPayload);
        benchmarkMetricsCalculator.addMacrosecondsSpentOnInsertion(macrosecondsSpentOnInsertion);
        benchmarkMetricsCalculator.incrementSuccessfulInsertions();

    }

    private void printBenchmarkResults() {
        final double averageThroughput = benchmarkMetricsCalculator.getAverageThroughput();
        final double bandWidth = benchmarkMetricsCalculator.getBandwidth();
        System.out.println("Average throughput: " + averageThroughput + ", bandwidth: " + bandWidth);
    }


    // NOTE: For each failed insert operation write the error message to the standard output (console) with DB name, destination table name, key and failure cause.
    private void logFailedOperation(final String dbName, final String targetTable, final String insertingKey, final String failureCause) {
        System.err.println("Failure: " + dbName + "," + targetTable + "," + insertingKey + "," + failureCause);
    }

    private Long convertNanoSecondToMicroseconds(Long nanoseconds) {
        final Integer MICRO_FROM_NANO_OFFSET = 1000;
        return (nanoseconds / MICRO_FROM_NANO_OFFSET);
    }

    private Boolean shouldContinueInserting() {
        if (this.isInsertionsInfinite()) {
            return true;
        }
        final int insertionsLeft = this.amountOfInsertions.get();
        if (insertionsLeft <= 0) {
            return false;
        }
        this.decrementInsertions();
        return true;
    }

    private synchronized int decrementInsertions() throws IllegalArgumentException {
        if (this.isInsertionsInfinite()) {
            throw new IllegalArgumentException("Amount of insertions is infinite and couldn't be decremented.");
        }
        // NOTE: It works correctly if .decrementAndGet() and setting a value are different operations
        this.amountOfInsertions.decrementAndGet();
        final int insertionsLeft = this.amountOfInsertions.get();
        return insertionsLeft;
    }


    private boolean isInsertionsInfinite() {
        return this.amountOfInsertions.get() == Constants.INFINITE_AMOUNT_OF_INSERTIONS;
    }
    // NOTE: Returning insertion payload, update unsent payload value
    private synchronized int decrementPayload() {
        int payloadLeft = this.totalPayload.get() - this.minimalPayloadPerInsertion;
        final int minimalAvailablePayloadValue = 0;
        if (payloadLeft < minimalAvailablePayloadValue) {
            // NOTE: Returning last positive value of payload
            payloadLeft = this.totalPayload.get();
            this.totalPayload.set(minimalAvailablePayloadValue);
            return payloadLeft;
        }
        this.totalPayload.set(payloadLeft);

        return this.minimalPayloadPerInsertion;
    }



    private int getMinimalPayloadPerInsertion() {
        final int oneBytePerInsertion = 1;
        if (this.amountOfInsertions.get() == this.INFINITE_AMOUNT_OF_INSERTIONS) {
            // NOTE: If amount if infinite, inserting 1 byte per operation
            return oneBytePerInsertion;
        }
        System.out.println("Payload: " + this.totalPayload);
        System.out.println("amount of insertions: " + this.amountOfInsertions.get());
        int result = (this.totalPayload.get() / this.amountOfInsertions.get());
        if (result == 0) {
            // NOTE: If amount of insertions is the way bigger than payload, inserting 1 byte per operation
            result = oneBytePerInsertion;
        }
        return result;
    }

}
