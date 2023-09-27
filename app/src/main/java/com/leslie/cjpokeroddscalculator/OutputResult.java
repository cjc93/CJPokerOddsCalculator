package com.leslie.cjpokeroddscalculator;

import android.graphics.Color;

public abstract class OutputResult {
    public Thread currentThread;
    public MainActivity mainActivity;

    public OutputResult (MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.currentThread = Thread.currentThread();
    }

    public abstract boolean during_simulations(double[]... result);

    public abstract void after_all_simulations(double[] result) throws InterruptedException;

    public void update_win_results(double[] result) {
        for(int i = 0; i < mainActivity.players_remaining_no; i++) {
            mainActivity.win_array[i].setText(mainActivity.getString(R.string.win_perc_populated, result[i] * 100));

            if(result[i] > 1 / (double) mainActivity.players_remaining_no + 0.02) {
                mainActivity.win_array[i].setTextColor(Color.GREEN);
            } else if (result[i] < 1 / (double) mainActivity.players_remaining_no - 0.02) {
                mainActivity.win_array[i].setTextColor(Color.RED);
            } else {
                mainActivity.win_array[i].setTextColor(Color.WHITE);
            }
        }
    }
}
