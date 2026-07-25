package com.leslie.cjpokeroddscalculator.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.leslie.cjpokeroddscalculator.calculation.TexasHoldemExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.TexasHoldemMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemLiveUpdate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TexasHoldemViewModel extends EquityCalculatorViewModel {
    public MutableLiveData<Integer> selectedRangePosition = new MutableLiveData<>();

    public TexasHoldemViewModel() {
        cardsPerHand = 2;

        List<CardRow> cardRowList = new ArrayList<>();
        cardRowList.add(new SpecificCardsRow(null, null, 5));

        List<Double> initialStats = Arrays.asList(
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
        );

        cardRowList.add(new SpecificCardsRow(new ArrayList<>(initialStats), false, cardsPerHand));
        cardRowList.add(new SpecificCardsRow(new ArrayList<>(initialStats), false, cardsPerHand));

        cardRows.setValue(cardRowList);
    }

    @Override
    public Thread createMonteCarloThread() {
        return new Thread(() -> {
            try {
                TexasHoldemMonteCarloCalc calcObj = new TexasHoldemMonteCarloCalc();
                calcObj.calculate(cardRows.getValue(), new TexasHoldemLiveUpdate(this));
            } catch (InterruptedException ignored) { }
        });
    }

    @Override
    public Thread createExactCalcThread() {
        return new Thread(() -> {
            try {
                TexasHoldemExactCalc calcObj = new TexasHoldemExactCalc();
                calcObj.calculate(cardRows.getValue(), new TexasHoldemFinalUpdate(this));
            } catch (InterruptedException ignored) { }
        });
    }
}
