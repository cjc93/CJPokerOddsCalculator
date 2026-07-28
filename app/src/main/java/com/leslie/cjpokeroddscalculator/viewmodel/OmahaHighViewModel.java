package com.leslie.cjpokeroddscalculator.viewmodel;

import com.leslie.cjpokeroddscalculator.calculation.OmahaExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.OmahaMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaLiveUpdate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OmahaHighViewModel extends EquityCalculatorViewModel {
    public OmahaHighViewModel() {
        cardsPerHand = 4;

        List<CardRow> cardRowList = new ArrayList<>();
        cardRowList.add(new SpecificCardsRow(null, 5, null));
        cardRowList.add(new SpecificCardsRow(false, cardsPerHand, 0));
        cardRowList.add(new SpecificCardsRow(false, cardsPerHand, null));
        cardRows.setValue(cardRowList);

        double[] initialStats = new double[]{
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

        stats.setValue(new double[][]{initialStats, initialStats});
    }

    @Override
    public Thread createMonteCarloThread() {
        return new Thread(() -> {
            try {
                OmahaMonteCarloCalc calcObj = new OmahaMonteCarloCalc(new OmahaPoker(), cardsPerHand);
                calcObj.calculate(cardRows.getValue(), new OmahaLiveUpdate(this));
            } catch (InterruptedException ignored) { }
        });
    }

    @Override
    public Thread createExactCalcThread() {
        return new Thread(() -> {
            try {
                OmahaExactCalc calcObj = new OmahaExactCalc(new OmahaPoker(), cardsPerHand);
                calcObj.calculate(cardRows.getValue(), new OmahaFinalUpdate(this));
            } catch (InterruptedException ignored) { }
        });
    }
}
