package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;


public abstract class OutputResult {
    public EquityCalculatorFragment equityCalculatorFragment;
    public EquityCalculatorViewModel equityCalculatorViewModel;

    public OutputResult(EquityCalculatorFragment equityCalculatorFragment, EquityCalculatorViewModel equityCalculatorViewModel) {
        this.equityCalculatorFragment = equityCalculatorFragment;
        this.equityCalculatorViewModel = equityCalculatorViewModel;
    }

    public void updateResDesc(int stringId) {
        if (equityCalculatorFragment.equityCalculatorBinding != null) {
            equityCalculatorFragment.viewModel.resDesc.postValue(stringId);
        }
    }

    public void updateWinResults(double[][] results) {
        int rows = results.length;
        int cols = results[0].length;

        double[][] transposedResults = new double[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposedResults[j][i] = results[i][j];
            }
        }

        equityCalculatorFragment.viewModel.stats.postValue(transposedResults);
    }
}
