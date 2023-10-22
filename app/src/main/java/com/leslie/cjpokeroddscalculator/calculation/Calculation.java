package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.CardRow;
import com.leslie.cjpokeroddscalculator.OutputResult;
import com.leslie.cjpokeroddscalculator.GlobalStatic;

public class Calculation {
    static {
        System.loadLibrary("cjpokeroddscalculator");
    }

    public OutputResult outputResultObj;
    protected boolean[] known_players;
    protected int no_of_unknown_players;
    protected int playersRemainingNo;

    public void initialise_variables(CardRow[] cardRows, int playersRemainingNo, OutputResult outputResultObj) {
        this.playersRemainingNo = playersRemainingNo;
        this.outputResultObj = outputResultObj;

        this.known_players = new boolean[playersRemainingNo];
        this.no_of_unknown_players = 0;

        for(int player = 1; player <= playersRemainingNo; player++) {
            CardRow cardRow = cardRows[player];
            if (cardRow.isKnownPlayer()) {
                known_players[player - 1] = true;
            } else {
                no_of_unknown_players++;
            }
        }
    }

    public String convertBoardCardsToStr(int[][] cards) {
        StringBuilder boardCards = new StringBuilder();
        for (int[] card : cards) {
            boardCards.append(GlobalStatic.rankToStr.get(card[1]));
            boardCards.append(GlobalStatic.suitToStr.get(card[0]));
        }
        return String.valueOf(boardCards);
    }

    public String[] convertPlayerCardsToStr(CardRow[] cardRows, int playersRemainingNo) {
        String[] playerCards = new String[playersRemainingNo];

        for (int i = 1; i <= playersRemainingNo; i++) {
            CardRow cardRow = cardRows[i];
            playerCards[i - 1] = cardRow.convertPlayerCardsToStr();
        }

        return playerCards;
    }

    public double[] average_unknown_equity(double[] result) {
        double known_players_equity = 0;

        for(int i = 0; i < this.playersRemainingNo; i++) {
            if(this.known_players[i]) {
                known_players_equity += result[i];
            }
        }

        double unknown_players_equity = (1 - known_players_equity) / this.no_of_unknown_players;
        for(int i = 0; i < playersRemainingNo; i++) {
            if(!known_players[i]) {
                result[i] = unknown_players_equity;
            }
        }

        return result;
    }
}
