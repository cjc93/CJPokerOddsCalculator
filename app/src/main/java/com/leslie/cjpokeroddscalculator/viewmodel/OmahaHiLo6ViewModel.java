package com.leslie.cjpokeroddscalculator.viewmodel;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OmahaHiLo6ViewModel extends OmahaHiLoViewModel {
    public OmahaHiLo6ViewModel() {
        cardsPerHand = 6;

        List<CardRow> cardRowList = new ArrayList<>();
        cardRowList.add(new SpecificCardsRow(null, null, 5));

        List<Double> initialStats = Arrays.asList(
            0.5,
            0.4862,
            0.2725,
            0.0275,
            0.0372,
            0.0014,
            0.0979,
            0.3485,
            0.1027,
            0.1855,
            0.1225,
            0.1292,
            0.0101,
            0.0022,
            0.4889
        );

        cardRowList.add(new SpecificCardsRow(new ArrayList<>(initialStats), false, cardsPerHand));
        cardRowList.add(new SpecificCardsRow(new ArrayList<>(initialStats), false, cardsPerHand));

        cardRows.setValue(cardRowList);
    }
}