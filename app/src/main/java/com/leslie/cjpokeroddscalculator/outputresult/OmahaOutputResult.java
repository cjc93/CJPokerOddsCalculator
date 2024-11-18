package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.calculation.OmahaCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;

public abstract class OmahaOutputResult extends OutputResult {
    OmahaCalc omahaCalc;

    public OmahaOutputResult(EquityCalculatorFragment equityCalculatorFragment, OmahaCalc omahaCalc) {
        super(equityCalculatorFragment);
        this.omahaCalc = omahaCalc;
    }

    public abstract void beforeAllSimulations();

    public abstract void duringSimulations(Equity[] eqs, int count) throws InterruptedException;

    public abstract void afterAllSimulations(Equity[] eqs) throws InterruptedException;
}
