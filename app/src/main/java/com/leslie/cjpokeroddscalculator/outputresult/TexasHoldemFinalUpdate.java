package com.leslie.cjpokeroddscalculator.outputresult;

import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.R;

public class TexasHoldemFinalUpdate extends TexasHoldemOutputResult {

    public TexasHoldemFinalUpdate(EquityCalculatorFragment equityCalculatorFragment) {
        super(equityCalculatorFragment);
    }

    @Override
    public boolean duringSimulations(double[]... result) {
        if (this.currentThread.isInterrupted()) {
            return false;
        }

        equityCalculatorFragment.requireActivity().runOnUiThread(() -> updateResDesc(R.string.checking_combinations));

        return true;
    }

    @Override
    public void afterAllSimulations(double[] equity, double[] win, double[] highCard, double[] onePair, double[] twoPair, double[] threeOfAKind, double[] straight, double[] flush, double[] fullHouse, double[] fourOfAKind, double[] straightFlush, boolean... isCancelled) {
        if (!Thread.interrupted()) {
            if (equityCalculatorFragment.monteCarloThread.isAlive()) {
                if (isCancelled[0]) {
                    equityCalculatorFragment.requireActivity().runOnUiThread(() -> updateResDesc(R.string.checking_random_subset));
                } else {
                    equityCalculatorFragment.monteCarloThread.interrupt();
                    equityCalculatorFragment.requireActivity().runOnUiThread(() -> {
                        updateWinResults(equity, win, highCard, onePair, twoPair, threeOfAKind, straight, flush, fullHouse, fourOfAKind, straightFlush);
                        updateResDesc(R.string.all_combinations_checked_result_is_exact);
                    });
                }
            } else {
                if (isCancelled[0]) {
                    equityCalculatorFragment.requireActivity().runOnUiThread(() -> updateResDesc(R.string.finished_checking_random_subset));
                } else {
                    equityCalculatorFragment.requireActivity().runOnUiThread(() -> {
                        updateWinResults(equity, win, highCard, onePair, twoPair, threeOfAKind, straight, flush, fullHouse, fourOfAKind, straightFlush);
                        updateResDesc(R.string.all_combinations_checked_result_is_exact);
                    });
                }
            }
        }
    }

}
