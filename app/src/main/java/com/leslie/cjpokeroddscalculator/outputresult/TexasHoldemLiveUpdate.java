package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.R;

public class TexasHoldemLiveUpdate extends TexasHoldemOutputResult {

    public TexasHoldemLiveUpdate(EquityCalculatorFragment equityCalculatorFragment) {
        super(equityCalculatorFragment);
    }

    @Override
    public boolean duringSimulations(double[]... results) {
        if (this.currentThread.isInterrupted()) {
            return false;
        }

        equityCalculatorFragment.requireActivity().runOnUiThread(() -> updateWinResults(results));
        return true;
    }

    @Override
    public void afterAllSimulations(double[][] results, boolean... isCancelled) {
        if (!Thread.interrupted()) {
            if (equityCalculatorFragment.exactCalcThread.isAlive()) {
                equityCalculatorFragment.requireActivity().runOnUiThread(() -> updateWinResults(results));
            } else {
                equityCalculatorFragment.requireActivity().runOnUiThread(() -> {
                    updateWinResults(results);
                    updateResDesc(R.string.finished_checking_random_subset);
                });
            }
        }
    }
}
