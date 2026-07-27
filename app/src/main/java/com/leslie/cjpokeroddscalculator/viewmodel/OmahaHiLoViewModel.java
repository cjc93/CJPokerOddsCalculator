package com.leslie.cjpokeroddscalculator.viewmodel;

import com.leslie.cjpokeroddscalculator.calculation.OmahaExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.OmahaMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaHiLoPoker;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaLiveUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OmahaHiLoViewModel extends OmahaHighViewModel {
    public OmahaHiLoViewModel() {
        cardsPerHand = 4;

        List<CardRow> cardRowList = new ArrayList<>();
        cardRowList.add(new SpecificCardsRow(null, null, 5, null));

        List<Double> initialStats = Arrays.asList(
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
        );

        cardRowList.add(new SpecificCardsRow(new ArrayList<>(initialStats), false, cardsPerHand, 0));
        cardRowList.add(new SpecificCardsRow(new ArrayList<>(initialStats), false, cardsPerHand, null));

        cardRows.setValue(cardRowList);
    }

    @Override
    public Thread createMonteCarloThread() {
        return new Thread(() -> {
            try {
                OmahaMonteCarloCalc calcObj = new OmahaMonteCarloCalc(cardsPerHand);
                OmahaOutputResult omahaOutputResult = new OmahaLiveUpdate(this, calcObj);
                calcObj.setOmahaPokerObj(new OmahaHiLoPoker(omahaOutputResult));
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
                calcObj.setOmahaPokerObj(new OmahaHiLoPoker(omahaOutputResult));
                calcObj.calculate(cardRows.getValue());
            } catch (InterruptedException ignored) { }
        });
    }
}
