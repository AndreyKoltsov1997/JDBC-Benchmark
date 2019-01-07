package benchmark.metrics;

import benchmark.common.AtomicFloat;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


// NOTE: A class responsible for calculation of required benchmark metrics.
public class BenchmarkMetricsCalculator {

    private AtomicInteger insertedOperations;
    private AtomicLong microsecondsSpendOnInsertions;
    private AtomicInteger bytesInserted;

    private AtomicFloat secondsSpentonInsertion;



    public BenchmarkMetricsCalculator() {
        final int initialMetricValue = 0;
        this.insertedOperations = new AtomicInteger(initialMetricValue);
        this.microsecondsSpendOnInsertions = new AtomicLong(initialMetricValue);
        this.bytesInserted = new AtomicInteger(initialMetricValue);
        this.secondsSpentonInsertion = new AtomicFloat(initialMetricValue);
    }

    public synchronized void incrementSuccessfulInsertions() {
        final int amountOfSuccessfulInsertions =  this.insertedOperations.incrementAndGet();
        System.out.println("Adding successful insertion: " + amountOfSuccessfulInsertions);
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
        final double secondsSpentOnInsertion = convertMacrosecondsToSeconds(this.microsecondsSpendOnInsertions.get());
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
        final double secondsSpentOnInsertion = convertMacrosecondsToSeconds(this.microsecondsSpendOnInsertions.get());
        System.out.println("[Bandwidth] secondsSpentOnInsertion: " + secondsSpentOnInsertion);
        System.out.println("[Bandwidth] Paload inserted: " + bytesInserted.get());

        final double result = bytesInserted.get() / secondsSpentOnInsertion;
        return result;
    }


    private double convertMacrosecondsToSeconds(Long microseconds) {
        final int secondsToMacrosecondsPower = -6;
        final int convertionBase = 10;
        final double secondsToMacrosecondsMultiplicator = Math.pow(convertionBase, secondsToMacrosecondsPower);
        System.out.println("[convertMacrosecondsToSeconds]microsecondsSpendOnInsertions: " + microseconds);
        final double result = microseconds * secondsToMacrosecondsMultiplicator;
        return result;
    }

    private float convertMacrosecondsToSeconds(int macroseconds) {
        final int secondsToMacrosecondsPower = -6;
        final int convertionBase = 10;
        final float secondsToMacrosecondsMultiplicator = (float) Math.pow(convertionBase, secondsToMacrosecondsPower);
        return  macroseconds * secondsToMacrosecondsMultiplicator;
    }


    private int safeLongToInt(long value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (value + " cannot be cast to int without changing its value.");
        }
        return (int) value;
    }

    // NOTE: Benchmark has successful operations in case any value has been inserted.
    private boolean hasSuccessfulOperations() {
        return ((this.insertedOperations.get() > 0) && (this.microsecondsSpendOnInsertions.get() > 0));
    }

}
