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
	/** number of people tied with including self */
	int tiedwithcount;

	/**
	 * update percentage won, tied and by rank
	 */
	void summariseEquity(int hands) {
		won = (woncount * 100.0) / hands;
		tied = (tiedcount * 100.0) / hands;

		total = (woncount * 100.0) / hands;
		if (tiedcount > 0) {
			total += (tied * ((tiedcount * 1.0) / tiedwithcount));
		}
	}
}
