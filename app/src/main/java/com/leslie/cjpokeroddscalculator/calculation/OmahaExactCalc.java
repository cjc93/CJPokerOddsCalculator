package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.calculation.pet.Cards;
import com.leslie.cjpokeroddscalculator.calculation.pet.CardsExact;

public class OmahaExactCalc extends OmahaCalc {

    public OmahaExactCalc(int cardsPerHand) {
        super(cardsPerHand);
    }

    public Cards createCards(String[] deck, String[] boardCards, String[][] playerCards) {
        return new CardsExact(deck, boardCards, playerCards, this.cardsPerHand);
    }
}
