package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.CardRow;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemOutputResult;

public class Calculation {
    public TexasHoldemOutputResult outputResultObj;
    protected boolean[] known_players;
    protected int no_of_unknown_players;
    protected int playersRemainingNo;

    public void initialiseVariables(CardRow[] cardRows, int playersRemainingNo, TexasHoldemOutputResult outputResultObj) {
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
}
