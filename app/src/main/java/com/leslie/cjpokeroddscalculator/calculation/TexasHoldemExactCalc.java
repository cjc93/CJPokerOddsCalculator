package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemOutputResult;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;

import java.util.List;

public class TexasHoldemExactCalc extends TexasHoldemCalc {

    public void calculate(List<CardRow> cardRows, TexasHoldemOutputResult outputResultObj) throws InterruptedException {
        initialiseVariables(cardRows, outputResultObj);

        String boardCards = convertBoardCardsToStr(((SpecificCardsRow) cardRows.get(0)).cards);

        String[] playerCards = convertPlayerCardsToStr(cardRows);

        nativeExactCalc(playerCards, boardCards);
    }

    public native void nativeExactCalc(String[] cards, String boardCards);

    public boolean duringSimulations() {
        return outputResultObj.duringSimulations();
    }

    public void afterAllSimulations(double[][] results, boolean isCancelled) {
        results = averageUnknownStats(results);
        outputResultObj.afterAllSimulations(results, isCancelled);
    }
}
