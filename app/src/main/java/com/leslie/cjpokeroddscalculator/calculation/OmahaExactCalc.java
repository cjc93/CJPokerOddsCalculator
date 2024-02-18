package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.calculation.pet.Cards;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.calculation.pet.CardsExact;
import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;
import com.leslie.cjpokeroddscalculator.calculation.pet.Poker;

public class OmahaExactCalc extends OmahaCalc {

    public void calculate(CardRow[] cardRows, int playersRemainingNo, OmahaOutputResult outputResultObj) throws InterruptedException {
        initialiseVariables(cardRows, playersRemainingNo, outputResultObj);

        OmahaPoker poker = new OmahaPoker(outputResultObj);

        String[] boardCards = ((SpecificCardsRow) cardRows[0]).convertOmahaCardsToStr();

        String[][] playerCards = convertPlayerCardsToStr(cardRows, playersRemainingNo);

        String[] deck = Poker.remdeck(playerCards, boardCards);

        Cards cards = new CardsExact(deck, boardCards, playerCards);

        try {
            this.totalSimulations = cards.count();
        } catch (ArithmeticException e) {
            throw new InterruptedException();
        }

        outputResultObj.beforeAllSimulations();

        Equity[] eqs = poker.equityImpl(cards);

        outputResultObj.afterAllSimulations(eqs);
    }
}
