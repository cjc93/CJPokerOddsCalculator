package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.calculation.OmahaCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;
import com.leslie.cjpokeroddscalculator.calculation.pet.EquityUtil;

public class OmahaLiveUpdate extends OmahaOutputResult {
    private long lastUpdateTime;

    public OmahaLiveUpdate(EquityCalculatorFragment equityCalculatorFragment, OmahaCalc omahaCalc) {
        super(equityCalculatorFragment, omahaCalc);
    }

    @Override
    public void beforeAllSimulations() {
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void duringSimulations(Equity[] eqs, int count) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        long current_time = System.currentTimeMillis();
        if (current_time - lastUpdateTime > 300) {
            EquityUtil.summariseEquities(eqs, count);

            equityCalculatorFragment.requireActivity().runOnUiThread(() -> updateWinResults(eqs));

            lastUpdateTime = System.currentTimeMillis();
        }
    }

    public void afterAllSimulations(Equity[] eqs) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        if (equityCalculatorFragment.exact_calc_thread.isAlive()) {
            equityCalculatorFragment.requireActivity().runOnUiThread(() -> updateWinResults(eqs));
        } else {
            equityCalculatorFragment.requireActivity().runOnUiThread(() -> {
                updateWinResults(eqs);
                updateResDesc(R.string.finished_checking_random_subset);
            });
        }
    }
}
