package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemOutputResult;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;

import java.util.List;

public class TexasHoldemMonteCarloCalc extends TexasHoldemCalc {

    public void calculate(List<CardRow> cardRows, TexasHoldemOutputResult outputResultObj) throws InterruptedException {
        initialiseVariables(cardRows, outputResultObj);

        String boardCards = convertBoardCardsToStr(((SpecificCardsRow) cardRows.get(0)).cards);

        String[] playerCards = convertPlayerCardsToStr(cardRows);

        nativeMonteCarloCalc(playerCards, boardCards);
    }

    public native void nativeMonteCarloCalc(String[] cards, String boardCards);

    public boolean duringSimulations(double[] equity, double[] win, double[] highCard, double[] onePair, double[] twoPair, double[] threeOfAKind, double[] straight, double[] flush, double[] fullHouse, double[] fourOfAKind, double[] straightFlush) {
        double[][] result = averageUnknownStats(equity, win, highCard, onePair, twoPair, threeOfAKind, straight, flush, fullHouse, fourOfAKind, straightFlush);
        return outputResultObj.duringSimulations(result);
    }

    public void afterAllSimulations(double[] equity, double[] win, double[] highCard, double[] onePair, double[] twoPair, double[] threeOfAKind, double[] straight, double[] flush, double[] fullHouse, double[] fourOfAKind, double[] straightFlush) {
        double[][] result = averageUnknownStats(equity, win, highCard, onePair, twoPair, threeOfAKind, straight, flush, fullHouse, fourOfAKind, straightFlush);
        outputResultObj.afterAllSimulations(result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7], result[8], result[9], result[10]);
    }
}
