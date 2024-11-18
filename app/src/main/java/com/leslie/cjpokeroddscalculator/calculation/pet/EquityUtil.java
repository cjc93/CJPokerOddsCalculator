package com.leslie.cjpokeroddscalculator.calculation.pet;

/**
 * utility methods for Equity class
 */
public class EquityUtil {

	/**
	 * Make array of multiple hand equities
	 */
	public static Equity[] createEquities(int hands) {
		Equity[] eqs = new Equity[hands];
		for (int n = 0; n < eqs.length; n++) {
			eqs[n] = new Equity();
		}
		return eqs;
	}

	public static EquityHiLo[] createEquitiesHiLo(int hands) {
		EquityHiLo[] eqs = new EquityHiLo[hands];
		for (int n = 0; n < eqs.length; n++) {
			eqs[n] = new EquityHiLo();
		}
		return eqs;
	}

	public static void updateEquities(Equity[] eqs, int[] vals) {
		// find highest hand and number of times it occurs
		int max = 0, maxCount = 0, rank;
        for (int v : vals) {
            if (v > max) {
                max = v;
                maxCount = 1;
            } else if (v == max) {
                maxCount++;
            }
        }

        for (int i = 0; i < vals.length; i++) {
			Equity e = eqs[i];

			rank = (vals[i] & Poker.RANK) >> 20;
			e.rankCount[rank]++;

			if (vals[i] == max) {
				if (maxCount == 1) {
                    e.winCount++;
				} else {
					e.tieCount++;
					e.tieEquity += 1.0 / maxCount;
				}
			}
		}
	}

	public static void updateEquitiesHiLo(EquityHiLo[] eqs, int[] hiVals, int[] loVals) {
		int bestHi = 0, bestHiCount = 0, rank;
		int bestLo = 0, bestLoCount = 0;
		int v;
		double highPot;

		for (int i = 0; i < hiVals.length; i++) {
			v = hiVals[i];
			if (v > bestHi) {
				bestHi = v;
				bestHiCount = 1;
			} else if (v == bestHi) {
				bestHiCount++;
			}

			if (loVals != null) {
				v = loVals[i];
				if (v > bestLo) {
					bestLo = v;
					bestLoCount = 1;
				} else if (v == bestLo) {
					bestLoCount++;
				}
			}
		}

		for (int i = 0; i < hiVals.length; i++) {
			EquityHiLo e = eqs[i];
			if (bestLo > 0) {
				v = loVals[i];
				if (v > 0) {
					e.lowCount++;
				}

				if (v == bestLo) {
					if (bestLoCount == 1) {
						e.winLoCount++;
					} else {
						e.tieLoCount++;
						e.tieLoEquity += 0.5 / bestLoCount;
					}
				}

				highPot = 0.5;
			} else {
				highPot = 1.0;
			}

			v = hiVals[i];
			rank = (v & Poker.RANK) >> 20;
			e.rankCount[rank]++;

			if (v == bestHi) {
				if (bestHiCount == 1) {
					if (bestLo > 0) {
						e.winHiHalfCount++;
					} else {
						e.winCount++;
					}
				} else {
					e.tieCount++;
					e.tieEquity += highPot / bestHiCount;
				}
			}
		}
	}

	/**
	 * summarise equities (convert counts to percentages)
	 */
	public static void summariseEquities(Equity[] eqs, int count) {
		for (Equity eq : eqs) {
			eq.summariseEquity(count);
		}
	}

	public static double[][] convertEquitiesToMatrix(Equity[] eqs) {
		if (eqs.length > 0) {
			double[] playerArray = eqs[0].toArray();
			double[][] results = new double[playerArray.length][eqs.length];

			for (int statsIdx = 0; statsIdx < playerArray.length; statsIdx++) {
				results[statsIdx][0] = playerArray[statsIdx];
			}

			for (int playerIdx = 0; playerIdx < eqs.length; playerIdx++) {
				playerArray = eqs[playerIdx].toArray();
				for (int statsIdx = 0; statsIdx < playerArray.length; statsIdx++) {
					results[statsIdx][playerIdx] = playerArray[statsIdx];
				}
			}
			return results;
		} else {
			return new double[][]{};
		}
	}
}
