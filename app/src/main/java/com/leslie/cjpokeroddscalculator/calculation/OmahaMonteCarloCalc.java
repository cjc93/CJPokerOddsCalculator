package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.calculation.pet.Cards;
import com.leslie.cjpokeroddscalculator.calculation.pet.CardsMonteCarlo;

public class OmahaMonteCarloCalc extends OmahaCalc {

    public Cards createCards(String[] deck, String[] boardCards, String[][] playerCards) {
        Cards cards = new CardsMonteCarlo(deck, boardCards, playerCards, 2000000000);
        this.totalSimulations = cards.count();

        return cards;
    }
}
