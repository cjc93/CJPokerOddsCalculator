package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.calculation.pet.EquityUtil;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;

public class OmahaFinalUpdate extends OmahaOutputResult {
    private long startTime;
    private boolean isStartingPeriod;

    public OmahaFinalUpdate(EquityCalculatorViewModel equityCalculatorViewModel) {
        super(equityCalculatorViewModel);
    }

    @Override
    public void beforeAllSimulations() {
        this.startTime = System.currentTimeMillis();
        this.isStartingPeriod = true;
    }

    @Override
    public void duringSimulations(Equity[] eqs, int count) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        if (this.isStartingPeriod) {
            long current_time = System.currentTimeMillis();
            if (current_time - startTime > 300) {
                isStartingPeriod = false;

                if ((double) (current_time - startTime) / (double) count > 4000000 / (double) totalSimulations) {
                    if (!equityCalculatorViewModel.monteCarloThread.isAlive()) {
                        updateResDesc(R.string.finished_checking_random_subset);
                    }

                    throw new InterruptedException();
                }
            }
        }
    }

    @Override
    public void afterAllSimulations(Equity[] eqs) throws InterruptedException {
        if (!Thread.interrupted()) {
            if (equityCalculatorViewModel.monteCarloThread.isAlive()) {
                equityCalculatorViewModel.monteCarloThread.interrupt();
            }

            double [][] results = EquityUtil.convertEquitiesToMatrix(eqs);
            results = averageUnknownStats.apply(results);
            updateWinResults(results);
            updateResDesc(R.string.all_combinations_checked_result_is_exact);
        }
    }
}
