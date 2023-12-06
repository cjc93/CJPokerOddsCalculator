package com.leslie.cjpokeroddscalculator;

public class FinalUpdate extends OutputResult {

    public FinalUpdate(EquityCalculatorFragment equityCalculatorFragment) {
        super(equityCalculatorFragment);
    }

    public boolean during_simulations(double[]... result) {
        if (this.currentThread.isInterrupted()) {
            return false;
        }

        equityCalculatorFragment.requireActivity().runOnUiThread(() -> updateResDesc(R.string.checking_combinations));

        return true;
    }

    public void after_all_simulations(double[] equity, double[] win, boolean... isCancelled) {
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
