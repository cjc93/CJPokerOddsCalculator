package com.leslie.cjpokeroddscalculator;

public abstract class OutputResult {
    public Thread currentThread;
    public EquityCalculatorFragment equityCalculatorFragment;

    public OutputResult (EquityCalculatorFragment equityCalculatorFragment) {
        this.equityCalculatorFragment = equityCalculatorFragment;
        this.currentThread = Thread.currentThread();
    }

    public abstract boolean during_simulations(double[]... result);

    public abstract void after_all_simulations(double[] equity, double[] win, boolean... isCancelled);

    public void updateWinResults(double[] equity, double[] win) {
        if (equityCalculatorFragment.getActivity() != null) {
            for(int i = 0; i < equityCalculatorFragment.playersRemainingNo; i++) {
                equityCalculatorFragment.equityArray[i].setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, equity[i] * 100));
                equityCalculatorFragment.winArray[i].setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, win[i] * 100));
                equityCalculatorFragment.tieArray[i].setText(equityCalculatorFragment.getString(R.string.two_decimal_perc, Math.abs(equity[i] - win[i]) * 100));
            }
        }
    }

    public void updateResDesc(int stringId) {
        if (equityCalculatorFragment.equityCalculatorBinding != null) {
            equityCalculatorFragment.equityCalculatorBinding.resDesc.setText(stringId);
        }
    }
}
