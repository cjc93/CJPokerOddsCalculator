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

        double[] unknownPlayersRanks = new double[9];

        for(int i = 0; i < eqs.length; i++) {
            if(!this.knownPlayers[i]) {
                unknownPlayersEquity += eqs[i].total;
                unknownPlayersWin += eqs[i].won;
                unknownPlayersTie += eqs[i].tied;

                for (int n = 0; n < unknownPlayersRanks.length; n++) {
                    unknownPlayersRanks[n] += eqs[i].rankpercent[n];
                }
            }
        }

        double[] equity = new double[eqs.length];
        double[] win = new double[eqs.length];
        double[] tie = new double[eqs.length];
        double[][] rankPercents = new double[unknownPlayersRanks.length][eqs.length];

        unknownPlayersEquity = unknownPlayersEquity / this.numOfUnknownPlayers;
        unknownPlayersWin = unknownPlayersWin / this.numOfUnknownPlayers;
        unknownPlayersTie = unknownPlayersTie / this.numOfUnknownPlayers;

        for (int n = 0; n < unknownPlayersRanks.length; n++) {
            unknownPlayersRanks[n] = unknownPlayersRanks[n] / this.numOfUnknownPlayers;
        }

        for(int i = 0; i < eqs.length; i++) {
            if(knownPlayers[i]) {
                equity[i] = eqs[i].total;
                win[i] = eqs[i].won;
                tie[i] = eqs[i].tied;

                for (int n = 0; n < eqs[i].rankpercent.length; n++) {
                    rankPercents[n][i] = eqs[i].rankpercent[n];
                }
            } else {
                equity[i] = unknownPlayersEquity;
                win[i] = unknownPlayersWin;
                tie[i] = unknownPlayersTie;

                for (int n = 0; n < unknownPlayersRanks.length; n++) {
                    rankPercents[n][i] = unknownPlayersRanks[n];
                }
            }
        }

        return new double[][] {equity, win, tie, rankPercents[0], rankPercents[1], rankPercents[2], rankPercents[3], rankPercents[4], rankPercents[5], rankPercents[6], rankPercents[7], rankPercents[8]};
    }
}
