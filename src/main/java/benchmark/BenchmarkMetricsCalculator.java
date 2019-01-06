package benchmark;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BenchmarkMetricsCalculator {

    private AtomicInteger insertedOperations;
    private AtomicInteger macrosecondsSpendOnInsertions;
    private AtomicInteger bytesInserted;

    // NOTE: Storing list of overflowed int values in case of infinite insertion
    private List<Integer> overflowedMacrosecondValues;


    public BenchmarkMetricsCalculator() {
        final int initialMetricValue = 0;
        this.insertedOperations = new AtomicInteger(initialMetricValue);
        this.macrosecondsSpendOnInsertions = new AtomicInteger(initialMetricValue);
        this.bytesInserted = new AtomicInteger(initialMetricValue);
        this.overflowedMacrosecondValues = new LinkedList<>();
    }

    public synchronized void incrementSuccessfulInsertions() {
        this.insertedOperations.incrementAndGet();
    }

    public synchronized void addMacrosecondsSpentOnInsertion(final Long macroseconds) {
        int macrosecondsSpentOnInsertion = this.safeLongToInt(macroseconds);
//        try {
//            macrosecondsSpentOnInsertion = this.safeLongToInt(macroseconds);
//        } catch (IllegalArgumentException error) {
//            // NOTE: Handling integer overflow
//            overflowedMacrosecondValues.add(Integer.MAX_VALUE);
//            final int macrosecondsCounterResetValue = 0;
//            this.macrosecondsSpendOnInsertions.set(macrosecondsCounterResetValue);
//            return;
//
//        }
        final int updatedTime = this.macrosecondsSpendOnInsertions.get() + macrosecondsSpentOnInsertion;
        this.macrosecondsSpendOnInsertions.set(updatedTime);
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
        final double secondsSpentOnInsertion = this.getSecondsSpentOnInsertion();
        final double result = insertedOperations.get() / secondsSpentOnInsertion;
        return result;
    }

    // NOTE: Calculation of payload bytes inserted per second
    public double getBandwidth() {
        if (!this.hasSuccessfulOperations()) {
            final double zeroBandwidth = 0.0;
            return zeroBandwidth;
        }
        final double secondsSpentOnInsertion = this.getSecondsSpentOnInsertion();
        System.out.println("[bandwidth] secondsSpentOnInsertion:" + secondsSpentOnInsertion);

        final double result = bytesInserted.get() / secondsSpentOnInsertion;
        return result;
    }


    private double getSecondsSpentOnInsertion() {
        final int secondsToMacrosecondsPower = -6;
        final int convertionBase = 10;
        System.out.println("this.macrosecondsSpendOnInsertions.get(): " + this.macrosecondsSpendOnInsertions.get());
        final double secondsToMacrosecondsMultiplicator = Math.pow(convertionBase, secondsToMacrosecondsPower);
        final double result = this.macrosecondsSpendOnInsertions.get() * secondsToMacrosecondsMultiplicator;
        return result;
    }

    private int safeLongToInt(long value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (value + " cannot be cast to int without changing its value.");
        }
        return (int) value;
    }

    private boolean hasSuccessfulOperations() {
        return ((this.insertedOperations.get() != 0) && (this.macrosecondsSpendOnInsertions.get() != 0));
    }

}
