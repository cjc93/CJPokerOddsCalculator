package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;

public abstract class TexasHoldemOutputResult extends OutputResult {
    public Thread currentThread;

    public TexasHoldemOutputResult(EquityCalculatorFragment equityCalculatorFragment, EquityCalculatorViewModel equityCalculatorViewModel) {
        super(equityCalculatorFragment, equityCalculatorViewModel);
        this.currentThread = Thread.currentThread();
    }

    public abstract boolean duringSimulations(double[]... result);

    public abstract void afterAllSimulations(double[][] results, boolean... isCancelled);
}
