package com.leslie.cjpokeroddscalculator.viewmodel;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OmahaHigh5ViewModel extends OmahaHighViewModel {
    public OmahaHigh5ViewModel() {
        cardsPerHand = 5;

        List<CardRow> cardRowList = new ArrayList<>();
        cardRowList.add(new SpecificCardsRow(null, null, 5, null));

        List<Double> initialStats = Arrays.asList(
            0.5,
            0.4898,
            0.0204,
            0.0078,
            0.1668,
            0.376,
            0.0966,
            0.1525,
            0.0961,
            0.0956,
            0.0072,
            0.0014
        );

        cardRowList.add(new SpecificCardsRow(new ArrayList<>(initialStats), false, cardsPerHand, 0));
        cardRowList.add(new SpecificCardsRow(new ArrayList<>(initialStats), false, cardsPerHand, null));

        cardRows.setValue(cardRowList);
    }
}
