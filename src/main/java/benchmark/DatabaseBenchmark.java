package benchmark;

import benchmark.common.RandomAsciiStringGenerator;
import benchmark.database.DatabaseInfo;
import benchmark.files.IInsertionsFileLogger;
import benchmark.files.InsertionFileLogger;
import benchmark.jdbc.DatabaseOperator;
import benchmark.jdbc.JdbcCrudFailureException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseBenchmark {

    private final int DEFAULT_PAYLOAD = 0;
    private final int DEFAULT_AMOUNT_OF_THREDS = 1;
    private final String NO_OUTPUT_FILE_REQUIRED_FILENAME = "";
    // TODO: Replace this valud with value from Constants class
    private final int INFINITE_AMOUNT_OF_INSERTIONS = -9;


    private DatabaseInfo databaseInfo;
    private final AtomicInteger totalPayload;
    private final int amountOfThreads;
    private final AtomicInteger amountOfInsertions;
    private final String outputFileName;
    private final int minimalPayloadPerInsertion;
    private BenchmarkMetricsCalculator benchmarkMetricsCalculator = new BenchmarkMetricsCalculator();

    // AtomicFloat is used to calculate benchmark's throughput dynamicly without storing ...
    // ... benchmark results in memory.
//    private AtomicFloat averageThroughput = new AtomicFloat((float) 0.0);

    // MARK: - Constructors

    public DatabaseBenchmark(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;

        this.totalPayload = new AtomicInteger(this.DEFAULT_PAYLOAD);
        this.amountOfThreads = this.DEFAULT_AMOUNT_OF_THREDS;
        this.outputFileName = this.NO_OUTPUT_FILE_REQUIRED_FILENAME;
        this.amountOfInsertions = new AtomicInteger(this.INFINITE_AMOUNT_OF_INSERTIONS);
        this.minimalPayloadPerInsertion = this.getMinimalPayloadPerInsertion();
    }

    public DatabaseBenchmark(int totalPayload, int amountOfThreads, int amountOfInsertions, DatabaseInfo databaseInfo) {
        this.totalPayload = new AtomicInteger(totalPayload);
        this.amountOfThreads = amountOfThreads;
        this.amountOfInsertions = new AtomicInteger(amountOfInsertions);
        this.databaseInfo = databaseInfo;
        this.outputFileName = this.NO_OUTPUT_FILE_REQUIRED_FILENAME;
        this.minimalPayloadPerInsertion = this.getMinimalPayloadPerInsertion();

    }

    public DatabaseBenchmark(int totalPayload, int amountOfThreads, int amountOfInsertions, DatabaseInfo databaseInfo, String outputFileName) {
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

    // NOTE: Testing INSERT operations via JDBC connector into the specified database.
    // ... DatabaseOperator is an object which perform insert operations
    private void performInsertionTest(DatabaseOperator databaseOperator) throws IOException {

        // TODO: Do something with insertion file logger - it shouldn't be created if not needed
        IInsertionsFileLogger insertionFileLogger = new InsertionFileLogger(this.outputFileName);
        Runnable insertTask = () -> {
            while (this.shouldContinueInserting()) {

                RandomAsciiStringGenerator randomAsciiStringGenerator = new RandomAsciiStringGenerator();
                String randomString = "";

                final int payloadLeft = this.decrementPayload();

                if (payloadLeft < this.minimalPayloadPerInsertion) {
                    // NOTE: If the payload left is smaller than the required minimum (e.g.: when ..
                    // ... left payload is equal to reminder of the division
                    randomString = randomAsciiStringGenerator.getRandomString(this.totalPayload.get());
                } else {
                    randomString = randomAsciiStringGenerator.getRandomString(this.minimalPayloadPerInsertion);
                }


                System.out.println("Inserting random string: " + randomString);

                final String COLUMN_KEY_NAME_MOCK = "key";
                final String COLUMN_VALUE_NAME_MOCK = "value";

                // NOTE: Not using JDBC Batch since we're trying to get average insertion time, ...
                // ... thus we should calculate each insertion operation.

                try {
                    Long insertionStartTime = System.nanoTime();
                    databaseOperator.insertValueIntoColumn(COLUMN_KEY_NAME_MOCK, randomString);
                    Long currentInsertionTime = System.nanoTime() - insertionStartTime;
                    System.out.println("Total insertion time time: " + this.convertNanoSecondToMicroseconds(currentInsertionTime) + " microseconds.");
                    if (((InsertionFileLogger) insertionFileLogger).isActive()) {
                        insertionFileLogger.logOperation(this.databaseInfo.getTargetDatabaseName(), this.databaseInfo.getTargetTable(), randomString, String.valueOf(currentInsertionTime));
                    }

                    // NOTE: Updating metrics in case of successful insertion
                    // TODO: Add payload for BOTH key and value
                    final int payloadInserted = randomAsciiStringGenerator.getPayloadOfUTF8String(randomString);
                    this.updateMetrics(payloadInserted, currentInsertionTime);

                } catch (SQLException error) {
                    this.logFailedOperation(this.databaseInfo.getTargetDatabaseName(), this.databaseInfo.getTargetTable(), randomString, error.getMessage());
                } catch (Exception error) {
                    final String misleadingMsg = "An error has occured while inserting new value into column: " + error.getMessage();
                    System.err.println(misleadingMsg);
                }

            }
        };

        final int amountOfThreads = this.amountOfThreads;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < amountOfThreads; ++i) {
            executorService.execute(insertTask);
        }

        System.out.println("SHUTTING DOWN ES!!!");
        executorService.shutdown();
        try {
            boolean hasInsertionFinished = executorService.awaitTermination(2, TimeUnit.MINUTES);
            if (hasInsertionFinished) {
                // TODO: Create a better way to stop writing. Maybe try-with-resources?
                ((InsertionFileLogger) insertionFileLogger).stopWriting();
                System.out.println("Insertion has been finished");
                this.printBenchmarkResults();
            }
        } catch (Exception error) {
            System.err.println("Unable to finish executor service");
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
        final double averageThroughput = benchmarkMetricsCalculator.getAverageThroughtput();
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
        final int minimalAvaliablePayloadValue = 0;
        if (payloadLeft < minimalAvaliablePayloadValue) {
            // NOTE: Returning last positive value of payload
            payloadLeft = this.totalPayload.get();
            this.totalPayload.set(minimalAvaliablePayloadValue);
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
