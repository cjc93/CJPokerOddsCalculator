package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.calculation.pet.Cards;
import com.leslie.cjpokeroddscalculator.calculation.pet.CardsMonteCarlo;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;

public class OmahaMonteCarloCalc extends OmahaCalc {

    public OmahaMonteCarloCalc(OmahaPoker omahaPoker, int cardsPerHand) {
        super(omahaPoker, cardsPerHand);
    }

    public Cards createCards(String[] deck, String[] boardCards, String[][] playerCards) {
        return new CardsMonteCarlo(deck, boardCards, playerCards, 2000000000, this.cardsPerHand);
    }
}
