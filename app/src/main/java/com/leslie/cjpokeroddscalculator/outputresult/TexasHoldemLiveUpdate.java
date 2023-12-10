package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.R;

public class TexasHoldemLiveUpdate extends TexasHoldemOutputResult {

    public TexasHoldemLiveUpdate(EquityCalculatorFragment equityCalculatorFragment) {
        super(equityCalculatorFragment);
    }

    public boolean during_simulations(double[]... result) {
        if (this.currentThread.isInterrupted()) {
            return false;
        }

        equityCalculatorFragment.requireActivity().runOnUiThread(() -> updateWinResults(result[0], result[1]));
        return true;
    }

    public void after_all_simulations(double[] equity, double[] win, boolean... isCancelled) {
        if (!Thread.interrupted()) {
            if (equityCalculatorFragment.exact_calc_thread.isAlive()) {
                equityCalculatorFragment.requireActivity().runOnUiThread(() -> updateWinResults(equity, win));
            } else {
                equityCalculatorFragment.requireActivity().runOnUiThread(() -> {
                    updateWinResults(equity, win);
                    updateResDesc(R.string.finished_checking_random_subset);
                });
            }
        }
    }
}
