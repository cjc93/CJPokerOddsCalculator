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

                    equityCalculatorFragment.handStats.get(i).get(0).setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, result[3][i] * 100));
                    equityCalculatorFragment.handStats.get(i).get(1).setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, result[4][i] * 100));
                    equityCalculatorFragment.handStats.get(i).get(2).setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, result[5][i] * 100));
                    equityCalculatorFragment.handStats.get(i).get(3).setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, result[6][i] * 100));
                    equityCalculatorFragment.handStats.get(i).get(4).setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, result[7][i] * 100));
                    equityCalculatorFragment.handStats.get(i).get(5).setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, result[8][i] * 100));
                    equityCalculatorFragment.handStats.get(i).get(6).setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, result[9][i] * 100));
                    equityCalculatorFragment.handStats.get(i).get(7).setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, result[10][i] * 100));
                    equityCalculatorFragment.handStats.get(i).get(8).setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, result[11][i] * 100));
                }
            }
        } catch (IndexOutOfBoundsException ignored) { }
    }
}
