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

    public boolean duringSimulations(double[][] results) {
        results = averageUnknownStats(results);
        return outputResultObj.duringSimulations(results);
    }

    public void afterAllSimulations(double[][] results) {
        results = averageUnknownStats(results);
        outputResultObj.afterAllSimulations(results);
    }
}
