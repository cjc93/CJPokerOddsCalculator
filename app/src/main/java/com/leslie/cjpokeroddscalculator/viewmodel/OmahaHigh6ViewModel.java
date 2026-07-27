package com.leslie.cjpokeroddscalculator.viewmodel;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OmahaHigh6ViewModel extends OmahaHighViewModel {
    public OmahaHigh6ViewModel() {
        cardsPerHand = 6;

        List<CardRow> cardRowList = new ArrayList<>();
        cardRowList.add(new SpecificCardsRow(null, null, 5, null));

        List<Double> initialStats = Arrays.asList(
            0.5,
            0.4864,
            0.0273,
            0.0014,
            0.0978,
            0.3479,
            0.1032,
            0.1860,
            0.1224,
            0.1289,
            0.0101,
            0.0022
        );

        cardRowList.add(new SpecificCardsRow(new ArrayList<>(initialStats), false, cardsPerHand, 0));
        cardRowList.add(new SpecificCardsRow(new ArrayList<>(initialStats), false, cardsPerHand, null));

        cardRows.setValue(cardRowList);
    }
}
