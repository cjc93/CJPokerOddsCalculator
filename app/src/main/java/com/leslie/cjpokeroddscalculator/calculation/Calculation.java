package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;

import java.util.List;

public class Calculation {
    protected boolean[] knownPlayers;
    protected int numOfUnknownPlayers;

    public void initialiseVariables(List<CardRow> cardRows) {
        this.knownPlayers = new boolean[cardRows.size() - 1];
        this.numOfUnknownPlayers = 0;

        for(int player = 1; player < cardRows.size(); player++) {
            CardRow cardRow = cardRows.get(player);
            if (cardRow.isKnownPlayer()) {
                knownPlayers[player - 1] = true;
            } else {
                numOfUnknownPlayers++;
            }
        }
    }

    public double[][] averageUnknownStats(double[][] results) {
        double[] unknownPlayersStats = new double[results.length];

        for(int playerIdx = 0; playerIdx < this.knownPlayers.length; playerIdx++) {
            if(!knownPlayers[playerIdx]) {
                for(int statIdx = 0; statIdx < unknownPlayersStats.length; statIdx++) {
                    unknownPlayersStats[statIdx] += results[statIdx][playerIdx];
                }
            }
        }

        for(int statIdx = 0; statIdx < unknownPlayersStats.length; statIdx++) {
            unknownPlayersStats[statIdx] /= this.numOfUnknownPlayers;
        }

        for(int playerIdx = 0; playerIdx < knownPlayers.length; playerIdx++) {
            if(!knownPlayers[playerIdx]) {
                for(int statIdx = 0; statIdx < unknownPlayersStats.length; statIdx++) {
                    results[statIdx][playerIdx] = unknownPlayersStats[statIdx];
                }
            }
        }

        return results;
    }
}
