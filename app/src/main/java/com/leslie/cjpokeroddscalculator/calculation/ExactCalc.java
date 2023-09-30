package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.OutputResult;

public class ExactCalc extends Calculation {

    public void exactCalc(int[][][] cards, int playersRemainingNo, OutputResult outputResultObj) throws InterruptedException {
        initialise_variables(cards, playersRemainingNo, outputResultObj);

        String boardCards = convertBoardCardsToStr(cards[0]);

        String[] playerCards = convertPlayerCardsToStr(cards, playersRemainingNo);

        nativeExactCalc(playerCards, boardCards);
    }

    public native void nativeExactCalc(String[] cards, String boardCards);

    public boolean during_simulations() {
        return outputResultObj.during_simulations();
    }

    public void after_all_simulations(double[] result, boolean isCancelled) {
        result = average_unknown_equity(result);
        outputResultObj.after_all_simulations(result, isCancelled);
    }
}
