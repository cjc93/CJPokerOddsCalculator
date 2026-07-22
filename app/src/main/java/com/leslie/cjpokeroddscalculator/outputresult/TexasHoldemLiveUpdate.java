package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;

public class TexasHoldemLiveUpdate extends TexasHoldemOutputResult {

    public TexasHoldemLiveUpdate(EquityCalculatorFragment equityCalculatorFragment, EquityCalculatorViewModel equityCalculatorViewModel) {
        super(equityCalculatorFragment, equityCalculatorViewModel);
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
            if (equityCalculatorViewModel.exactCalcThread.isAlive()) {
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
