package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.OutputResult;

public class MonteCarloCalc extends Calculation{
    public void monteCarloCalc(int[][][] cards, int playersRemainingNo, OutputResult outputResultObj) throws InterruptedException {
        initialise_variables(cards, playersRemainingNo, outputResultObj);

        String boardCards = convertBoardCardsToStr(cards[0]);

        String[] playerCards = convertPlayerCardsToStr(cards, playersRemainingNo);

        double[] result = nativeMonteCarloCalc(playerCards, boardCards);

        result = average_unknown_equity(result);

        outputResultObj.after_all_simulations(result);
    }

    public native double[] nativeMonteCarloCalc(String[] cards, String boardCards);

    public boolean during_simulations(double[] result) {
        result = average_unknown_equity(result);
        return outputResultObj.during_simulations(result);
    }
}
