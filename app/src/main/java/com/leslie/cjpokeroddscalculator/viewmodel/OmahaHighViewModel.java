package com.leslie.cjpokeroddscalculator.viewmodel;

import com.leslie.cjpokeroddscalculator.calculation.OmahaExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.OmahaMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaLiveUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OmahaHighViewModel extends EquityCalculatorViewModel {
    public OmahaHighViewModel() {
        cardsPerHand = 4;

        List<CardRow> cardRowList = new ArrayList<>();
        cardRowList.add(new SpecificCardsRow(null, null, 5));

        List<Double> initialStats = Arrays.asList(
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
        );

        cardRowList.add(new SpecificCardsRow(new ArrayList<>(initialStats), false, cardsPerHand));
        cardRowList.add(new SpecificCardsRow(new ArrayList<>(initialStats), false, cardsPerHand));

        cardRows.setValue(cardRowList);
    }

    @Override
    public Thread createMonteCarloThread() {
        return new Thread(() -> {
            try {
                OmahaMonteCarloCalc calcObj = new OmahaMonteCarloCalc(cardsPerHand);
                OmahaOutputResult omahaOutputResult = new OmahaLiveUpdate(this, calcObj);
                calcObj.setOmahaPokerObj(new OmahaPoker(omahaOutputResult));
                calcObj.calculate(cardRows.getValue());
            } catch (InterruptedException ignored) { }
        });
    }

    @Override
    public Thread createExactCalcThread() {
        return new Thread(() -> {
            try {
                OmahaExactCalc calcObj = new OmahaExactCalc(cardsPerHand);
                OmahaOutputResult omahaOutputResult = new OmahaFinalUpdate(this, calcObj);
                calcObj.setOmahaPokerObj(new OmahaPoker(omahaOutputResult));
                calcObj.calculate(cardRows.getValue());
            } catch (InterruptedException ignored) { }
        });
    }
}
