package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.R;

public abstract class TexasHoldemOutputResult extends OutputResult {
    public Thread currentThread;

    public TexasHoldemOutputResult(EquityCalculatorFragment equityCalculatorFragment) {
        super(equityCalculatorFragment);
        this.currentThread = Thread.currentThread();
    }

    public abstract boolean duringSimulations(double[]... result);

    public abstract void afterAllSimulations(double[] equity, double[] win, boolean... isCancelled);

    public void updateWinResults(double[] equity, double[] win) {
        if (equityCalculatorFragment.getActivity() != null) {
            for(int i = 0; i < equityCalculatorFragment.playersRemainingNo; i++) {
                equityCalculatorFragment.equityArray[i].setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, equity[i] * 100));
                equityCalculatorFragment.winArray[i].setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, win[i] * 100));
                equityCalculatorFragment.tieArray[i].setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, Math.abs(equity[i] - win[i]) * 100));
            }
        }
    }
}
