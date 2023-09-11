package com.leslie.cjpokeroddscalculator;

import android.graphics.Color;

public class OutputResult {
    public Calculation calcObj;
    public MainActivity mainActivity;
    public OutputResult (MainActivity mainActivity, Calculation calcObj) {
        this.mainActivity = mainActivity;
        this.calcObj = calcObj;
    }

    public void before_all_simulation() { }
    public void after_every_simulation() throws InterruptedException { }

    public void after_all_simulation() throws InterruptedException { }

    public void update_win_perc() {
        double[] equity_perc = calcObj.calc_equity_perc();

        mainActivity.handler.post(() -> {
            for(int i = 0; i < mainActivity.players_remaining_no; i++) {
                mainActivity.win_array[i].setText(mainActivity.getString(R.string.win_perc_populated, equity_perc[i] * 100));

                if(equity_perc[i] > 1 / (double) mainActivity.players_remaining_no + 0.02) {
                    mainActivity.win_array[i].setTextColor(Color.GREEN);
                } else if (equity_perc[i] < 1 / (double) mainActivity.players_remaining_no - 0.02) {
                    mainActivity.win_array[i].setTextColor(Color.RED);
                } else {
                    mainActivity.win_array[i].setTextColor(Color.WHITE);
                }
            }
        });
    }
}
