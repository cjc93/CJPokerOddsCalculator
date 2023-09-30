package com.leslie.cjpokeroddscalculator;

public class FinalUpdate extends OutputResult {

    public FinalUpdate(MainActivity mainActivity) {
        super(mainActivity);
    }

    public boolean during_simulations(double[]... result) {
        if (this.currentThread.isInterrupted()) {
            return false;
        }

        mainActivity.runOnUiThread(() -> {
            mainActivity.binding.resDesc.setText(R.string.checking_combinations);
        });

        return true;
    }

    public void after_all_simulations(double[] result, boolean... isCancelled) {
        if (!Thread.interrupted()) {
            if (mainActivity.monte_carlo_thread.isAlive()) {
                if (isCancelled[0]) {
                    mainActivity.runOnUiThread(() -> {
                        mainActivity.binding.resDesc.setText(R.string.checking_random_subset);
                    });
                } else {
                    mainActivity.monte_carlo_thread.interrupt();
                    mainActivity.runOnUiThread(() -> {
                        update_win_results(result);
                        mainActivity.binding.resDesc.setText(R.string.all_combinations_checked_result_is_exact);
                    });
                }
            } else {
                if (isCancelled[0]) {
                    mainActivity.runOnUiThread(() -> {
                        mainActivity.binding.resDesc.setText(R.string.finished_checking_random_subset);
                    });
                } else {
                    mainActivity.runOnUiThread(() -> {
                        update_win_results(result);
                        mainActivity.binding.resDesc.setText(R.string.all_combinations_checked_result_is_exact);
                    });
                }
            }
        }
    }
}
