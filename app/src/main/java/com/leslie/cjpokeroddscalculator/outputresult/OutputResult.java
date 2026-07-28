package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public abstract class OutputResult {
    public EquityCalculatorViewModel equityCalculatorViewModel;

    public OutputResult(EquityCalculatorViewModel equityCalculatorViewModel) {
        this.equityCalculatorViewModel = equityCalculatorViewModel;
    }

    public void updateResDesc(int stringId) {
        equityCalculatorViewModel.resDesc.postValue(stringId);
    }

    public void updateWinResults(double[][] results) {
        double[][] transposedResults;

        if (results.length == 0) {
            transposedResults = null;
        } else {
            int rows = results.length;
            int cols = results[0].length;

            transposedResults = new double[cols][rows];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    transposedResults[j][i] = results[i][j];
                }
            }
        }

        equityCalculatorViewModel.stats.postValue(transposedResults);
    }
}
