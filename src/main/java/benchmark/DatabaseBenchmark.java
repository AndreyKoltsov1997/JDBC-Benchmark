package benchmark;

import benchmark.common.RandomAsciiStringGenerator;
import benchmark.database.DatabaseInfo;
import benchmark.jdbc.JdbcRowInserter;

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
    private final int payload;
    private final int amountOfThreads;
    private final AtomicInteger amountOfInsertions;
    private final String outputFileName;

    // MARK: - Constructors

    public DatabaseBenchmark(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;

        this.payload = this.DEFAULT_PAYLOAD;
        this.amountOfThreads = this.DEFAULT_AMOUNT_OF_THREDS;
        this.outputFileName = this.NO_OUTPUT_FILE_REQUIRED_FILENAME;
        this.amountOfInsertions = new AtomicInteger(this.INFINITE_AMOUNT_OF_INSERTIONS);
    }

    public DatabaseBenchmark(int payload, int amountOfThreads, int amountOfInsertions, DatabaseInfo databaseInfo) {
        this.payload = payload;
        this.amountOfThreads = amountOfThreads;
        this.amountOfInsertions = new AtomicInteger(amountOfInsertions);
        this.databaseInfo = databaseInfo;
        this.outputFileName = this.NO_OUTPUT_FILE_REQUIRED_FILENAME;
    }

    public DatabaseBenchmark(int payload, int amountOfThreads, int amountOfInsertions, DatabaseInfo databaseInfo, String outputFileName) {
        this.payload = payload;
        this.amountOfThreads = amountOfThreads;
        this.amountOfInsertions = new AtomicInteger(amountOfInsertions);
        this.databaseInfo = databaseInfo;
        this.outputFileName = outputFileName;
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

        this.performInsertion(jdbcRowInserter);



    }

    private void performInsertion(JdbcRowInserter jdbcRowInserter) {
        Runnable insertTask = () -> {
            while (this.shouldContinueInserting()) {
                RandomAsciiStringGenerator randomAsciiStringGenerator = new RandomAsciiStringGenerator(this.payload);

                final String COLUMN_KEY_NAME_MOCK = "key";
                final String COLUMN_VALUE_NAME_MOCK = "value";

                try {
                    final String randomString = randomAsciiStringGenerator.getRandomString();
                    jdbcRowInserter.insertValueIntoColumn(COLUMN_KEY_NAME_MOCK, randomString);
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
                System.out.println("Insertion has been finished");
            }
        } catch (Exception error) {
            System.out.println("unable to finish excecutor service");
        }




    }


    // MARK: - Private methods



    private Boolean shouldContinueInserting() {
        final int insertionsLeft = this.decrementInsertions();

        if (insertionsLeft == this.INFINITE_AMOUNT_OF_INSERTIONS) {
            return true;
        }
        return (insertionsLeft >= 0);
    }

    private synchronized int decrementInsertions() {
        this.amountOfInsertions.decrementAndGet();
        final int insertionsLeft = this.amountOfInsertions.get();
        return insertionsLeft;
    }

}
