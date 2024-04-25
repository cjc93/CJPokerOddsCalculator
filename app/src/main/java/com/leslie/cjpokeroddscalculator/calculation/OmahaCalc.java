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
        double unknownPlayersEquity = 0;
        double unknownPlayersWin = 0;
        double unknownPlayersTie = 0;

        for(int i = 0; i < eqs.length; i++) {
            if(!this.knownPlayers[i]) {
                unknownPlayersEquity += eqs[i].total;
                unknownPlayersWin += eqs[i].won;
                unknownPlayersTie += eqs[i].tied;
            }
        }

        double[] equity = new double[eqs.length];
        double[] win = new double[eqs.length];
        double[] tie = new double[eqs.length];

        unknownPlayersEquity = unknownPlayersEquity / this.numOfUnknownPlayers;
        unknownPlayersWin = unknownPlayersWin / this.numOfUnknownPlayers;
        unknownPlayersTie = unknownPlayersTie / this.numOfUnknownPlayers;
        for(int i = 0; i < eqs.length; i++) {
            if(knownPlayers[i]) {
                equity[i] = eqs[i].total;
                win[i] = eqs[i].won;
                tie[i] = eqs[i].tied;
            } else {
                equity[i] = unknownPlayersEquity;
                win[i] = unknownPlayersWin;
                tie[i] = unknownPlayersTie;
            }
        }

        return new double[][] {equity, win, tie};
    }
}
