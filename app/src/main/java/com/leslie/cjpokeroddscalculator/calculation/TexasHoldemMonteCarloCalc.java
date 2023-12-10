package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.CardRow;
import com.leslie.cjpokeroddscalculator.OutputResult;
import com.leslie.cjpokeroddscalculator.SpecificCardsRow;

public class TexasHoldemMonteCarloCalc extends TexasHoldemCalc {

    public void monteCarloCalc(CardRow[] cardRows, int playersRemainingNo, OutputResult outputResultObj) throws InterruptedException {
        initialiseVariables(cardRows, playersRemainingNo, outputResultObj);

        String boardCards = convertBoardCardsToStr(((SpecificCardsRow) cardRows[0]).cards);

        String[] playerCards = convertPlayerCardsToStr(cardRows, playersRemainingNo);

        nativeMonteCarloCalc(playerCards, boardCards);
    }

    public native void nativeMonteCarloCalc(String[] cards, String boardCards);

    public boolean during_simulations(double[] equity, double[] win) {
        double[][] result = average_unknown_equity(equity, win);
        return outputResultObj.during_simulations(result);
    }

    public void after_all_simulations(double[] equity, double[] win) {
        double[][] result = average_unknown_equity(equity, win);
        outputResultObj.after_all_simulations(result[0], result[1]);
    }
}
