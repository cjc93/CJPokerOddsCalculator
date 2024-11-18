package com.leslie.cjpokeroddscalculator.calculation.pet;

/**
 * Represents the equity of a hand according to a specific valuation type
 */
public class Equity {

	/**
	 * percentage of hands won but not tied.
	 */
	public double winPercent;
	/** percentage of hands tied but not won */
	public double tiePercent;
	/** total equity percentage */
	public double equity;

	/** number of samples won */
	int winCount;
	/** number of samples tied */
	int tieCount;

	public double tieEquity;

	final int[] rankCount = new int[9];
	public final double[] rankPercent = new double[9];

	/**
	 * update percentage won, tied and rank
	 */
	void summariseEquity(int hands) {
		equity = (winCount + tieEquity) / hands;
		winPercent = (double) winCount / hands;
		tiePercent = (double) tieCount / hands;

		for (int n = 0; n < rankCount.length; n++) {
			rankPercent[n] = (double) rankCount[n] / hands;
		}
	}

	double[] toArray() {
		double[] arr = new double[3 + rankPercent.length];

		arr[0] = equity;
		arr[1] = winPercent;
		arr[2] = tiePercent;

		System.arraycopy(rankPercent, 0, arr, 3, rankCount.length);

		return arr;
	}
}
