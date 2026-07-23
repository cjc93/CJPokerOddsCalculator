package com.leslie.cjpokeroddscalculator.viewmodel;

public class OmahaHiLo5ViewModel extends OmahaHiLoViewModel {
    public OmahaHiLo5ViewModel() {
        cardsPerHand = 5;

        double[] initialSinglePlayerStats = new double[]{
            0.5,
            0.4898,
            0.2644,
            0.0203,
            0.0263,
            0.0078,
            0.1668,
            0.3762,
            0.0965,
            0.1525,
            0.0961,
            0.0956,
            0.0072,
            0.0014,
            0.4302
        };

        double[][] initialStats = new double[2][];

        for (int playerIdx = 0; playerIdx < 2; playerIdx++) {
            initialStats[playerIdx] = initialSinglePlayerStats;
        }

        stats.postValue(initialStats);
    }
}