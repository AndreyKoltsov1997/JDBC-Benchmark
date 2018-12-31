package benchmark;

import benchmark.common.RandomAsciiStringGenerator;
import benchmark.database.DatabaseInfo;

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
        Runnable insertTask = () -> {
            while (this.shouldContinueInserting()) {
                RandomAsciiStringGenerator randomAsciiStringGenerator = new RandomAsciiStringGenerator(this.payload);
                final String randomString = randomAsciiStringGenerator.getRandomString();
                String testKey = "inserting string: " + randomString;
                System.out.println(testKey);
            }
        };

        final int amountOfThreads = this.amountOfThreads;
        for (int i = 0; i < amountOfThreads; ++i) {
            Thread thread = new Thread(insertTask);
            thread.start();
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
