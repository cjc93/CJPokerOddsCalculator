package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;

import java.util.ArrayList;
import java.util.List;


public abstract class OutputResult {
    public EquityCalculatorViewModel equityCalculatorViewModel;

    public OutputResult(EquityCalculatorViewModel equityCalculatorViewModel) {
        this.equityCalculatorViewModel = equityCalculatorViewModel;
    }

    public void updateResDesc(int stringId) {
        equityCalculatorViewModel.resDesc.postValue(stringId);
    }

    public void updateWinResults(double[][] results) {
        List<CardRow> newCardRows = equityCalculatorViewModel.getCardRowsCopy();

        try {
            for (int playerIdx = 1; playerIdx < newCardRows.size(); playerIdx++) {
                CardRow cardRow = newCardRows.get(playerIdx);
                List<Double> stats = new ArrayList<>();
                for (double[] result : results) {
                    stats.add(result[playerIdx - 1]);
                }
                cardRow.stats = stats;
            }

            equityCalculatorViewModel.cardRows.postValue(newCardRows);
        } catch (IndexOutOfBoundsException ignored) { }
    }
}
