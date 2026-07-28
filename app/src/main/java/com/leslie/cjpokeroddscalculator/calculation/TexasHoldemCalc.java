package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;

import java.util.List;

public class TexasHoldemCalc extends Calculation{
    static {
        System.loadLibrary("cjpokeroddscalculator");
    }

    public TexasHoldemProgressListener texasHoldemProgressListener;

    public void initialiseVariables(List<CardRow> cardRows, TexasHoldemProgressListener texasHoldemProgressListener) {
        super.initialiseVariables(cardRows);
        this.texasHoldemProgressListener = texasHoldemProgressListener;
    }

    public String convertBoardCardsToStr(String[] cards) {
        StringBuilder boardCards = new StringBuilder();
        for (String card : cards) {
            boardCards.append(card);
        }
        return String.valueOf(boardCards);
    }

    public String[] convertPlayerCardsToStr(List<CardRow> cardRows) {
        String[] playerCards = new String[cardRows.size() - 1];

        for (int i = 1; i < cardRows.size(); i++) {
            playerCards[i - 1] = cardRows.get(i).convertTexasHoldemPlayerCardsToStr();
        }

        return playerCards;
    }
}
