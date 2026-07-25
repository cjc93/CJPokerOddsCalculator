package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;

public class TexasHoldemLiveUpdate extends TexasHoldemOutputResult {

    public TexasHoldemLiveUpdate(EquityCalculatorViewModel equityCalculatorViewModel) {
        super(equityCalculatorViewModel);
    }

    @Override
    public boolean duringSimulations(double[]... results) {
        if (this.currentThread.isInterrupted()) {
            return false;
        }

        updateWinResults(results);
        return true;
    }

    @Override
    public void afterAllSimulations(double[][] results, boolean... isCancelled) {
        if (!Thread.interrupted()) {
            if (equityCalculatorViewModel.exactCalcThread.isAlive()) {
                updateWinResults(results);
            } else {
                updateWinResults(results);
                updateResDesc(R.string.finished_checking_random_subset);
            }
        }
    }
}
