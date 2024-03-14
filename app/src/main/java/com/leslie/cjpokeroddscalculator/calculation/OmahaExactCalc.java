package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.calculation.pet.Cards;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.calculation.pet.CardsExact;
import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;
import com.leslie.cjpokeroddscalculator.calculation.pet.Poker;

import java.util.List;

public class OmahaExactCalc extends OmahaCalc {

    public void calculate(List<CardRow> cardRows, OmahaOutputResult outputResultObj) throws InterruptedException {
        initialiseVariables(cardRows, outputResultObj);

        OmahaPoker poker = new OmahaPoker(outputResultObj);

        String[] boardCards = ((SpecificCardsRow) cardRows.get(0)).convertOmahaCardsToStr();

        String[][] playerCards = convertPlayerCardsToStr(cardRows);

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
