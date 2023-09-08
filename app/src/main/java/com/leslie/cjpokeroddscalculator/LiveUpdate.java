package com.leslie.cjpokeroddscalculator;

import android.graphics.Color;

public class LiveUpdate {
    private final Calculation calc_obj;
    private long last_update_time;
    private final MainActivity main_activity;

    public LiveUpdate(MainActivity main_activity, Calculation calc_obj) {
        this.main_activity = main_activity;
        this.calc_obj = calc_obj;
    }

    public void before_all_simulation() {
        this.last_update_time = System.currentTimeMillis();
    }

    public void after_every_simulation() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        long current_time = System.currentTimeMillis();
        if (current_time - last_update_time > 300) {

            double[] equity_perc = calc_obj.calc_equity_perc();

            main_activity.handler.post(() -> {
                for(int i = 0; i < main_activity.players_remaining_no; i++) {
                    main_activity.win_array[i].setText(main_activity.getString(R.string.win_perc_populated, equity_perc[i] * 100));

                    if(equity_perc[i] > 1 / (double) main_activity.players_remaining_no + 0.02) {
                        main_activity.win_array[i].setTextColor(Color.GREEN);
                    } else if (equity_perc[i] < 1 / (double) main_activity.players_remaining_no - 0.02) {
                        main_activity.win_array[i].setTextColor(Color.RED);
                    } else {
                        main_activity.win_array[i].setTextColor(Color.WHITE);
                    }
                }
            });

            last_update_time = System.currentTimeMillis();
        }
    }
}
