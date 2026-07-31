package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;

import java.util.List;

public class TexasHoldemExactCalc extends TexasHoldemCalc {

    public void calculate(SpecificCardsRow boardCardRow, List<CardRow> playerCardRows, TexasHoldemProgressListener texasHoldemProgressListener) throws InterruptedException {
        initialiseVariables(playerCardRows, texasHoldemProgressListener);

        String boardCards = convertBoardCardsToStr(boardCardRow.cards);

        String[] playerCards = convertPlayerCardsToStr(playerCardRows);

        nativeExactCalc(playerCards, boardCards);
    }

    public native void nativeExactCalc(String[] cards, String boardCards);

    public boolean duringSimulations() {
        return texasHoldemProgressListener.duringSimulations();
    }

    public void afterAllSimulations(double[][] results, boolean isCancelled) {
        results = averageUnknownStats(results);
        texasHoldemProgressListener.afterAllSimulations(results, isCancelled);
    }
}
