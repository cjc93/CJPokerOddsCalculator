package com.leslie.cjpokeroddscalculator;

public abstract class OutputResult {
    public Thread currentThread;
    public TexasHoldemFragment texasHoldemFragment;

    public OutputResult (TexasHoldemFragment texasHoldemFragment) {
        this.texasHoldemFragment = texasHoldemFragment;
        this.currentThread = Thread.currentThread();
    }

    public abstract boolean during_simulations(double[]... result);

    public abstract void after_all_simulations(double[] equity, double[] win, boolean... isCancelled);

    public void update_win_results(double[] equity, double[] win) {
        for(int i = 0; i < texasHoldemFragment.players_remaining_no; i++) {
            texasHoldemFragment.equityArray[i].setText(texasHoldemFragment.getString(R.string.two_decimal_perc, equity[i] * 100));
            texasHoldemFragment.winArray[i].setText(texasHoldemFragment.getString(R.string.two_decimal_perc, win[i] * 100));
            texasHoldemFragment.tieArray[i].setText(texasHoldemFragment.getString(R.string.two_decimal_perc, Math.abs(equity[i] - win[i]) * 100));
        }
    }
}
