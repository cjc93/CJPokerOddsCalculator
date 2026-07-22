package com.leslie.cjpokeroddscalculator.viewmodel;

import com.leslie.cjpokeroddscalculator.calculation.TexasHoldemExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.TexasHoldemMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemLiveUpdate;

import java.util.List;

public class TexasHoldemViewModel extends EquityCalculatorViewModel {
    public TexasHoldemViewModel() {
        double[] initialSinglePlayerStats = new double[]{
            0.5,
            0.4797,
            0.0407,
            0.1741,
            0.4382,
            0.235,
            0.0483,
            0.0462,
            0.0303,
            0.026,
            0.0017,
            0.0003
        };

        double[][] initialStats = new double[2][];

        for (int playerIdx = 0; playerIdx < 2; playerIdx++) {
            initialStats[playerIdx] = initialSinglePlayerStats;
        }

        stats.postValue(initialStats);
    }

    @Override
    public Thread createMonteCarloThread(List<CardRow> cardRows, int cardsPerHand) {
        return new Thread(() -> {
            try {
                TexasHoldemMonteCarloCalc calcObj = new TexasHoldemMonteCarloCalc();
                calcObj.calculate(cardRows, new TexasHoldemLiveUpdate(this));
            } catch (InterruptedException ignored) { }
        });
    }

    @Override
    public Thread createExactCalcThread(List<CardRow> cardRows, int cardsPerHand) {
        return new Thread(() -> {
            try {
                TexasHoldemExactCalc calcObj = new TexasHoldemExactCalc();
                calcObj.calculate(cardRows, new TexasHoldemFinalUpdate(this));
            } catch (InterruptedException ignored) { }
        });
    }
}
