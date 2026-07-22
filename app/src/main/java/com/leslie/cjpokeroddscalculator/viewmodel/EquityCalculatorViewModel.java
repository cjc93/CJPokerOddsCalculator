package com.leslie.cjpokeroddscalculator.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;

import java.util.ArrayList;
import java.util.List;

public abstract class EquityCalculatorViewModel extends ViewModel {
    public MutableLiveData<Integer> resDesc = new MutableLiveData<>(R.string.all_combinations_checked_result_is_exact);
    public MutableLiveData<int[]> selectedCard = new MutableLiveData<>(new int[]{1, 0});
    public MutableLiveData<double[][]> stats = new MutableLiveData<>();
    public MutableLiveData<List<Boolean>> statsVisibleList = new MutableLiveData<>(new ArrayList<>());

    public Thread monteCarloThread = null;
    public Thread exactCalcThread = null;

    public void calculateOdds(List<CardRow> cardRows, int cardsPerHand) {
        if (monteCarloThread != null) {
            monteCarloThread.interrupt();
        }

        if (exactCalcThread != null) {
            exactCalcThread.interrupt();
        }

        monteCarloThread = createMonteCarloThread(cardRows, cardsPerHand);
        exactCalcThread = createExactCalcThread(cardRows, cardsPerHand);

        monteCarloThread.start();
        exactCalcThread.start();
    }

    public abstract Thread createMonteCarloThread(List<CardRow> cardRows, int cardsPerHand);
    public abstract Thread createExactCalcThread(List<CardRow> cardRows, int cardsPerHand);

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
