package com.leslie.cjpokeroddscalculator;

public abstract class OutputResult {
    public Thread currentThread;
    public MainActivity mainActivity;

    public OutputResult (MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.currentThread = Thread.currentThread();
    }

    public abstract boolean during_simulations(double[]... result);

    public abstract void after_all_simulations(double[] equity, double[] win, boolean... isCancelled);

    public void update_win_results(double[] equity, double[] win) {
        for(int i = 0; i < mainActivity.players_remaining_no; i++) {
            mainActivity.equityArray[i].setText(mainActivity.getString(R.string.two_decimal_perc, equity[i] * 100));
            mainActivity.winArray[i].setText(mainActivity.getString(R.string.two_decimal_perc, win[i] * 100));
            mainActivity.tieArray[i].setText(mainActivity.getString(R.string.two_decimal_perc, Math.abs(equity[i] - win[i]) * 100));
        }
    }
}
