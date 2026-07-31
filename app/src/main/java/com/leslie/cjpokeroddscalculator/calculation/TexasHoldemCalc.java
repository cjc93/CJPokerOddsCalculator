package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;

import java.util.List;

public class TexasHoldemCalc extends Calculation{
    static {
        System.loadLibrary("cjpokeroddscalculator");
    }

    public TexasHoldemProgressListener texasHoldemProgressListener;

    public void initialiseVariables(List<CardRow> playerCardRows, TexasHoldemProgressListener texasHoldemProgressListener) {
        super.initialiseVariables(playerCardRows);
        this.texasHoldemProgressListener = texasHoldemProgressListener;
    }

    public String convertBoardCardsToStr(String[] cards) {
        StringBuilder boardCards = new StringBuilder();
        for (String card : cards) {
            boardCards.append(card);
        }
        return String.valueOf(boardCards);
    }

    public String[] convertPlayerCardsToStr(List<CardRow> playerCardRows) {
        String[] playerCards = new String[playerCardRows.size()];

        for (int i = 0; i < playerCardRows.size(); i++) {
            playerCards[i] = playerCardRows.get(i).convertTexasHoldemPlayerCardsToStr();
        }

        return playerCards;
    }
}
