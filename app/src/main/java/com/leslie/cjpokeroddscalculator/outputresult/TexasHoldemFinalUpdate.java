package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;

public class TexasHoldemFinalUpdate extends TexasHoldemOutputResult {

    public TexasHoldemFinalUpdate(EquityCalculatorViewModel equityCalculatorViewModel) {
        super(equityCalculatorViewModel);
    }

    @Override
    public boolean duringSimulations(double[]... result) {
        if (this.currentThread.isInterrupted()) {
            return false;
        }

        updateResDesc(R.string.checking_combinations);

        return true;
    }

    @Override
    public void afterAllSimulations(double[][] results, boolean... isCancelled) {
        if (!Thread.interrupted()) {
            if (equityCalculatorViewModel.monteCarloThread.isAlive()) {
                if (isCancelled[0]) {
                    updateResDesc(R.string.checking_random_subset);
                } else {
                    equityCalculatorViewModel.monteCarloThread.interrupt();
                    updateWinResults(results);
                    updateResDesc(R.string.all_combinations_checked_result_is_exact);
                }
            } else {
                if (isCancelled[0]) {
                    updateResDesc(R.string.finished_checking_random_subset);
                } else {
                    updateWinResults(results);
                    updateResDesc(R.string.all_combinations_checked_result_is_exact);
                }
            }
        }
    }

}
