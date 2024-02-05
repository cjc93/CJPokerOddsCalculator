package com.leslie.cjpokeroddscalculator.outputresult;

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
}
