package com.leslie.cjpokeroddscalculator.viewmodel;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OmahaHiLo5ViewModel extends OmahaHiLoViewModel {
    public OmahaHiLo5ViewModel() {
        cardsPerHand = 5;

        List<CardRow> cardRowList = new ArrayList<>();
        cardRowList.add(new SpecificCardsRow(null, 5, null));
        cardRowList.add(new SpecificCardsRow(false, cardsPerHand, 0));
        cardRowList.add(new SpecificCardsRow(false, cardsPerHand, null));
        cardRows.setValue(cardRowList);

        double[] initialStats = new double[]{
            0.5,
            0.4898,
            0.2644,
            0.0203,
            0.0263,
            0.0078,
            0.1668,
            0.3762,
            0.0965,
            0.1525,
            0.0961,
            0.0956,
            0.0072,
            0.0014,
            0.4302
        };

        stats.setValue(new double[][]{initialStats, initialStats});
    }
}