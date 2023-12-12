package com.leslie.cjpokeroddscalculator.calculation.pet;

import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;

/**
 * Omaha hand analysis, using a combinatorial number system.
 */
public class OmahaPoker extends Poker {

	public OmahaOutputResult omahaOutputResult;

	public OmahaPoker(OmahaOutputResult omahaOutputResult) {
		this.omahaOutputResult = omahaOutputResult;
	}


	/**
	 * Calc exact omaha hand equity for each hand for given board
	 */
	public Equity[] equityImpl(final Cards cards) throws InterruptedException {
		final Equity[] eqs = EquityUtil.createEquities(cards.currentCards.length - 1);
		final int[] vals = new int[cards.currentCards.length - 1];
        final String[] temp = new String[5];

		final int count = cards.count();

		for (int p = 0; p < count; p++) {
			cards.next();

			for (int i = 0; i < cards.currentCards.length - 1; i++) {
				vals[i] = heValue(cards.cards[0], cards.cards[i + 1], temp);
			}

			EquityUtil.updateEquities(eqs, vals);

			omahaOutputResult.duringSimulations(eqs, p + 1);
        }

		EquityUtil.summariseEquities(eqs, count);

		return eqs;
	}
	

	/**
	 * Calculate value of omaha hand (using at least min cards from hand).
	 * Board can be 3-5 cards.
	 */
	private int heValue(final String[] board, final String[] hole, final String[] temp) {
		int hv = 0;
		final int nh = MathsUtil.binomialCoefficientFast(hole.length, 2);
		final int nb = MathsUtil.binomialCoefficientFast(board.length, 3);
		for (int kh = 0; kh < nh; kh++) {
			MathsUtil.kCombination(2, kh, hole, temp, 0);
			for (int kb = 0; kb < nb; kb++) {
				MathsUtil.kCombination(3, kb, board, temp, 2);
				final int val = Poker.value(temp);
				if (val > hv) {
					hv = val;
				}
			}
		}
		return hv;
	}
}
