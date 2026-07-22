package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.calculation.OmahaCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;
import com.leslie.cjpokeroddscalculator.calculation.pet.EquityUtil;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;

public class OmahaLiveUpdate extends OmahaOutputResult {
    private long lastUpdateTime;

    public OmahaLiveUpdate(EquityCalculatorViewModel equityCalculatorViewModel, OmahaCalc omahaCalc) {
        super(equityCalculatorViewModel, omahaCalc);
    }

    @Override
    public void beforeAllSimulations() {
        this.lastUpdateTime = System.currentTimeMillis();
    }

    @Override
    public void duringSimulations(Equity[] eqs, int count) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        if (System.currentTimeMillis() - lastUpdateTime > 300) {
            EquityUtil.summariseEquities(eqs, count);

            if (equityCalculatorViewModel.exactCalcThread.isAlive()) {
                double [][] results = EquityUtil.convertEquitiesToMatrix(eqs);
                results = omahaCalc.averageUnknownStats(results);
                updateWinResults(results);
                updateResDesc(R.string.checking_combinations);
            } else {
                double [][] results = EquityUtil.convertEquitiesToMatrix(eqs);
                results = omahaCalc.averageUnknownStats(results);
                updateWinResults(results);
                updateResDesc(R.string.checking_random_subset);
            }

            lastUpdateTime = System.currentTimeMillis();
        }
    }

    @Override
    public void afterAllSimulations(Equity[] eqs) throws InterruptedException {
        if (!Thread.interrupted()) {
            if (equityCalculatorViewModel.exactCalcThread.isAlive()) {
                double [][] results = EquityUtil.convertEquitiesToMatrix(eqs);
                results = omahaCalc.averageUnknownStats(results);
                updateWinResults(results);
            } else {
                double [][] results = EquityUtil.convertEquitiesToMatrix(eqs);
                results = omahaCalc.averageUnknownStats(results);
                updateWinResults(results);
                updateResDesc(R.string.finished_checking_random_subset);
            }
        }
    }
}
