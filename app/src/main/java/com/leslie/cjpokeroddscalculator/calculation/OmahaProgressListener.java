package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;

import java.util.function.Function;

public interface OmahaProgressListener {
    void setTotalSimulations(int totalSimulations);
    void setAverageUnknownStats(Function<double[][], double[][]> averageUnknownStats);
    void beforeAllSimulations();
    void duringSimulations(Equity[] eqs, int count) throws InterruptedException;
    void afterAllSimulations(Equity[] eqs) throws InterruptedException;
}