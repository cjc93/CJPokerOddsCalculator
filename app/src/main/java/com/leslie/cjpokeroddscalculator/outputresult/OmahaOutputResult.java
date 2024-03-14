package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.calculation.OmahaCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;

public abstract class OmahaOutputResult extends OutputResult {
    OmahaCalc omahaCalc;

    public OmahaOutputResult(EquityCalculatorFragment equityCalculatorFragment, OmahaCalc omahaCalc) {
        super(equityCalculatorFragment);
        this.omahaCalc = omahaCalc;
    }

    public abstract void beforeAllSimulations();

    public abstract void duringSimulations(Equity[] eqs, int count) throws InterruptedException;

    public abstract void afterAllSimulations(Equity[] eqs) throws InterruptedException;

    public void updateWinResults(Equity[] eqs) {
        try {
            if (equityCalculatorFragment.getActivity() != null) {

                double[][] result = omahaCalc.averageUnknownStats(eqs);

                for (int i = 0; i < eqs.length; i++) {
                    equityCalculatorFragment.equityList.get(i).setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, result[0][i] * 100));
                    equityCalculatorFragment.winList.get(i).setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, result[1][i] * 100));
                    equityCalculatorFragment.tieList.get(i).setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, result[2][i] * 100));
                }
            }
        } catch (IndexOutOfBoundsException ignored) { }
    }
}
