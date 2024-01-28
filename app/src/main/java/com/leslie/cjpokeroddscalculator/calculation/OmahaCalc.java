package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.CardRow;
import com.leslie.cjpokeroddscalculator.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;

public class OmahaCalc extends Calculation{
    public OmahaOutputResult outputResultObj;
    public int totalSimulations;

    public void initialiseVariables(CardRow[] cardRows, int playersRemainingNo, OmahaOutputResult outputResultObj) {
        super.initialiseVariables(cardRows, playersRemainingNo);
        this.outputResultObj = outputResultObj;
    }

    public String[][] convertPlayerCardsToStr(CardRow[] cardRows, int playersRemainingNo) {
        String[][] playerCards = new String[playersRemainingNo][];

        for (int i = 1; i <= playersRemainingNo; i++) {
            playerCards[i - 1] = ((SpecificCardsRow) cardRows[i]).convertOmahaCardsToStr();
        }

        return playerCards;
    }

    public double[][] averageUnknownStats(Equity[] eqs) {
        double unknown_players_equity = 0;
        double unknown_players_win = 0;
        double unknown_players_tie = 0;

        for(int i = 0; i < this.playersRemainingNo; i++) {
            if(!this.known_players[i]) {
                unknown_players_equity += eqs[i].total;
                unknown_players_win += eqs[i].won;
                unknown_players_tie += eqs[i].tied;
            }
        }

        double[] equity = new double[playersRemainingNo];
        double[] win = new double[playersRemainingNo];
        double[] tie = new double[playersRemainingNo];

        unknown_players_equity = unknown_players_equity / this.no_of_unknown_players;
        unknown_players_win = unknown_players_win / this.no_of_unknown_players;
        unknown_players_tie = unknown_players_tie / this.no_of_unknown_players;
        for(int i = 0; i < playersRemainingNo; i++) {
            if(known_players[i]) {
                equity[i] = eqs[i].total;
                win[i] = eqs[i].won;
                tie[i] = eqs[i].tied;
            } else {
                equity[i] = unknown_players_equity;
                win[i] = unknown_players_win;
                tie[i] = unknown_players_tie;
            }
        }

        return new double[][] {equity, win, tie};
    }
}
