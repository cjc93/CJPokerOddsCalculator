package com.leslie.cjpokeroddscalculator.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;

public abstract class EquityCalculatorViewModel extends ViewModel {
    public MutableLiveData<Integer> resDesc = new MutableLiveData<>(R.string.all_combinations_checked_result_is_exact);
    public MutableLiveData<int[]> selectedCard = new MutableLiveData<>(new int[]{1, 0});
    public MutableLiveData<double[][]> stats = new MutableLiveData<>();

    public Thread monteCarloThread = null;
    public Thread exactCalcThread = null;

    public void calculateOdds(EquityCalculatorFragment fragment) {
        if (monteCarloThread != null) {
            monteCarloThread.interrupt();
        }

        if (exactCalcThread != null) {
            exactCalcThread.interrupt();
        }

        monteCarloThread = createMonteCarloThread(fragment);
        exactCalcThread = createExactCalcThread(fragment);

        monteCarloThread.start();
        exactCalcThread.start();
    }

    public abstract Thread createMonteCarloThread(EquityCalculatorFragment fragment);
    public abstract Thread createExactCalcThread(EquityCalculatorFragment fragment);

    @Override
    protected void onCleared() {
        super.onCleared();
        if (monteCarloThread != null) {
            monteCarloThread.interrupt();
        }
        if (exactCalcThread != null) {
            exactCalcThread.interrupt();
        }
    }
}
