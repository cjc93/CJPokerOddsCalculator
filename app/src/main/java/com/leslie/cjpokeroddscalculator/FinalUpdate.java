package com.leslie.cjpokeroddscalculator;

public class FinalUpdate extends OutputResult {

    public FinalUpdate(TexasHoldemFragment texasHoldemFragment) {
        super(texasHoldemFragment);
    }

    public boolean during_simulations(double[]... result) {
        if (this.currentThread.isInterrupted()) {
            return false;
        }

        texasHoldemFragment.requireActivity().runOnUiThread(() -> updateResDesc(R.string.checking_combinations));

        return true;
    }

    public void after_all_simulations(double[] equity, double[] win, boolean... isCancelled) {
        if (!Thread.interrupted()) {
            if (texasHoldemFragment.monte_carlo_thread.isAlive()) {
                if (isCancelled[0]) {
                    texasHoldemFragment.requireActivity().runOnUiThread(() -> updateResDesc(R.string.checking_random_subset));
                } else {
                    texasHoldemFragment.monte_carlo_thread.interrupt();
                    texasHoldemFragment.requireActivity().runOnUiThread(() -> {
                        updateWinResults(equity, win);
                        updateResDesc(R.string.all_combinations_checked_result_is_exact);
                    });
                }
            } else {
                if (isCancelled[0]) {
                    texasHoldemFragment.requireActivity().runOnUiThread(() -> updateResDesc(R.string.finished_checking_random_subset));
                } else {
                    texasHoldemFragment.requireActivity().runOnUiThread(() -> {
                        updateWinResults(equity, win);
                        updateResDesc(R.string.all_combinations_checked_result_is_exact);
                    });
                }
            }
        }
    }

}
