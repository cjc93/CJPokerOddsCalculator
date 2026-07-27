package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.calculation.pet.Cards;
import com.leslie.cjpokeroddscalculator.calculation.pet.CardsExact;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;

public class OmahaExactCalc extends OmahaCalc {

    public OmahaExactCalc(OmahaPoker omahaPoker, int cardsPerHand) {
        super(omahaPoker, cardsPerHand);
    }

    public Cards createCards(String[] deck, String[] boardCards, String[][] playerCards) {
        return new CardsExact(deck, boardCards, playerCards, this.cardsPerHand);
    }
}
