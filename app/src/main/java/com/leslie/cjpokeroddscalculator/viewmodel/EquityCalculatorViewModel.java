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
    public MutableLiveData<int[]> selectedCard = new MutableLiveData<>(new int[]{1, 0});
    public MutableLiveData<Integer> resDesc = new MutableLiveData<>(R.string.all_combinations_checked_result_is_exact);
    public MediatorLiveData<List<CardRow>> allCardsData = new MediatorLiveData<>();

    public Thread monteCarloThread = null;
    public Thread exactCalcThread = null;

    public int cardsPerHand;

    public void init(int cardsPerHand) {
        if (this.cardsPerHand != 0) {
            return;
        }

        allCardsData.addSource(cardRows, value -> updateAllCardsData());
        allCardsData.addSource(stats, value -> updateAllCardsData());
        allCardsData.addSource(selectedCard, value -> updateAllCardsData());

        this.cardsPerHand = cardsPerHand;

        List<CardRow> cardRowList = new ArrayList<>();
        cardRowList.add(new SpecificCardsRow(null, 5));
        cardRowList.add(new SpecificCardsRow(false, this.cardsPerHand));
        cardRowList.add(new SpecificCardsRow(false, this.cardsPerHand));
        cardRows.setValue(cardRowList);

        stats.setValue(new double[][]{getInitialStats(), getInitialStats()});
    }

    public abstract double[] getInitialStats();

    private void updateAllCardsData() {
        double[][] statsMatrix = stats.getValue();
        int[] selectedCardArray = selectedCard.getValue();
        List<CardRow> newCardRows = getCardRowsCopy();

        for (int rowIdx = 0; rowIdx < newCardRows.size(); rowIdx++) {
            CardRow newCardRow = newCardRows.get(rowIdx);

            if (statsMatrix != null && rowIdx > 0 && rowIdx - 1 < statsMatrix.length) {
                newCardRow.stats = statsMatrix[rowIdx - 1];
            } else {
                newCardRow.stats = null;
            }

            if (newCardRow instanceof SpecificCardsRow specificCardsRow) {
                if (selectedCardArray != null && rowIdx == selectedCardArray[0]) {
                    specificCardsRow.selectedCard = selectedCardArray[1];
                } else {
                    specificCardsRow.selectedCard = null;
                }
            }
        }

        allCardsData.setValue(newCardRows);
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

    @Override
    protected void onCleared() {
        super.onCleared();
        killThreads();
    }
}
