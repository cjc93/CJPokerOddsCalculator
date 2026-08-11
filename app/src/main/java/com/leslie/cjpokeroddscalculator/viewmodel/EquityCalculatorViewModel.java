package com.leslie.cjpokeroddscalculator.viewmodel;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class EquityCalculatorViewModel extends ViewModel {
    public SavedStateHandle savedStateHandle;

    public MutableLiveData<SpecificCardsRow> boardCardRow;
    public MutableLiveData<List<CardRow>> playerCardRows;
    public MutableLiveData<double[][]> stats;
    public MutableLiveData<int[]> selectedCard;
    public MutableLiveData<Integer> resDesc;

    public MediatorLiveData<List<CardRow>> recyclerViewData = new MediatorLiveData<>();
    public MediatorLiveData<SpecificCardsRow> boardData = new MediatorLiveData<>();
    public MediatorLiveData<List<CardRow>> inputCardsViewData = new MediatorLiveData<>();

    public Thread monteCarloThread = null;
    public Thread exactCalcThread = null;

    public int cardsPerHand;

    public EquityCalculatorViewModel(SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;

        this.boardCardRow = savedStateHandle.getLiveData("boardCardRow");
        this.playerCardRows = savedStateHandle.getLiveData("playerCardRows");
        this.stats = savedStateHandle.getLiveData("stats");
        this.selectedCard = savedStateHandle.getLiveData("selectedCard");
        this.resDesc = savedStateHandle.getLiveData("resDesc", R.string.all_combinations_checked_result_is_exact);
    }

    public void init(int cardsPerHand) {
        if (this.cardsPerHand != 0) {
            return;
        }

        this.cardsPerHand = cardsPerHand;

        recyclerViewData.addSource(playerCardRows, value -> updateRecyclerViewData());
        recyclerViewData.addSource(stats, value -> updateRecyclerViewData());
        recyclerViewData.addSource(selectedCard, value -> updateRecyclerViewData());

        boardData.addSource(boardCardRow, value -> updateBoardData());
        boardData.addSource(selectedCard, value -> updateBoardData());

        inputCardsViewData.addSource(boardCardRow, value -> updateInputCardsViewData());
        inputCardsViewData.addSource(playerCardRows, value -> updateInputCardsViewData());

        if (boardCardRow.getValue() == null) {
            boardCardRow.setValue(new SpecificCardsRow(null, 5));
        }

        if (playerCardRows.getValue() == null) {
            List<CardRow> cardRowList = new ArrayList<>();
            cardRowList.add(new SpecificCardsRow(false, this.cardsPerHand));
            cardRowList.add(new SpecificCardsRow(false, this.cardsPerHand));
            playerCardRows.setValue(cardRowList);
        }

        if (selectedCard.getValue() == null) {
            setSelectedCardPosition(0, 0);
        }

        if (stats.getValue() == null) {
            stats.setValue(new double[][]{getInitialStats(), getInitialStats()});
        }
    }

    public abstract double[] getInitialStats();


    public int[] getSelectedCardPosition() {
        int[] selectedCard = this.selectedCard.getValue();

        if (selectedCard == null) {
            return null;
        }

        if (Objects.requireNonNull(boardCardRow.getValue()).id == selectedCard[0]) {
            return new int[]{-1, selectedCard[1]};
        }

        List<CardRow> cardRows = this.playerCardRows.getValue();
        for (int rowIdx = 0; rowIdx < Objects.requireNonNull(cardRows).size(); rowIdx++) {
            if (cardRows.get(rowIdx).id == selectedCard[0]) {
                return new int[]{rowIdx, selectedCard[1]};
            }
        }

        return null;
    }

    public void setSelectedCardPosition(Integer selectedRowIdx, Integer selectedCardIdx) {
        if (selectedRowIdx == null) {
            selectedCard.setValue(null);
        } else if (selectedRowIdx == -1) {
            selectedCard.setValue(new int[]{Objects.requireNonNull(boardCardRow.getValue()).id, selectedCardIdx});
        } else {
            selectedCard.setValue(new int[]{Objects.requireNonNull(this.playerCardRows.getValue()).get(selectedRowIdx).id, selectedCardIdx});
        }
    }

    private void updateInputCardsViewData() {
        List<CardRow> newCardRows = getPlayerCardRowsCopy();
        SpecificCardsRow newCardRow = Objects.requireNonNull(boardCardRow.getValue()).copy();
        newCardRows.add(newCardRow);
        inputCardsViewData.setValue(newCardRows);
    }

    private void updateBoardData() {
        int[] selectedCardArray = getSelectedCardPosition();
        SpecificCardsRow newCardRow = Objects.requireNonNull(boardCardRow.getValue()).copy();

        if (selectedCardArray != null && selectedCardArray[0] == -1) {
            newCardRow.selectedCard = selectedCardArray[1];
        } else {
            newCardRow.selectedCard = null;
        }

        boardData.setValue(newCardRow);
    }

    private void updateRecyclerViewData() {
        double[][] statsMatrix = stats.getValue();
        int[] selectedCardArray = getSelectedCardPosition();
        List<CardRow> newCardRows = getPlayerCardRowsCopy();

        for (int rowIdx = 0; rowIdx < newCardRows.size(); rowIdx++) {
            CardRow newCardRow = newCardRows.get(rowIdx);

            if (statsMatrix != null && rowIdx < statsMatrix.length) {
                newCardRow.stats = statsMatrix[rowIdx];
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

    public List<CardRow> getPlayerCardRowsCopy() {
        List<CardRow> cardRows = this.playerCardRows.getValue();
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
