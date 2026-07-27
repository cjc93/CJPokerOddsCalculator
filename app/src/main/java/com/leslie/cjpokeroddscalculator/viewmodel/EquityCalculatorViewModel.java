package com.leslie.cjpokeroddscalculator.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;

import java.util.ArrayList;
import java.util.List;

public abstract class EquityCalculatorViewModel extends ViewModel {
    public MutableLiveData<List<CardRow>> cardRows = new MutableLiveData<>();
    public MutableLiveData<Integer> resDesc = new MutableLiveData<>(R.string.all_combinations_checked_result_is_exact);

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

    public int[] getSelectedCardPosition() {
        List<CardRow> cardRows = this.cardRows.getValue();
        if (cardRows != null) {
            for (int rowIdx = 0; rowIdx < cardRows.size(); rowIdx++) {
                if (cardRows.get(rowIdx) instanceof SpecificCardsRow specificCardsRow) {
                    if (specificCardsRow.selectedCard != null) {
                        return new int[]{rowIdx, specificCardsRow.selectedCard};
                    }
                }
            }
        }

        return null;
    }

    public void setSelectedCardPosition(Integer selectedRowIdx, Integer selectedCardIdx) {
        List<CardRow> cardRows = this.cardRows.getValue();
        assert cardRows != null;

        List<CardRow> newCardRows = new ArrayList<>();
        for (int rowIdx = 0; rowIdx < cardRows.size(); rowIdx++) {
            CardRow newCardRow = cardRows.get(rowIdx).copy();
            if (newCardRow instanceof SpecificCardsRow specificCardsRow) {
                if (selectedRowIdx != null && rowIdx == selectedRowIdx) {
                    specificCardsRow.selectedCard = selectedCardIdx;
                } else {
                    specificCardsRow.selectedCard = null;
                }
            }
            newCardRows.add(newCardRow);
        }

        this.cardRows.setValue(newCardRows);
    }

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
