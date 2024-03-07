package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.fragment.TexasHoldemFragment;

public abstract class TexasHoldemOutputResult extends OutputResult {
    public Thread currentThread;
    public TexasHoldemFragment texasHoldemFragment;

    public TexasHoldemOutputResult(EquityCalculatorFragment equityCalculatorFragment) {
        super(equityCalculatorFragment);
        this.currentThread = Thread.currentThread();
        this.texasHoldemFragment = (TexasHoldemFragment) equityCalculatorFragment;
    }

    public abstract boolean duringSimulations(double[]... result);

    public abstract void afterAllSimulations(double[] equity, double[] win, double[] highCard, double[] onePair, double[] twoPair, double[] threeOfAKind, double[] straight, double[] flush, double[] fullHouse, double[] fourOfAKind, double[] straightFlush, boolean... isCancelled);

    public void updateWinResults(double[] equity, double[] win, double[] highCard, double[] onePair, double[] twoPair, double[] threeOfAKind, double[] straight, double[] flush, double[] fullHouse, double[] fourOfAKind, double[] straightFlush) {
        if (equityCalculatorFragment.getActivity() != null) {
            for(int i = 0; i < equityCalculatorFragment.playersRemainingNo; i++) {
                equityCalculatorFragment.equityList.get(i).setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, equity[i] * 100));
                equityCalculatorFragment.winList.get(i).setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, win[i] * 100));
                equityCalculatorFragment.tieList.get(i).setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, Math.abs(equity[i] - win[i]) * 100));

                texasHoldemFragment.handStats[i][0].setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, highCard[i] * 100));
                texasHoldemFragment.handStats[i][1].setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, onePair[i] * 100));
                texasHoldemFragment.handStats[i][2].setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, twoPair[i] * 100));
                texasHoldemFragment.handStats[i][3].setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, threeOfAKind[i] * 100));
                texasHoldemFragment.handStats[i][4].setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, straight[i] * 100));
                texasHoldemFragment.handStats[i][5].setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, flush[i] * 100));
                texasHoldemFragment.handStats[i][6].setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, fullHouse[i] * 100));
                texasHoldemFragment.handStats[i][7].setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, fourOfAKind[i] * 100));
                texasHoldemFragment.handStats[i][8].setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, straightFlush[i] * 100));
            }
        }
    }
}
