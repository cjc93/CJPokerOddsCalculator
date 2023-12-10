package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.CardRow;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemOutputResult;
import com.leslie.cjpokeroddscalculator.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.calculation.pet.CardsMonteCarlo;
import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;
import com.leslie.cjpokeroddscalculator.calculation.pet.Poker;

public class OmahaMonteCarloCalc extends OmahaCalc {

    public void calculate(CardRow[] cardRows, int playersRemainingNo, TexasHoldemOutputResult outputResultObj) throws InterruptedException {
        initialiseVariables(cardRows, playersRemainingNo, outputResultObj);

        OmahaPoker poker = new OmahaPoker();

        String[] boardCards = ((SpecificCardsRow) cardRows[0]).convertOmahaCardsToStr();

        String[][] playerCards = convertPlayerCardsToStr(cardRows, playersRemainingNo);

        String[] deck = Poker.remdeck(playerCards, boardCards);
        Equity[] eqs = poker.equityImpl(new CardsMonteCarlo(deck, boardCards, playerCards, 100000));
        afterAllSimulations(eqs);
    }

    public boolean during_simulations(Equity[] eqs) {
        double[][] result = averageUnknownStats(eqs);
        return outputResultObj.during_simulations(result);
    }

    public void afterAllSimulations(Equity[] eqs) {
        double[][] result = averageUnknownStats(eqs);
//        outputResultObj.after_all_simulations(result[0], result[1], result[2]);
        outputResultObj.after_all_simulations(result[0], result[1]);
    }
}
