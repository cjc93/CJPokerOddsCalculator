package com.leslie.cjpokeroddscalculator.calculation.pet;

/**
 * Represents the equity of a hand according to a specific valuation type
 */
public class Equity {

	/**
	 * percentage of hands won but not tied.
	 */
	public double won;
	/** percentage of hands tied but not won */
	public double tied;
	/** total equity percentage */
	public double total;

	/** number of samples won */
	int woncount;
	/** number of samples tied */
	int tiedcount;

	public double tiedequity;

	final int[] rankcount = new int[9];
	public final double[] rankpercent = new double[9];

	/**
	 * update percentage won, tied and by rank
	 */
	void summariseEquity(int hands) {
		won = (double) woncount / hands;
		tied = (double) tiedcount / hands;

		total = (woncount + tiedequity) / hands;

		for (int n = 0; n < rankcount.length; n++) {
			rankpercent[n] = (double) rankcount[n] / hands;
		}
	}
}
