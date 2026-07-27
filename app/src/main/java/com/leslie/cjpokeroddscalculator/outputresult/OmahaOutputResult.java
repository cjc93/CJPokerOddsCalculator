package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;

import java.util.function.Function;

public abstract class OmahaOutputResult extends OutputResult {
    public int totalSimulations;
    public Function<double[][], double[][]> averageUnknownStats;

    public OmahaOutputResult(EquityCalculatorViewModel equityCalculatorViewModel) {
        super(equityCalculatorViewModel);
    }

    public abstract void beforeAllSimulations();

    public abstract void duringSimulations(Equity[] eqs, int count) throws InterruptedException;

    public abstract void afterAllSimulations(Equity[] eqs) throws InterruptedException;
}
