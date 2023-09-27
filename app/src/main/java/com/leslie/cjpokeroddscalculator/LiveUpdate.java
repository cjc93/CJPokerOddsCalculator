package com.leslie.cjpokeroddscalculator;

public class LiveUpdate extends OutputResult {

    public LiveUpdate(MainActivity mainActivity) {
        super(mainActivity);
    }

    public boolean during_simulations(double[]... result) {
        if (this.currentThread.isInterrupted()) {
            return false;
        }

        mainActivity.runOnUiThread(() -> update_win_results(result[0]));
        return true;
    }

    public void after_all_simulations(double[] result) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        mainActivity.runOnUiThread(() -> update_win_results(result));
    }
}
