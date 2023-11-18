package com.leslie.cjpokeroddscalculator;

public class LiveUpdate extends OutputResult {

    public LiveUpdate(MainActivity mainActivity) {
        super(mainActivity);
    }

    public boolean during_simulations(double[]... result) {
        if (this.currentThread.isInterrupted()) {
            return false;
        }

        mainActivity.runOnUiThread(() -> update_win_results(result[0], result[1]));
        return true;
    }

    public void after_all_simulations(double[] equity, double[] win, boolean... isCancelled) {
        if (!Thread.interrupted()) {
            if (mainActivity.exact_calc_thread.isAlive()) {
                mainActivity.runOnUiThread(() -> update_win_results(equity, win));
            } else {
                mainActivity.runOnUiThread(() -> {
                    update_win_results(equity, win);
                    mainActivity.binding.resDesc.setText(R.string.finished_checking_random_subset);
                });
            }
        }
    }
}
