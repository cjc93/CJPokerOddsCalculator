package com.leslie.cjpokeroddscalculator.calculation.pet;

public class EquityHiLo extends Equity {
    /**
     * percentage of hands won but not tied.
     */
    public double winLoPercent;
    /** percentage of hands tied but not won */
    public double tieLoPercent;

    /** number of samples won */
    int winLoCount;
    /** number of samples tied */
    int tieLoCount;

    public double tieLoEquity;

    int lowCount;
    int winHiHalfCount;

    public double lowPercent;


    /**
     * update percentage won, tied and rank
     */
    void summariseEquity(int hands) {
        equity = (tieLoEquity + tieEquity + winCount + (winLoCount + winHiHalfCount) / 2.0 ) / hands;

        winPercent = (double) (winCount + winHiHalfCount) / hands;
        winLoPercent = (double) winLoCount / hands;

        tiePercent = (double) tieCount / hands;
        tieLoPercent = (double) tieLoCount / hands;

        for (int n = 0; n < rankCount.length; n++) {
            rankPercent[n] = (double) rankCount[n] / hands;
        }

        lowPercent = (double) lowCount / hands;
    }

    double[] toArray() {
        double[] arr = new double[6 + rankPercent.length];

        arr[0] = equity;
        arr[1] = winPercent;
        arr[2] = winLoPercent;
        arr[3] = tiePercent;
        arr[4] = tieLoPercent;

        System.arraycopy(rankPercent, 0, arr, 5, rankPercent.length);

        arr[14] = lowPercent;

        return arr;
    }
}
