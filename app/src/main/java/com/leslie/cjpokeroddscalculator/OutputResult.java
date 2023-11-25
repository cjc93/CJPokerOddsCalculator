package com.leslie.cjpokeroddscalculator;

import com.leslie.cjpokeroddscalculator.databinding.FragmentTexasHoldemBinding;

public abstract class OutputResult {
    public final FragmentTexasHoldemBinding binding;
    public Thread currentThread;
    public TexasHoldemFragment texasHoldemFragment;

    public OutputResult (TexasHoldemFragment texasHoldemFragment) {
        this.texasHoldemFragment = texasHoldemFragment;
        this.currentThread = Thread.currentThread();
        this.binding = texasHoldemFragment.binding;
    }

    public abstract boolean during_simulations(double[]... result);

    public abstract void after_all_simulations(double[] equity, double[] win, boolean... isCancelled);

    public void updateWinResults(double[] equity, double[] win) {
        if (texasHoldemFragment.getActivity() != null) {
            for(int i = 0; i < texasHoldemFragment.playersRemainingNo; i++) {
                texasHoldemFragment.equityArray[i].setText(texasHoldemFragment.getString(R.string.two_decimal_perc, equity[i] * 100));
                texasHoldemFragment.winArray[i].setText(texasHoldemFragment.getString(R.string.two_decimal_perc, win[i] * 100));
                texasHoldemFragment.tieArray[i].setText(texasHoldemFragment.getString(R.string.two_decimal_perc, Math.abs(equity[i] - win[i]) * 100));
            }
        }
    }

    public void updateResDesc(int stringId) {
        if (binding != null) {
            binding.mainUi.resDesc.setText(stringId);
        }
    }
}
