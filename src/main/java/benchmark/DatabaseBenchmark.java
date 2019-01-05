package benchmark;

import benchmark.common.AtomicFloat;
import benchmark.common.RandomAsciiStringGenerator;
import benchmark.database.DatabaseInfo;
import benchmark.files.IInsertionsFileLogger;
import benchmark.files.InsertionFileLogger;
import benchmark.jdbc.JdbcRowInserter;

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

        JdbcRowInserter jdbcRowInserter = new JdbcRowInserter(this.databaseInfo);
        final String DB_NAME_MOCK = "test";
        final String TABLE_NAME_MOCK = "link";
        final String COLUMN_KEY_NAME_MOCK = "key";
        final String COLUMN_VALUE_NAME_MOCK = "value";
        final String VARCHAR_TYPE = "VARCHAR(10)";
        try {
            jdbcRowInserter.createColumn(TABLE_NAME_MOCK, COLUMN_KEY_NAME_MOCK, VARCHAR_TYPE);


        } catch (SQLException error) {
            System.out.println("Unable to create required column. Reason: " + error.getMessage());
        }

        try {
            this.performInsertionTest(jdbcRowInserter);
        } catch (IOException error) {
            final String misleadingMsg = "An error hsa occured while working with file: " + error.getMessage();
            System.err.println(misleadingMsg);
        }



    }

    // NOTE: Testing INSERT operations via JDBC connector into the specified database.
    // ... JdbcRowInserter is an object which perform insert operations
    private void performInsertionTest(JdbcRowInserter jdbcRowInserter) throws IOException {
        // TODO: Do something with insertion file logger - it shouldn't be created if not needed
        IInsertionsFileLogger insertionFileLogger = new InsertionFileLogger(this.outputFileName);
        Runnable insertTask = () -> {
            while (this.shouldContinueInserting()) {

                RandomAsciiStringGenerator randomAsciiStringGenerator = new RandomAsciiStringGenerator();
                String randomString = "";

                final int payloadLeft = this.decrementPayload();
//                System.out.println("========");
//                System.out.println("Payload left: " + payloadLeft + "modifying from Thread: " + Thread.currentThread().getName());
//                System.out.println("Total payload: " + this.totalPayload.get());
//                System.out.println("Minimal payload: " + this.minimalPayloadPerInsertion);
//                System.out.println("========");

                if (payloadLeft < this.minimalPayloadPerInsertion) {
                    // NOTE: If the payload left is smaller than the required minimum (e.g.: when ..
                    // ... left paload is equal to reminder of the division
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
                    jdbcRowInserter.insertValueIntoColumn(COLUMN_KEY_NAME_MOCK, randomString);
                    Long currentInsertionTime = System.nanoTime() - insertionStartTime;
                    System.out.println("Total insertion time time: " + this.convertNanoSecondToMicroseconds(currentInsertionTime) + " microseconds.");
                    if (insertionFileLogger != null) {
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
                    System.out.println(misleadingMsg);
                }

            }
        };

        final int amountOfThreads = this.amountOfThreads;
        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 0; i < amountOfThreads; ++i) {
            es.execute(insertTask);
        }
        es.shutdown();
        try {
            boolean finshed = es.awaitTermination(1, TimeUnit.MINUTES);
            if (finshed) {
                // TODO: Create a better way to stop writing. Maybe try-with-resources?
                ((InsertionFileLogger) insertionFileLogger).stopWriting();
                System.out.println("Insertion has been finished");
                this.printBenchmarkResults();
            }
        } catch (Exception error) {
            System.out.println("Unable to finish excecutor service: " + error.getLocalizedMessage());
        }

    }


    // MARK: - Private methods


    private void updateMetrics(final int insertedPayload, final Long macrosecondsSpentOnInsertion) {
        System.out.println("updating metrics..");
        benchmarkMetricsCalculator.addBytesInserted(insertedPayload);
        benchmarkMetricsCalculator.addMacrosecondsSpentOnInsertion(macrosecondsSpentOnInsertion);
        benchmarkMetricsCalculator.incrementSuccessfulInsertions();
    }

    private void printBenchmarkResults() {
        final double averageThroughtput = benchmarkMetricsCalculator.getAverageThroughtput();
        final double bandWidth = benchmarkMetricsCalculator.getBandwidth();
        System.out.println("Average thoughtput: " + averageThroughtput + ", bendwidth: " + bandWidth);
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
        final int insertionsLeft = this.decrementInsertions();

        if (insertionsLeft == this.INFINITE_AMOUNT_OF_INSERTIONS) {
            return true;
        }
        return (insertionsLeft >= 0);
    }

    private synchronized int decrementInsertions() {
        // NOTE: It works correctly if .decrementAndGet() and setting a value are different operations
        this.amountOfInsertions.decrementAndGet();
        final int insertionsLeft = this.amountOfInsertions.get();
        return insertionsLeft;
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
