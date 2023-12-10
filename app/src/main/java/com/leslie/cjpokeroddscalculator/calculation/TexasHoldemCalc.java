package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.CardRow;
import com.leslie.cjpokeroddscalculator.OutputResult;
import com.leslie.cjpokeroddscalculator.GlobalStatic;

public class TexasHoldemCalc extends Calculation{
    static {
        System.loadLibrary("cjpokeroddscalculator");
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
            playerCards[i - 1] = cardRows[i].convertPlayerCardsToStr();
        }

        return playerCards;
    }

    public double[][] average_unknown_equity(double[] equity, double[] win) {
        double unknown_players_equity = 0;
        double unknown_players_win = 0;

        for(int i = 0; i < this.playersRemainingNo; i++) {
            if(!this.known_players[i]) {
                unknown_players_equity += equity[i];
                unknown_players_win += win[i];
            }
        }

        unknown_players_equity = unknown_players_equity / this.no_of_unknown_players;
        unknown_players_win = unknown_players_win / this.no_of_unknown_players;
        for(int i = 0; i < playersRemainingNo; i++) {
            if(!known_players[i]) {
                equity[i] = unknown_players_equity;
                win[i] = unknown_players_win;
            }
        }

        return new double[][] {equity, win};
    }
}
