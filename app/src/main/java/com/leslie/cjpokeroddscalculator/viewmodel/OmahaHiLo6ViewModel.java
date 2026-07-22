package com.leslie.cjpokeroddscalculator.viewmodel;

public class OmahaHiLo6ViewModel extends OmahaHiLoViewModel {
    public OmahaHiLo6ViewModel() {
        double[] initialSinglePlayerStats = new double[]{
            0.5,
            0.4862,
            0.2725,
            0.0275,
            0.0372,
            0.0014,
            0.0979,
            0.3485,
            0.1027,
            0.1855,
            0.1225,
            0.1292,
            0.0101,
            0.0022,
            0.4889
        };

        double[][] initialStats = new double[2][];

        for (int playerIdx = 0; playerIdx < 2; playerIdx++) {
            initialStats[playerIdx] = initialSinglePlayerStats;
        }

        stats.postValue(initialStats);
    }
}