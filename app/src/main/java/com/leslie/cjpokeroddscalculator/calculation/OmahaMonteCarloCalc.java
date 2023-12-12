package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.CardRow;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;
import com.leslie.cjpokeroddscalculator.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.calculation.pet.CardsMonteCarlo;
import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;
import com.leslie.cjpokeroddscalculator.calculation.pet.Poker;

public class OmahaMonteCarloCalc extends OmahaCalc {

    public void calculate(CardRow[] cardRows, int playersRemainingNo, OmahaOutputResult outputResultObj) throws InterruptedException {
        initialiseVariables(cardRows, playersRemainingNo, outputResultObj);

        OmahaPoker poker = new OmahaPoker(outputResultObj);

        String[] boardCards = ((SpecificCardsRow) cardRows[0]).convertOmahaCardsToStr();

        String[][] playerCards = convertPlayerCardsToStr(cardRows, playersRemainingNo);

        String[] deck = Poker.remdeck(playerCards, boardCards);

        outputResultObj.beforeAllSimulations();

        Equity[] eqs = poker.equityImpl(new CardsMonteCarlo(deck, boardCards, playerCards, 2000000000));

        outputResultObj.afterAllSimulations(eqs);
    }
}
