package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;

import java.util.List;

public class Calculation {
    protected boolean[] knownPlayers;
    protected int numOfUnknownPlayers;

    public void initialiseVariables(List<CardRow> playerCardRows) {
        this.knownPlayers = new boolean[playerCardRows.size()];
        this.numOfUnknownPlayers = 0;

        for(int player = 0; player < playerCardRows.size(); player++) {
            CardRow cardRow = playerCardRows.get(player);
            if (cardRow.isKnownPlayer()) {
                knownPlayers[player] = true;
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
