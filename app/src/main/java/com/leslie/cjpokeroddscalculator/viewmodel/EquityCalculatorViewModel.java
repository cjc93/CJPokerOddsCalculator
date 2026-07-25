package com.leslie.cjpokeroddscalculator.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;

import java.util.List;

public abstract class EquityCalculatorViewModel extends ViewModel {
    public MutableLiveData<List<CardRow>> cardRows = new MutableLiveData<>();
    public MutableLiveData<Integer> resDesc = new MutableLiveData<>(R.string.all_combinations_checked_result_is_exact);
    public MutableLiveData<int[]> selectedCard = new MutableLiveData<>(new int[]{1, 0});

    public Thread monteCarloThread = null;
    public Thread exactCalcThread = null;

    public int cardsPerHand;

    public void calculateOdds() {
        if (monteCarloThread != null) {
            monteCarloThread.interrupt();
        }

        if (exactCalcThread != null) {
            exactCalcThread.interrupt();
        }

        monteCarloThread = createMonteCarloThread();
        exactCalcThread = createExactCalcThread();

        monteCarloThread.start();
        exactCalcThread.start();
    }

    public abstract Thread createMonteCarloThread();
    public abstract Thread createExactCalcThread();

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
