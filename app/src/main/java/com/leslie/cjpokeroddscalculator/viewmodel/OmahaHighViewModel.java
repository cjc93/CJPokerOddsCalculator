package com.leslie.cjpokeroddscalculator.viewmodel;

public class OmahaHighViewModel extends EquityCalculatorViewModel {
    public OmahaHighViewModel() {
        double[] initialSinglePlayerStats = new double[]{
            0.5,
            0.4929,
            0.0142,
            0.0299,
            0.2647,
            0.3683,
            0.0879,
            0.1127,
            0.0672,
            0.0635,
            0.0048,
            0.0009
        };

        double[][] initialStats = new double[2][];

        for (int playerIdx = 0; playerIdx < 2; playerIdx++) {
            initialStats[playerIdx] = initialSinglePlayerStats;
        }

        stats.postValue(initialStats);
    }
}
