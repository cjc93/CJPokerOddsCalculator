package com.leslie.cjpokeroddscalculator.calculation;

public interface TexasHoldemProgressListener {
    boolean duringSimulations(double[]... result);
    void afterAllSimulations(double[][] results, boolean... isCancelled);
}