package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;


public abstract class OutputResult {
    public EquityCalculatorFragment equityCalculatorFragment;

    public OutputResult(EquityCalculatorFragment equityCalculatorFragment) {
        this.equityCalculatorFragment = equityCalculatorFragment;
    }

    public void updateResDesc(int stringId) {
        if (equityCalculatorFragment.equityCalculatorBinding != null) {
            equityCalculatorFragment.equityCalculatorBinding.resDesc.setText(stringId);
        }
    }

    public void updateWinResults(double[][] results) {
        try {
            if (equityCalculatorFragment.getActivity() != null) {
                for (int playerIdx = 0; playerIdx < equityCalculatorFragment.statsMatrix.size(); playerIdx++) {
                    for (int statsIdx = 0; statsIdx < equityCalculatorFragment.statsMatrix.get(playerIdx).size(); statsIdx++) {
                        equityCalculatorFragment.statsMatrix.get(playerIdx).get(statsIdx).setText(
                            equityCalculatorFragment.getString(R.string.two_decimal_perc, results[statsIdx][playerIdx] * 100)
                        );
                    }
                }
            }
        } catch (IndexOutOfBoundsException ignored) { }
    }
}
