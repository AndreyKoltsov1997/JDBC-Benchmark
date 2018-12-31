package benchmark;

import benchmark.common.RandomAsciiStringGenerator;
import benchmark.database.DatabaseInfo;

public class DatabaseBenchmark {

    private final int DEFAULT_PAYLOAD = 0;
    private final int DEFAULT_AMOUNT_OF_THREDS = 1;
    private final String NO_OUTPUT_FILE_REQUIRED_FILENAME = "";


    private DatabaseInfo databaseInfo;
    private final int payload;
    private final int amountOfThreads;
    private final String outputFileName;

    // MARK: - Constructors

    public DatabaseBenchmark(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;

        this.payload = this.DEFAULT_PAYLOAD;
        this.amountOfThreads = this.DEFAULT_AMOUNT_OF_THREDS;
        this.outputFileName = this.NO_OUTPUT_FILE_REQUIRED_FILENAME;
    }

    public DatabaseBenchmark(int payload, int amountOfThreads, DatabaseInfo databaseInfo) {
        this.payload = payload;
        this.amountOfThreads = amountOfThreads;
        this.databaseInfo = databaseInfo;
        this.outputFileName = this.NO_OUTPUT_FILE_REQUIRED_FILENAME;
    }

    public DatabaseBenchmark(int payload, int amountOfThreads, DatabaseInfo databaseInfo, String outputFileName) {
        this.payload = payload;
        this.amountOfThreads = amountOfThreads;
        this.databaseInfo = databaseInfo;
        this.outputFileName = outputFileName;
    }


    // MARK: - Public methods

    public Boolean isFileOutputRequired() {
        return (outputFileName.equals(this.NO_OUTPUT_FILE_REQUIRED_FILENAME));
    }

    public void performBenchmark() {
        Runnable insertTask = () -> {
            RandomAsciiStringGenerator randomAsciiStringGenerator = new RandomAsciiStringGenerator(this.payload);
            final String randomString = randomAsciiStringGenerator.getRandomString();
            String testKey = "inserting string: " + randomString;
            System.out.println(testKey);
        };

        final int amountOfThreads = this.amountOfThreads;
        for (int i = 0; i < amountOfThreads; ++i) {
            Thread thread = new Thread(insertTask);
            thread.start();
        }
    }

    // MARK: - Private methods


}
