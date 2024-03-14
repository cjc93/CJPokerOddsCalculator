package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;

import java.util.List;

public class OmahaCalc extends Calculation{
    public OmahaOutputResult outputResultObj;
    public int totalSimulations;

    public void initialiseVariables(List<CardRow> cardRows, OmahaOutputResult outputResultObj) {
        super.initialiseVariables(cardRows);
        this.outputResultObj = outputResultObj;
    }

    public String[][] convertPlayerCardsToStr(List<CardRow> cardRows) {
        String[][] playerCards = new String[cardRows.size() - 1][];

        for (int i = 1; i < cardRows.size(); i++) {
            playerCards[i - 1] = ((SpecificCardsRow) cardRows.get(i)).convertOmahaCardsToStr();
        }

        return playerCards;
    }

    public double[][] averageUnknownStats(Equity[] eqs) {
        double unknown_players_equity = 0;
        double unknown_players_win = 0;
        double unknown_players_tie = 0;

        for(int i = 0; i < eqs.length; i++) {
            if(!this.known_players[i]) {
                unknown_players_equity += eqs[i].total;
                unknown_players_win += eqs[i].won;
                unknown_players_tie += eqs[i].tied;
            }
        }

        double[] equity = new double[eqs.length];
        double[] win = new double[eqs.length];
        double[] tie = new double[eqs.length];

        unknown_players_equity = unknown_players_equity / this.no_of_unknown_players;
        unknown_players_win = unknown_players_win / this.no_of_unknown_players;
        unknown_players_tie = unknown_players_tie / this.no_of_unknown_players;
        for(int i = 0; i < eqs.length; i++) {
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
