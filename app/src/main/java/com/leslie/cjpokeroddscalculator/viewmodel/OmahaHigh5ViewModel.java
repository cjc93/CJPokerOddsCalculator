package com.leslie.cjpokeroddscalculator.viewmodel;

public class OmahaHigh5ViewModel extends OmahaHighViewModel {
    public OmahaHigh5ViewModel() {
        cardsPerHand = 5;

        double[] initialSinglePlayerStats = new double[]{
            0.5,
            0.4898,
            0.0204,
            0.0078,
            0.1668,
            0.376,
            0.0966,
            0.1525,
            0.0961,
            0.0956,
            0.0072,
            0.0014
        };

        double[][] initialStats = new double[2][];

        for (int playerIdx = 0; playerIdx < 2; playerIdx++) {
            initialStats[playerIdx] = initialSinglePlayerStats;
        }

        stats.postValue(initialStats);
    }
}
