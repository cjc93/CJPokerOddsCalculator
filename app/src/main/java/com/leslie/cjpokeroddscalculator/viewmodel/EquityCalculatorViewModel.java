package com.leslie.cjpokeroddscalculator.viewmodel;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;

import java.util.ArrayList;
import java.util.List;

public abstract class EquityCalculatorViewModel extends ViewModel {
    public MutableLiveData<List<CardRow>> cardRows = new MutableLiveData<>();
    public MutableLiveData<double[][]> stats = new MutableLiveData<>();
    public MutableLiveData<Integer> resDesc = new MutableLiveData<>(R.string.all_combinations_checked_result_is_exact);
    public MediatorLiveData<List<CardRow>> recyclerViewData = new MediatorLiveData<>();

    public Thread monteCarloThread = null;
    public Thread exactCalcThread = null;

    public int cardsPerHand;

    public void init(int cardsPerHand) {
        if (this.cardsPerHand != 0) {
            return;
        }

        recyclerViewData.addSource(cardRows, value -> updateRecyclerViewData());
        recyclerViewData.addSource(stats, value -> updateRecyclerViewData());

        this.cardsPerHand = cardsPerHand;

        List<CardRow> cardRowList = new ArrayList<>();
        cardRowList.add(new SpecificCardsRow(null, 5, null));
        cardRowList.add(new SpecificCardsRow(false, this.cardsPerHand, 0));
        cardRowList.add(new SpecificCardsRow(false, this.cardsPerHand, null));
        cardRows.setValue(cardRowList);

        stats.setValue(new double[][]{getInitialStats(), getInitialStats()});
    }

    public abstract double[] getInitialStats();

    private void updateRecyclerViewData() {
        double[][] statsMatrix = stats.getValue();

        List<CardRow> newCardRows = getCardRowsCopy();
        for (int rowIdx = 1; rowIdx < newCardRows.size(); rowIdx++) {
            if (statsMatrix != null && rowIdx - 1 < statsMatrix.length) {
                newCardRows.get(rowIdx).stats = statsMatrix[rowIdx - 1];
            } else {
                newCardRows.get(rowIdx).stats = null;
            }
        }

        recyclerViewData.setValue(newCardRows);
    }

    public void calculateOdds() {
        killThreads();

        monteCarloThread = createMonteCarloThread();
        exactCalcThread = createExactCalcThread();

        monteCarloThread.start();
        exactCalcThread.start();
    }

    public void killThreads() {
        if (monteCarloThread != null) {
            monteCarloThread.interrupt();
        }

        if (exactCalcThread != null) {
            exactCalcThread.interrupt();
        }
    }

    public abstract Thread createMonteCarloThread();
    public abstract Thread createExactCalcThread();

    public List<CardRow> getCardRowsCopy() {
        List<CardRow> cardRows = this.cardRows.getValue();
        assert cardRows != null;

        List<CardRow> newCardRows = new ArrayList<>();
        for (CardRow cardRow : cardRows) {
            CardRow copy = cardRow.copy();
            newCardRows.add(copy);
        }
        return newCardRows;
    }

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
        List<CardRow> newCardRows = getCardRowsCopy();
        setSelectedCardPositionInCardRows(newCardRows, selectedRowIdx, selectedCardIdx);
        this.cardRows.setValue(newCardRows);
    }

    public void setSelectedCardPositionInCardRows(List<CardRow> cardRows, Integer selectedRowIdx, Integer selectedCardIdx) {
        for (int rowIdx = 0; rowIdx < cardRows.size(); rowIdx++) {
            if (cardRows.get(rowIdx) instanceof SpecificCardsRow specificCardsRow) {
                if (selectedRowIdx != null && rowIdx == selectedRowIdx) {
                    specificCardsRow.selectedCard = selectedCardIdx;
                } else {
                    specificCardsRow.selectedCard = null;
                }
            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        killThreads();
    }
}
