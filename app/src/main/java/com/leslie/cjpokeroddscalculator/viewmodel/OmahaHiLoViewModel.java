package com.leslie.cjpokeroddscalculator.viewmodel;

public class OmahaHiLoViewModel extends OmahaHighViewModel {
    public OmahaHiLoViewModel() {
        double[] initialSinglePlayerStats = new double[]{
            0.5,
            0.4929,
            0.2398,
            0.0142,
            0.0168,
            0.0298,
            0.2645,
            0.3686,
            0.0879,
            0.1128,
            0.0673,
            0.0635,
            0.0048,
            0.0009,
            0.3481
        };

        double[][] initialStats = new double[2][];

        for (int playerIdx = 0; playerIdx < 2; playerIdx++) {
            initialStats[playerIdx] = initialSinglePlayerStats;
        }

        stats.postValue(initialStats);
    }
}
