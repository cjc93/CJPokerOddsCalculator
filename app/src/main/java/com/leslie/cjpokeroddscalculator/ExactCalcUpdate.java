package com.leslie.cjpokeroddscalculator;

public class ExactCalcUpdate {
    private final Calculation calcObj;
    private long start_time;
    private boolean is_starting_period;

    public ExactCalcUpdate(Calculation calcObj) {
        this.calcObj = calcObj;
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
}
