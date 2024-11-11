package com.leslie.cjpokeroddscalculator.calculation.pet;

/**
 * utility methods for Equity class
 */
public class EquityUtil {

	/**
	 * Make array of multiple hand equities,
	 * number of remaining cards, and calculation method
	 */
	public static Equity[] createEquities(int hands) {
		Equity[] eqs = new Equity[hands];
		for (int n = 0; n < eqs.length; n++) {
			eqs[n] = new Equity();
		}
		return eqs;
	}

	public static void updateEquities(Equity[] eqs, int[] vals) {
		// find highest hand and number of times it occurs
		int max = 0, maxcount = 0, rank;
        for (int v : vals) {
            if (v > max) {
                max = v;
                maxcount = 1;
            } else if (v == max) {
                maxcount++;
            }
        }

        for (int i = 0; i < vals.length; i++) {
			Equity e = eqs[i];

			rank = (vals[i] & 0x00f00000) >> 20;
			e.rankcount[rank]++;

			if (vals[i] == max) {
				if (maxcount == 1) {
                    e.woncount++;
				} else {
					e.tiedcount++;
					e.tiedequity += 1.0 / maxcount;
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
}
