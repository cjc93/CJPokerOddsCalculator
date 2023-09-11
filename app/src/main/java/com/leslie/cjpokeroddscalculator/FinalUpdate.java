package com.leslie.cjpokeroddscalculator;

public class FinalUpdate extends OutputResult {
    private long start_time;
    private boolean is_starting_period;

    public FinalUpdate(MainActivity mainActivity, Calculation calcObj) {
        super(mainActivity, calcObj);
    }

    public void before_all_simulation() {
        this.start_time = System.currentTimeMillis();
        this.is_starting_period = true;
    }

    public void after_every_simulation() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        if (this.is_starting_period) {
            long current_time = System.currentTimeMillis();
            if (current_time - start_time > 300) {
                is_starting_period = false;

                if ((double) (current_time - start_time) / (double) calcObj.simulation_count > 4000000 / (double) calcObj.total_simulations) {
                    throw new InterruptedException();
                }
            }
        }
    }

    public void after_all_simulation() {
        if (mainActivity.monte_carlo_thread != null) {
            mainActivity.monte_carlo_thread.interrupt();
        }

        update_win_perc();
    }
}
