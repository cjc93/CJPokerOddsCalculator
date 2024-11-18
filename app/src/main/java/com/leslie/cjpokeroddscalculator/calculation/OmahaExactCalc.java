package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.calculation.pet.Cards;
import com.leslie.cjpokeroddscalculator.calculation.pet.CardsExact;

public class OmahaExactCalc extends OmahaCalc {

    public Cards createCards(String[] deck, String[] boardCards, String[][] playerCards) throws InterruptedException {
        Cards cards = new CardsExact(deck, boardCards, playerCards);

        try {
            this.totalSimulations = cards.count();
        } catch (ArithmeticException e) {
            throw new InterruptedException();
        }

        return cards;
    }
}
