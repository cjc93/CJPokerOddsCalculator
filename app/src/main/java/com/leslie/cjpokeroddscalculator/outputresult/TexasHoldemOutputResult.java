package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;

public abstract class TexasHoldemOutputResult extends OutputResult {
    public Thread currentThread;

    public TexasHoldemOutputResult(EquityCalculatorFragment equityCalculatorFragment) {
        super(equityCalculatorFragment);
        this.currentThread = Thread.currentThread();
    }

    public abstract boolean duringSimulations(double[]... result);

    public abstract void afterAllSimulations(double[][] results, boolean... isCancelled);
}
