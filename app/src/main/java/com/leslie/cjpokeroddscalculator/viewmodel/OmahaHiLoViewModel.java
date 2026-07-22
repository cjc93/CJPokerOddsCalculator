package com.leslie.cjpokeroddscalculator.viewmodel;

import com.leslie.cjpokeroddscalculator.calculation.OmahaExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.OmahaMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaHiLoPoker;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;
import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaLiveUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;

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
    public Thread createMonteCarloThread(EquityCalculatorFragment fragment) {
        return new Thread(() -> {
            try {
                OmahaMonteCarloCalc calcObj = new OmahaMonteCarloCalc(fragment.cardsPerHand);
                OmahaOutputResult omahaOutputResult = new OmahaLiveUpdate(fragment, this, calcObj);
                calcObj.setOmahaPokerObj(new OmahaHiLoPoker(omahaOutputResult));
                calcObj.calculate(fragment.cardRows);
            } catch (InterruptedException ignored) { }
        });
    }

    @Override
    public Thread createExactCalcThread(EquityCalculatorFragment fragment) {
        return new Thread(() -> {
            try {
                OmahaExactCalc calcObj = new OmahaExactCalc(fragment.cardsPerHand);
                OmahaOutputResult omahaOutputResult = new OmahaFinalUpdate(fragment, this, calcObj);
                calcObj.setOmahaPokerObj(new OmahaHiLoPoker(omahaOutputResult));
                calcObj.calculate(fragment.cardRows);
            } catch (InterruptedException ignored) { }
        });
    }
}
