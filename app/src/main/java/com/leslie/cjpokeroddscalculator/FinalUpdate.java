package com.leslie.cjpokeroddscalculator;

public class FinalUpdate extends OutputResult {

    public FinalUpdate(MainActivity mainActivity) {
        super(mainActivity);
    }

    public boolean during_simulations(double[]... result) {
        return !this.currentThread.isInterrupted();
    }

    public void after_all_simulations(double[] result) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        if (mainActivity.monte_carlo_thread != null) {
            mainActivity.monte_carlo_thread.interrupt();
        }

        mainActivity.runOnUiThread(() -> {
            update_win_results(result);
            mainActivity.binding.resDesc.setText(R.string.all_combinations_checked_result_is_exact);
        });
    }
}
