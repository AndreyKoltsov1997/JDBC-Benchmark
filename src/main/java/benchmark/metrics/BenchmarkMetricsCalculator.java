package benchmark.metrics;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


// NOTE: A class responsible for calculation of required benchmark metrics.
public class BenchmarkMetricsCalculator {

    private AtomicInteger insertedOperations;
    private AtomicLong microsecondsSpendOnInsertions;
    private AtomicInteger bytesInserted;




    public BenchmarkMetricsCalculator() {
        final int initialMetricValue = 0;
        this.insertedOperations = new AtomicInteger(initialMetricValue);
        this.microsecondsSpendOnInsertions = new AtomicLong(initialMetricValue);
        this.bytesInserted = new AtomicInteger(initialMetricValue);
    }

    public synchronized void incrementSuccessfulInsertions() {
        final int amountOfSuccessfulInsertions =  this.insertedOperations.incrementAndGet();
        System.out.println("Adding successful insertion: " + amountOfSuccessfulInsertions + " from " + Thread.currentThread().getName());
        System.out.println("Update amount of insertions: " + this.insertedOperations.get());
    }

    public synchronized void addMicrosecondsSpentOnInsertion(final Long microseconds) {
        Long microsecondsSpentInTotal = this.microsecondsSpendOnInsertions.get();
        Long microsecondsSpentUpdatedValue = microsecondsSpentInTotal + microseconds;
        this.microsecondsSpendOnInsertions.set(microsecondsSpentUpdatedValue);
    }

    public synchronized void addBytesInserted(final int amountOfBytes) {
        final int updatedPayloadInserted = this.bytesInserted.get() + amountOfBytes;
        this.bytesInserted.set(updatedPayloadInserted);
    }


    // NOTE: Calculation of insert operations per second
    public double getAverageThroughput() {
        if (!this.hasSuccessfulOperations()) {
            System.out.println("No successful operation has been found. Throughput is zero.");
            final double zeroThroughput = 0.0;
            return zeroThroughput;
        }
        System.out.println("[Throughtput] Microsecodns spent on insertion: " + this.microsecondsSpendOnInsertions.get());
        final double secondsSpentOnInsertion = convertMicrosecondsToSeconds(this.microsecondsSpendOnInsertions.get());
        final double result = insertedOperations.get() / secondsSpentOnInsertion;
        System.out.println("Throughout, seconds spent on insertion: " + secondsSpentOnInsertion);
        System.out.println("Throughout, insertedOperations: " + insertedOperations.get());

        return result;
    }

    // NOTE: Calculation of payload bytes inserted per second
    public double getBandwidth() {
        if (!this.hasSuccessfulOperations()) {
            final double zeroBandwidth = 0.0;
            return zeroBandwidth;
        }
        final double secondsSpentOnInsertion = convertMicrosecondsToSeconds(this.microsecondsSpendOnInsertions.get());
        System.out.println("[Bandwidth] for " + Thread.currentThread().getName() + "secondsSpentOnInsertion " + secondsSpentOnInsertion);
        System.out.println("[Bandwidth] for " + Thread.currentThread().getName() + " Payload inserted: " + bytesInserted.get());

        final double result = bytesInserted.get() / secondsSpentOnInsertion;
        return result;
    }


    private double convertMicrosecondsToSeconds(Long microseconds) {
        final int secondsToMicrosecondsPower = -6;
        final int convertionBase = 10;
        final double secondsToMicrosecondsMultiplicator = Math.pow(convertionBase, secondsToMicrosecondsPower);
        final double result = microseconds * secondsToMicrosecondsMultiplicator;
        return result;
    }


    // NOTE: Benchmark has successful operations in case any value has been inserted.
    private boolean hasSuccessfulOperations() {
        System.out.println("this.insertedOperations.get() inside " + Thread.currentThread().getName() + ": " + this.insertedOperations.get());
        System.out.println("this.microsecondsSpendOnInsertions.get() inside " + Thread.currentThread().getName() + ": " + this.microsecondsSpendOnInsertions.get());

        return ((this.insertedOperations.get() > 0) && (this.microsecondsSpendOnInsertions.get() > 0));
    }

}
