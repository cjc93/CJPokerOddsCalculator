package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.CardRow;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemOutputResult;
import com.leslie.cjpokeroddscalculator.SpecificCardsRow;

public class TexasHoldemMonteCarloCalc extends TexasHoldemCalc {

    public void calculate(CardRow[] cardRows, int playersRemainingNo, TexasHoldemOutputResult outputResultObj) throws InterruptedException {
        initialiseVariables(cardRows, playersRemainingNo, outputResultObj);

        String boardCards = convertBoardCardsToStr(((SpecificCardsRow) cardRows[0]).cards);

        String[] playerCards = convertPlayerCardsToStr(cardRows, playersRemainingNo);

        nativeMonteCarloCalc(playerCards, boardCards);
    }

    public native void nativeMonteCarloCalc(String[] cards, String boardCards);

    public boolean during_simulations(double[] equity, double[] win) {
        double[][] result = averageUnknownStats(equity, win);
        return outputResultObj.during_simulations(result);
    }

    public void afterAllSimulations(double[] equity, double[] win) {
        double[][] result = averageUnknownStats(equity, win);
        outputResultObj.after_all_simulations(result[0], result[1]);
    }
}
