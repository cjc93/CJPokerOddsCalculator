package com.leslie.cjpokeroddscalculator.viewmodel;

public class OmahaHigh6ViewModel extends OmahaHighViewModel {
    public OmahaHigh6ViewModel() {
        cardsPerHand = 6;

        double[] initialSinglePlayerStats = new double[]{
            0.5,
            0.4864,
            0.0273,
            0.0014,
            0.0978,
            0.3479,
            0.1032,
            0.1860,
            0.1224,
            0.1289,
            0.0101,
            0.0022
        };

        double[][] initialStats = new double[2][];

        for (int playerIdx = 0; playerIdx < 2; playerIdx++) {
            initialStats[playerIdx] = initialSinglePlayerStats;
        }

        stats.postValue(initialStats);
    }
}
