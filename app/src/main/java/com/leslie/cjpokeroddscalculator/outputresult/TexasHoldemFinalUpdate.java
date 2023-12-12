package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.R;

public class TexasHoldemFinalUpdate extends TexasHoldemOutputResult {

    public TexasHoldemFinalUpdate(EquityCalculatorFragment equityCalculatorFragment) {
        super(equityCalculatorFragment);
    }

    public boolean duringSimulations(double[]... result) {
        if (this.currentThread.isInterrupted()) {
            return false;
        }

        equityCalculatorFragment.requireActivity().runOnUiThread(() -> updateResDesc(R.string.checking_combinations));

        return true;
    }

    public void afterAllSimulations(double[] equity, double[] win, boolean... isCancelled) {
        if (!Thread.interrupted()) {
            if (equityCalculatorFragment.monte_carlo_thread.isAlive()) {
                if (isCancelled[0]) {
                    equityCalculatorFragment.requireActivity().runOnUiThread(() -> updateResDesc(R.string.checking_random_subset));
                } else {
                    equityCalculatorFragment.monte_carlo_thread.interrupt();
                    equityCalculatorFragment.requireActivity().runOnUiThread(() -> {
                        updateWinResults(equity, win);
                        updateResDesc(R.string.all_combinations_checked_result_is_exact);
                    });
                }
            } else {
                if (isCancelled[0]) {
                    equityCalculatorFragment.requireActivity().runOnUiThread(() -> updateResDesc(R.string.finished_checking_random_subset));
                } else {
                    equityCalculatorFragment.requireActivity().runOnUiThread(() -> {
                        updateWinResults(equity, win);
                        updateResDesc(R.string.all_combinations_checked_result_is_exact);
                    });
                }
            }
        }
    }

}