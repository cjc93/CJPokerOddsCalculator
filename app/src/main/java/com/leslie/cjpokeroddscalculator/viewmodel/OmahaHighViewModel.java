package com.leslie.cjpokeroddscalculator.viewmodel;

import com.leslie.cjpokeroddscalculator.calculation.OmahaExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.OmahaMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaLiveUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;

import java.util.List;

public class OmahaHighViewModel extends EquityCalculatorViewModel {
    public OmahaHighViewModel() {
        double[] initialSinglePlayerStats = new double[]{
            0.5,
            0.4929,
            0.0142,
            0.0299,
            0.2647,
            0.3683,
            0.0879,
            0.1127,
            0.0672,
            0.0635,
            0.0048,
            0.0009
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
                OmahaMonteCarloCalc calcObj = new OmahaMonteCarloCalc(cardsPerHand);
                OmahaOutputResult omahaOutputResult = new OmahaLiveUpdate(this, calcObj);
                calcObj.setOmahaPokerObj(new OmahaPoker(omahaOutputResult));
                calcObj.calculate(cardRows);
            } catch (InterruptedException ignored) { }
        });
    }

    @Override
    public Thread createExactCalcThread(List<CardRow> cardRows, int cardsPerHand) {
        return new Thread(() -> {
            try {
                OmahaExactCalc calcObj = new OmahaExactCalc(cardsPerHand);
                OmahaOutputResult omahaOutputResult = new OmahaFinalUpdate(this, calcObj);
                calcObj.setOmahaPokerObj(new OmahaPoker(omahaOutputResult));
                calcObj.calculate(cardRows);
            } catch (InterruptedException ignored) { }
        });
    }
}
