package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemOutputResult;

import java.util.List;

public class TexasHoldemCalc extends Calculation{
    static {
        System.loadLibrary("cjpokeroddscalculator");
    }

    public TexasHoldemOutputResult outputResultObj;

    public void initialiseVariables(List<CardRow> cardRows, TexasHoldemOutputResult outputResultObj) {
        super.initialiseVariables(cardRows);
        this.outputResultObj = outputResultObj;
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
