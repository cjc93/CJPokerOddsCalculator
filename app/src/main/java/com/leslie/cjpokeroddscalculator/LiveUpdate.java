package com.leslie.cjpokeroddscalculator;

import com.leslie.cjpokeroddscalculator.calculation.Calculation;

public class LiveUpdate extends OutputResult {
    private long last_update_time;

    public LiveUpdate(MainActivity mainActivity, Calculation calcObj) {
        super(mainActivity, calcObj);
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
            update_win_perc();

            last_update_time = System.currentTimeMillis();
        }
    }

    public void after_all_simulation() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        update_win_perc();
    }
}
