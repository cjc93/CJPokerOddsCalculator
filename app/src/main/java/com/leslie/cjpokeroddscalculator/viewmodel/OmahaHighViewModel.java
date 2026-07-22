package com.leslie.cjpokeroddscalculator.viewmodel;

import com.leslie.cjpokeroddscalculator.calculation.OmahaExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.OmahaMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.calculation.TexasHoldemExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.TexasHoldemMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;
import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaLiveUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemLiveUpdate;

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
    public Thread createMonteCarloThread(EquityCalculatorFragment fragment) {
        return new Thread(() -> {
            try {
                OmahaMonteCarloCalc calcObj = new OmahaMonteCarloCalc(fragment.cardsPerHand);
                OmahaOutputResult omahaOutputResult = new OmahaLiveUpdate(fragment, this, calcObj);
                calcObj.setOmahaPokerObj(new OmahaPoker(omahaOutputResult));
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
                calcObj.setOmahaPokerObj(new OmahaPoker(omahaOutputResult));
                calcObj.calculate(fragment.cardRows);
            } catch (InterruptedException ignored) { }
        });
    }
}
