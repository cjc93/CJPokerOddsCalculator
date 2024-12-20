package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.calculation.pet.EquityUtil;
import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;
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

    @Override
    public void duringSimulations(Equity[] eqs, int count) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        if (this.isStartingPeriod) {
            long current_time = System.currentTimeMillis();
            if (current_time - startTime > 300) {
                isStartingPeriod = false;

                if ((double) (current_time - startTime) / (double) count > 4000000 / (double) omahaCalc.totalSimulations) {
                    if (!equityCalculatorFragment.monteCarloThread.isAlive()) {
                        equityCalculatorFragment.requireActivity().runOnUiThread(() -> updateResDesc(R.string.finished_checking_random_subset));
                    }

                    throw new InterruptedException();
                }
            }
        }
    }

    @Override
    public void afterAllSimulations(Equity[] eqs) throws InterruptedException {
        if (!Thread.interrupted()) {
            if (equityCalculatorFragment.monteCarloThread.isAlive()) {
                equityCalculatorFragment.monteCarloThread.interrupt();
            }

            equityCalculatorFragment.requireActivity().runOnUiThread(() -> {
                double [][] results = EquityUtil.convertEquitiesToMatrix(eqs);
                results = omahaCalc.averageUnknownStats(results);
                updateWinResults(results);
                updateResDesc(R.string.all_combinations_checked_result_is_exact);
            });
        }
    }
}
