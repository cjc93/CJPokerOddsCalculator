package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;

import java.util.List;

public class TexasHoldemMonteCarloCalc extends TexasHoldemCalc {

    public void calculate(SpecificCardsRow boardCardRow, List<CardRow> playerCardRows, TexasHoldemProgressListener texasHoldemProgressListener) throws InterruptedException {
        initialiseVariables(playerCardRows, texasHoldemProgressListener);

        String boardCards = convertBoardCardsToStr(boardCardRow.cards);

        String[] playerCards = convertPlayerCardsToStr(playerCardRows);

        nativeMonteCarloCalc(playerCards, boardCards);
    }

    public native void nativeMonteCarloCalc(String[] cards, String boardCards);

    public boolean duringSimulations(double[][] results) {
        results = averageUnknownStats(results);
        return texasHoldemProgressListener.duringSimulations(results);
    }

    public void afterAllSimulations(double[][] results) {
        results = averageUnknownStats(results);
        texasHoldemProgressListener.afterAllSimulations(results);
    }
}
