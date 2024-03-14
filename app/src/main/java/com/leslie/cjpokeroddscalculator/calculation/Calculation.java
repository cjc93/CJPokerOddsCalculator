package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;

import java.util.List;

public class Calculation {
    protected boolean[] known_players;
    protected int no_of_unknown_players;

    public void initialiseVariables(List<CardRow> cardRows) {
        this.known_players = new boolean[cardRows.size() - 1];
        this.no_of_unknown_players = 0;

        for(int player = 1; player < cardRows.size(); player++) {
            CardRow cardRow = cardRows.get(player);
            if (cardRow.isKnownPlayer()) {
                known_players[player - 1] = true;
            } else {
                no_of_unknown_players++;
            }
        }
    }
}
