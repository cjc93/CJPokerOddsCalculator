package com.leslie.cjpokeroddscalculator;

public class LiveUpdate extends OutputResult {

    public LiveUpdate(TexasHoldemFragment texasHoldemFragment) {
        super(texasHoldemFragment);
    }

    public boolean during_simulations(double[]... result) {
        if (this.currentThread.isInterrupted()) {
            return false;
        }

        texasHoldemFragment.requireActivity().runOnUiThread(() -> updateWinResults(result[0], result[1]));
        return true;
    }

    public void after_all_simulations(double[] equity, double[] win, boolean... isCancelled) {
        if (!Thread.interrupted()) {
            if (texasHoldemFragment.exact_calc_thread.isAlive()) {
                texasHoldemFragment.requireActivity().runOnUiThread(() -> updateWinResults(equity, win));
            } else {
                texasHoldemFragment.requireActivity().runOnUiThread(() -> {
                    updateWinResults(equity, win);
                    updateResDesc(R.string.finished_checking_random_subset);
                });
            }
        }
    }
}
