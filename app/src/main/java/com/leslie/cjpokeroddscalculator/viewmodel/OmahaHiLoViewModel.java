package com.leslie.cjpokeroddscalculator.viewmodel;

import com.leslie.cjpokeroddscalculator.calculation.OmahaExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.OmahaMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaHiLoPoker;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaLiveUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;

import java.util.List;

public class OmahaHiLoViewModel extends OmahaHighViewModel {
    public OmahaHiLoViewModel() {
        double[] initialSinglePlayerStats = new double[]{
            0.5,
            0.4929,
            0.2398,
            0.0142,
            0.0168,
            0.0298,
            0.2645,
            0.3686,
            0.0879,
            0.1128,
            0.0673,
            0.0635,
            0.0048,
            0.0009,
            0.3481
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
                calcObj.setOmahaPokerObj(new OmahaHiLoPoker(omahaOutputResult));
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
                calcObj.setOmahaPokerObj(new OmahaHiLoPoker(omahaOutputResult));
                calcObj.calculate(cardRows);
            } catch (InterruptedException ignored) { }
        });
    }
}
