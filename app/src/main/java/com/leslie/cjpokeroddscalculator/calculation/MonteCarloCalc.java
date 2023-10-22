package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.CardRow;
import com.leslie.cjpokeroddscalculator.OutputResult;
import com.leslie.cjpokeroddscalculator.SpecificCardsRow;

public class MonteCarloCalc extends Calculation{

    public void monteCarloCalc(CardRow[] cardRows, int playersRemainingNo, OutputResult outputResultObj) throws InterruptedException {
        initialise_variables(cardRows, playersRemainingNo, outputResultObj);

        String boardCards = convertBoardCardsToStr(((SpecificCardsRow) cardRows[0]).cards);

        String[] playerCards = convertPlayerCardsToStr(cardRows, playersRemainingNo);

        nativeMonteCarloCalc(playerCards, boardCards);
    }

    public native void nativeMonteCarloCalc(String[] cards, String boardCards);

    public boolean during_simulations(double[] result) {
        result = average_unknown_equity(result);
        return outputResultObj.during_simulations(result);
    }

    public void after_all_simulations(double[] result) {
        result = average_unknown_equity(result);
        outputResultObj.after_all_simulations(result);
    }
}
