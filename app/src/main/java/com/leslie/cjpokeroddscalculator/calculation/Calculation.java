package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.CardRow;
import com.leslie.cjpokeroddscalculator.OutputResult;

import java.util.HashMap;
import java.util.StringJoiner;

public class Calculation {
    static {
        System.loadLibrary("cjpokeroddscalculator");
    }

    public OutputResult outputResultObj;
    protected boolean[] known_players;
    protected int no_of_unknown_players;
    protected int playersRemainingNo;

    HashMap<Integer, String> rankToStr = new HashMap<>();
    HashMap<Integer, String> suitToStr = new HashMap<>();

    String[] all_possible_cards = new String[] {
        "2d","3d","4d","5d","6d","7d","8d","9d","Td","Jd","Qd","Kd","Ad",
        "2c","3c","4c","5c","6c","7c","8c","9c","Tc","Jc","Qc","Kc","Ac",
        "2h","3h","4h","5h","6h","7h","8h","9h","Th","Jh","Qh","Kh","Ah",
        "2s","3s","4s","5s","6s","7s","8s","9s","Ts","Js","Qs","Ks","As"
    };

    public Calculation() {
        rankToStr.put(0, "");
        rankToStr.put(2, "2");
        rankToStr.put(3, "3");
        rankToStr.put(4, "4");
        rankToStr.put(5, "5");
        rankToStr.put(6, "6");
        rankToStr.put(7, "7");
        rankToStr.put(8, "8");
        rankToStr.put(9, "9");
        rankToStr.put(10, "T");
        rankToStr.put(11, "J");
        rankToStr.put(12, "Q");
        rankToStr.put(13, "K");
        rankToStr.put(14, "A");

        suitToStr.put(0, "");
        suitToStr.put(1, "d");
        suitToStr.put(2, "c");
        suitToStr.put(3, "h");
        suitToStr.put(4, "s");
    }

    public void initialise_variables(CardRow[] cardRows, int playersRemainingNo, OutputResult outputResultObj) {
        this.playersRemainingNo = playersRemainingNo;
        this.outputResultObj = outputResultObj;

        this.known_players = new boolean[playersRemainingNo];
        this.no_of_unknown_players = 0;

        for(int player = 1; player <= playersRemainingNo; player++) {
            if(cardRows[player].cards[0][0] != 0 || cardRows[player].cards[1][0] != 0) {
                known_players[player - 1] = true;
            } else {
                no_of_unknown_players++;
            }
        }
    }

    public String convertBoardCardsToStr(int[][] cards) {
        StringBuilder boardCards = new StringBuilder();
        for (int[] card : cards) {
            boardCards.append(rankToStr.get(card[1]));
            boardCards.append(suitToStr.get(card[0]));
        }
        return String.valueOf(boardCards);
    }

    public String[] convertPlayerCardsToStr(CardRow[] cardRows, int playersRemainingNo) {
        String[] playerCards = new String[playersRemainingNo];

        for (int i = 1; i <= playersRemainingNo; i++) {
            StringBuilder temp = new StringBuilder();
            temp.append(rankToStr.get(cardRows[i].cards[0][1]));
            temp.append(suitToStr.get(cardRows[i].cards[0][0]));
            temp.append(rankToStr.get(cardRows[i].cards[1][1]));
            temp.append(suitToStr.get(cardRows[i].cards[1][0]));
            if (String.valueOf(temp).equals("")) {
                playerCards[i - 1] = "random";
            } else if (temp.length() == 2) {
                StringJoiner sj = new StringJoiner(",");
                for (String card : all_possible_cards) {
                    if (!card.equals(String.valueOf(temp))) {
                        sj.add(temp + card);
                    }
                }
                playerCards[i - 1] = sj.toString();
            } else {
                playerCards[i - 1] = String.valueOf(temp);
            }
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
