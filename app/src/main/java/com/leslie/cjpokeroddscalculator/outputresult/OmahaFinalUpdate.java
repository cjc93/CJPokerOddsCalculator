package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.calculation.OmahaCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;

public class OmahaFinalUpdate extends OmahaOutputResult {
    private long startTime;
    private boolean isStartingPeriod;

    public OmahaFinalUpdate(EquityCalculatorFragment equityCalculatorFragment, OmahaCalc omahaCalc) {
        super(equityCalculatorFragment, omahaCalc);
    }

    @Override
    public void beforeAllSimulations() {
        this.startTime = System.currentTimeMillis();
        this.isStartingPeriod = true;
    }

    public void duringSimulations(Equity[] eqs, int count) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        if (this.isStartingPeriod) {
            long current_time = System.currentTimeMillis();
            if (current_time - startTime > 300) {
                isStartingPeriod = false;

                if ((double) (current_time - startTime) / (double) count > 4000000 / (double) omahaCalc.totalSimulations) {
                    if (!equityCalculatorFragment.monte_carlo_thread.isAlive()) {
                        equityCalculatorFragment.requireActivity().runOnUiThread(() -> updateResDesc(R.string.finished_checking_random_subset));
                    }

                    throw new InterruptedException();
                }
            }
        }
    }

    public void afterAllSimulations(Equity[] eqs) throws InterruptedException {
        if (!Thread.interrupted()) {
            if (equityCalculatorFragment.monte_carlo_thread.isAlive()) {
                equityCalculatorFragment.monte_carlo_thread.interrupt();
            }

            equityCalculatorFragment.requireActivity().runOnUiThread(() -> {
                updateWinResults(eqs);
                updateResDesc(R.string.all_combinations_checked_result_is_exact);
            });
        }
    }
}
