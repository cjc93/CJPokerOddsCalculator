package com.leslie.cjpokeroddscalculator.calculation.pet;

import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;

/**
 * Omaha hand analysis, using a combinatorial number system.
 */
public class OmahaHiLoPoker extends OmahaPoker {
    public OmahaHiLoPoker(OmahaOutputResult omahaOutputResult) {
        super(omahaOutputResult);
    }


    /**
     * Calc exact omaha hand equity for each hand for given board
     */
    public Equity[] equityImpl(final Cards cards) throws InterruptedException {
        final boolean lowPossible;
        if (cards.currentCards[0].length > 2) {
            // only possible if there are no more than 2 high cards on board
            lowPossible = cards.currentCards[0].length - lowCount(cards.currentCards[0]) <= 2;
        } else {
            lowPossible = true;
        }

        final EquityHiLo[] eqs = EquityUtil.createEquitiesHiLo(cards.currentCards.length - 1);
        final int[] hivals = new int[cards.currentCards.length - 1];
        final int[] lovals = lowPossible ? new int[cards.currentCards.length - 1] : null;
        final String[] temp = new String[5];

        final int count = cards.count();

        for (int p = 0; p < count; p++) {
            cards.next();

            for (int i = 0; i < cards.currentCards.length - 1; i++) {
                if (lowPossible) {
                    int[] hiLoValue = calcHiLoValue(cards.cards[0], cards.cards[i + 1], temp);
                    hivals[i] = hiLoValue[0];
                    lovals[i] = hiLoValue[1];
                } else {
                    hivals[i] = calcHighValue(cards.cards[0], cards.cards[i + 1], temp);
                }
            }

            EquityUtil.updateEquitiesHiLo(eqs, hivals, lovals);

            omahaOutputResult.duringSimulations(eqs, p + 1);
        }

        EquityUtil.summariseEquities(eqs, count);

        return eqs;
    }


    /**
     * Calculate value of omaha hand (using 2 cards from hand).
     */
    private int[] calcHiLoValue(final String[] board, final String[] hole, final String[] temp) {
        int hiValue = 0, loValue = 0;

        final int nh = MathsUtil.binomialCoefficientFast(hole.length, 2);
        final int nb = MathsUtil.binomialCoefficientFast(board.length, 3);
        for (int kh = 0; kh < nh; kh++) {
            MathsUtil.kCombination(2, kh, hole, temp, 0);
            for (int kb = 0; kb < nb; kb++) {
                MathsUtil.kCombination(3, kb, board, temp, 2);

                int val = Poker.value(temp);
                if (val > hiValue) {
                    hiValue = val;
                }

                val = Poker.afLow8Value(temp);
                if (val > loValue) {
                    loValue = val;
                }
            }
        }
        return new int[] {hiValue, loValue};
    }
}
