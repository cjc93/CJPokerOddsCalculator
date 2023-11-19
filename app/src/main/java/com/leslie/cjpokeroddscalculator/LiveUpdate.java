package com.leslie.cjpokeroddscalculator;

public class LiveUpdate extends OutputResult {

    public LiveUpdate(TexasHoldemFragment texasHoldemFragment) {
        super(texasHoldemFragment);
    }

    public boolean during_simulations(double[]... result) {
        if (this.currentThread.isInterrupted()) {
            return false;
        }

        texasHoldemFragment.requireActivity().runOnUiThread(() -> update_win_results(result[0], result[1]));
        return true;
    }

    public void after_all_simulations(double[] equity, double[] win, boolean... isCancelled) {
        if (!Thread.interrupted()) {
            if (texasHoldemFragment.exact_calc_thread.isAlive()) {
                texasHoldemFragment.requireActivity().runOnUiThread(() -> update_win_results(equity, win));
            } else {
                texasHoldemFragment.requireActivity().runOnUiThread(() -> {
                    update_win_results(equity, win);
                    texasHoldemFragment.binding.resDesc.setText(R.string.finished_checking_random_subset);
                });
            }
        }
    }
}
