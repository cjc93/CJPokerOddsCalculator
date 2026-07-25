package com.leslie.cjpokeroddscalculator.fragment;

import static com.leslie.cjpokeroddscalculator.GlobalStatic.navControllerNavigate;
import static com.leslie.cjpokeroddscalculator.GlobalStatic.rankStrings;
import static com.leslie.cjpokeroddscalculator.GlobalStatic.suitRankDrawableMap;
import static com.leslie.cjpokeroddscalculator.GlobalStatic.suitStrings;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowMetrics;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.common.collect.HashBiMap;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.MainActivity;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.databinding.FragmentEquityCalculatorBinding;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public abstract class EquityCalculatorFragment extends Fragment {

    public FragmentEquityCalculatorBinding equityCalculatorBinding;
    public EquityCalculatorViewModel viewModel;
    public long startClickTime;

    public List<ConstraintLayout> playerRowList = new ArrayList<>();
    List<MaterialButton> removeRowList = new ArrayList<>();

    public List<List<ImageButton>> cardButtonListOfLists = new ArrayList<>();
    HashBiMap<ImageButton, String> inputSuitRankMap;
    
    DisplayMetrics displayMetrics = new DisplayMetrics();
    int boardCardMaxHeight;
    int boardCardMaxWidth;

    public String fragmentName;
    public int fragmentId;
    public int homeButtonActionId;

    public List<List<TextView>> statsMatrix = new ArrayList<>();

    List<MaterialButton> statsButtonList = new ArrayList<>();
    List<ConstraintLayout> statsViewList = new ArrayList<>();

    int maxPlayers;

    int titleTextId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        equityCalculatorBinding = FragmentEquityCalculatorBinding.inflate(inflater, container, false);
        return equityCalculatorBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(getViewModelClass());

        initialiseVariables();

        generateMainLayout();

        observeLiveData();

        setButtonListeners();

        ((MainActivity) requireActivity()).dataStore.writeToDataStore(PreferencesKeys.stringKey("start_fragment"), fragmentName);
    }

    protected abstract Class<? extends EquityCalculatorViewModel> getViewModelClass();

    public void observeLiveData() {
        viewModel.cardRows.observe(getViewLifecycleOwner(), cardRows -> {
            for (ImageButton inputButton : inputSuitRankMap.keySet()) {
                inputButton.setVisibility(View.VISIBLE);
            }

            SpecificCardsRow boardCards = (SpecificCardsRow) cardRows.get(0);
            setViewsFromSpecificCardRow(0, boardCards);

            equityCalculatorBinding.playersremaining.setText(getString(R.string.players_remaining, cardRows.size() - 1));
            int numOfPlayersInUI = equityCalculatorBinding.playerRows.getChildCount();
            if (numOfPlayersInUI > cardRows.size() - 1) {
                removeAllPlayerRows();
                for (int rowIdx = 1; rowIdx < cardRows.size(); rowIdx++) {
                    addPlayerRow();
                }
            } else {
                for (int rowIdx = 0; rowIdx < cardRows.size() - 1 - numOfPlayersInUI; rowIdx++) {
                    addPlayerRow();
                }
            }

            for (int rowIdx = 1; rowIdx < cardRows.size(); rowIdx++) {
                setViewsFromCardRow(rowIdx, cardRows.get(rowIdx));
            }

            int[] selectedCard = viewModel.selectedCard.getValue();
            if (selectedCard != null && selectedCard[0] < cardButtonListOfLists.size()) {
                cardButtonListOfLists.get(selectedCard[0]).get(selectedCard[1]).setBackgroundResource(R.drawable.selected_border);
            }
        });

        viewModel.selectedCard.observe(getViewLifecycleOwner(), selectedCard -> {
            for (int rowIdx = 0; rowIdx < cardButtonListOfLists.size(); rowIdx++) {
                List<ImageButton> row = cardButtonListOfLists.get(rowIdx);
                for (int cardIdx = 0; cardIdx < row.size(); cardIdx++) {
                    if (selectedCard != null && rowIdx == selectedCard[0] && cardIdx == selectedCard[1]) {
                        row.get(cardIdx).setBackgroundResource(R.drawable.selected_border);
                    } else {
                        row.get(cardIdx).setBackgroundResource(0);
                    }
                }
            }

            if (selectedCard == null) {
                equityCalculatorBinding.inputCards.setVisibility(View.GONE);
                equityCalculatorBinding.buttonUnknown.setVisibility(View.GONE);
            } else {
                equityCalculatorBinding.inputCards.setVisibility(View.VISIBLE);
                equityCalculatorBinding.buttonUnknown.setVisibility(View.VISIBLE);
            }
        });

        viewModel.resDesc.observe(getViewLifecycleOwner(), stringId -> equityCalculatorBinding.resDesc.setText(stringId));
    }

    private void setButtonListeners () {
        equityCalculatorBinding.homeButton.setOnClickListener(v -> navControllerNavigate(this, fragmentId, homeButtonActionId));

        equityCalculatorBinding.addplayer.setOnClickListener(v -> {
            List<CardRow> cardRows = viewModel.cardRows.getValue();
            assert cardRows != null;
            if(cardRows.size() - 1 < this.maxPlayers){
                cardRows.add(new SpecificCardsRow(null, false, viewModel.cardsPerHand));
                viewModel.cardRows.setValue(cardRows);

                calculateOdds();
            }
            else{
                Toast.makeText(requireActivity(), "Max number of players is " + this.maxPlayers, Toast.LENGTH_SHORT).show();
            }
        });

        equityCalculatorBinding.clear.setOnClickListener(v -> {
            List<CardRow> cardRows = viewModel.cardRows.getValue();
            for (int rowIdx = 0; rowIdx < Objects.requireNonNull(cardRows).size(); rowIdx++) {
                cardRows.get(rowIdx).clear();
            }
            viewModel.cardRows.setValue(cardRows);

            if (viewModel.selectedCard.getValue() != null) {
                if (cardRows.size() > 1 && cardRows.get(1) instanceof SpecificCardsRow) {
                    viewModel.selectedCard.setValue(new int[]{1, 0});
                } else {
                    viewModel.selectedCard.setValue(new int[]{0, 0});
                }
            }

            equityCalculatorBinding.scrollView.post(
                () -> {
                    if (equityCalculatorBinding != null) {
                        equityCalculatorBinding.scrollView.smoothScrollTo(0, 0);
                    }
                }
            );

            calculateOdds();
        });

        equityCalculatorBinding.buttonUnknown.setOnClickListener(v -> setValueToSelectedCard(""));
    }

    public abstract void addPlayerRow();

    public void initialiseCardButtons(List<ImageButton> cardButtons, int cardMaxWidth) {
        for (ImageButton card : cardButtons) {
            card.setMaxHeight(boardCardMaxHeight);
            card.setMaxWidth(cardMaxWidth);

            card.setOnClickListener(v -> {
                int rowIdx;
                int cardIdx = 0;

                for (rowIdx = 0; rowIdx < cardButtonListOfLists.size(); rowIdx++) {
                    cardIdx = cardButtonListOfLists.get(rowIdx).indexOf((ImageButton) v);
                    if (cardIdx != -1) {
                        break;
                    }
                }

                viewModel.selectedCard.setValue(new int[]{rowIdx, cardIdx});
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        equityCalculatorBinding = null;
    }

    public void checkClickToHideCardSelector(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startClickTime = System.currentTimeMillis();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
                Rect outRect = new Rect();

                int event_x = (int) event.getRawX();
                int event_y = (int) event.getRawY();

                boolean hideCardSelectorFlag = true;

                equityCalculatorBinding.addplayer.getGlobalVisibleRect(outRect);
                if (outRect.top < event_y) {
                    hideCardSelectorFlag = false;
                }

                if (hideCardSelectorFlag) {
                    for (List<ImageButton> row : cardButtonListOfLists) {
                        for (ImageButton card : row) {
                            card.getGlobalVisibleRect(outRect);
                            if (outRect.contains(event_x, event_y)) {
                                hideCardSelectorFlag = false;
                                break;
                            }
                        }
                    }
                }

                if (hideCardSelectorFlag) {
                    for (MaterialButton b : removeRowList) {
                        b.getGlobalVisibleRect(outRect);
                        if (outRect.contains(event_x, event_y)) {
                            hideCardSelectorFlag = false;
                            break;
                        }
                    }
                }

                if (hideCardSelectorFlag) {
                    for (MaterialButton b : statsButtonList) {
                        b.getGlobalVisibleRect(outRect);
                        if (outRect.contains(event_x, event_y)) {
                            hideCardSelectorFlag = false;
                            break;
                        }
                    }
                }

                if (hideCardSelectorFlag) {
                    viewModel.selectedCard.setValue(null);
                }
            }
        }
    }

    public void initialiseVariables() {
        WindowMetrics windowMetrics = requireActivity().getWindowManager().getCurrentWindowMetrics();
        Rect bounds = windowMetrics.getBounds();
        displayMetrics.widthPixels = bounds.width();
        displayMetrics.heightPixels = bounds.height();
        boardCardMaxHeight = (int) (displayMetrics.heightPixels * 0.12);
        boardCardMaxWidth = (int) (displayMetrics.widthPixels * 0.2);
    }

    public void generateMainLayout() {
        equityCalculatorBinding.title.setText(this.titleTextId);

        List<ImageButton> cardList = Arrays.asList(
            equityCalculatorBinding.flop1,
            equityCalculatorBinding.flop2,
            equityCalculatorBinding.flop3,
            equityCalculatorBinding.turn,
            equityCalculatorBinding.river
        );

        initialiseCardButtons(cardList, boardCardMaxWidth);
        cardButtonListOfLists.add(cardList);

        inputSuitRankMap = HashBiMap.create();
        for (String suit : suitStrings) {
            for (String rank : rankStrings) {
                ImageButton b = new ImageButton(requireActivity());
                b.setId(View.generateViewId());
                b.setBackgroundResource(0);
                Integer id = suitRankDrawableMap.get(rank + suit);
                assert id != null;
                b.setImageResource(id);
                b.setScaleType(ImageButton.ScaleType.FIT_XY);
                b.setPadding(1, 1, 1, 1);

                b.setOnClickListener(v -> {
                    ImageButton cardInput = (ImageButton) v;
                    cardInput.setVisibility(View.INVISIBLE);
                    String cardStr = inputSuitRankMap.get(cardInput);
                    setValueToSelectedCard(cardStr);
                });

                inputSuitRankMap.put(b, rank + suit);

                equityCalculatorBinding.inputCards.addView(b);
            }
        }

        for (int i = 0; i < suitStrings.length; i++) {
            for (int j = 0; j < rankStrings.length; j++) {
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
                );

                if (i == 0) {
                    layoutParams.topToTop = ConstraintSet.PARENT_ID;
                    layoutParams.bottomToTop = Objects.requireNonNull(this.inputSuitRankMap.inverse().get(rankStrings[j] + suitStrings[1])).getId();
                } else if (i == 3) {
                    layoutParams.topToBottom = Objects.requireNonNull(this.inputSuitRankMap.inverse().get(rankStrings[j] + suitStrings[2])).getId();
                    layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;
                } else {
                    layoutParams.topToBottom = Objects.requireNonNull(this.inputSuitRankMap.inverse().get(rankStrings[j] + suitStrings[i - 1])).getId();
                    layoutParams.bottomToTop = Objects.requireNonNull(this.inputSuitRankMap.inverse().get(rankStrings[j] + suitStrings[i + 1])).getId();
                }

                if (j == 0) {
                    layoutParams.leftToLeft = ConstraintSet.PARENT_ID;
                    layoutParams.rightToLeft = Objects.requireNonNull(this.inputSuitRankMap.inverse().get(rankStrings[1] + suitStrings[i])).getId();
                } else if (j == 12) {
                    layoutParams.leftToRight = Objects.requireNonNull(this.inputSuitRankMap.inverse().get(rankStrings[11] + suitStrings[i])).getId();
                    layoutParams.rightToRight = ConstraintSet.PARENT_ID;
                } else {
                    layoutParams.leftToRight = Objects.requireNonNull(this.inputSuitRankMap.inverse().get(rankStrings[j - 1] + suitStrings[i])).getId();
                    layoutParams.rightToLeft = Objects.requireNonNull(this.inputSuitRankMap.inverse().get(rankStrings[j + 1] + suitStrings[i])).getId();
                }

                ImageButton button = this.inputSuitRankMap.inverse().get(rankStrings[j] + suitStrings[i]);
                assert button != null;
                button.setLayoutParams(layoutParams);
            }
        }
    }

    public void removeAllPlayerRows() {
        statsButtonList.clear();
        statsViewList.clear();
        statsMatrix.clear();

        equityCalculatorBinding.playerRows.removeAllViews();

        playerRowList.clear();
        removeRowList.clear();

        cardButtonListOfLists.subList(1, cardButtonListOfLists.size()).clear();
    }

    public void setViewsFromCardRow(int rowIdx, CardRow cardRow) {
        List<TextView> row = statsMatrix.get(rowIdx - 1);
        for (int statsIdx = 0; statsIdx < row.size(); statsIdx++) {
            String statString = "";
            if (cardRow.stats != null) {
                statString = getString(R.string.two_decimal_perc, cardRow.stats.get(statsIdx) * 100);
            }
            row.get(statsIdx).setText(statString);
        }

        if (cardRow.isStatsVisible) {
            statsViewList.get(rowIdx - 1).setVisibility(View.VISIBLE);
        } else {
            statsViewList.get(rowIdx - 1).setVisibility(View.GONE);
        }
    }

    public void setViewsFromSpecificCardRow(int rowIdx, SpecificCardsRow specificCardRow) {
        for (int cardIdx = 0; cardIdx < specificCardRow.cards.length; cardIdx++) {
            String cardStr = specificCardRow.cards[cardIdx];
            setCardImage(rowIdx, cardIdx, cardStr);
            if (!Objects.equals(cardStr, "")) {
                ImageButton card = inputSuitRankMap.inverse().get(cardStr);
                assert card != null;
                card.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setValueToSelectedCard(String cardStr) {
        int[] selectedCard = viewModel.selectedCard.getValue();
        assert selectedCard != null;
        int selectedRowIdx = selectedCard[0];
        int selectedCardIdx = selectedCard[1];

        int newSelectedRowIdx = -1;
        int newSelectedCardIdx = -1;

        List<CardRow> cardRows = viewModel.cardRows.getValue();
        assert cardRows != null;
        SpecificCardsRow cardRow = (SpecificCardsRow) cardRows.get(selectedRowIdx);

        cardRow.cards[selectedCardIdx] = cardStr;

        viewModel.cardRows.setValue(cardRows);

        if ((selectedRowIdx == 0 && selectedCardIdx < 4) || (selectedRowIdx > 0 && selectedCardIdx < (viewModel.cardsPerHand - 1))) {
            newSelectedRowIdx = selectedRowIdx;
            newSelectedCardIdx = selectedCardIdx + 1;
        } else if ((selectedRowIdx == 1 || selectedRowIdx == playerRowList.size()) && selectedCardIdx == (viewModel.cardsPerHand - 1)) {
            newSelectedRowIdx = 0;
            newSelectedCardIdx = 0;
        } else {
            boolean foundNext = false;
            for (int rowIdx = selectedRowIdx + 1; rowIdx < cardRows.size(); rowIdx++) {
                if (cardRows.get(rowIdx) instanceof SpecificCardsRow) {
                    newSelectedRowIdx = rowIdx;
                    newSelectedCardIdx = 0;
                    foundNext = true;
                    break;
                }
            }

            if (!foundNext) {
                newSelectedRowIdx = 0;
                newSelectedCardIdx = 0;
            }
        }

        viewModel.selectedCard.setValue(new int[]{newSelectedRowIdx, newSelectedCardIdx});

        Rect rect = new Rect();
        ImageButton selectedCardButton = cardButtonListOfLists.get(newSelectedRowIdx).get(newSelectedCardIdx);
        if(newSelectedRowIdx > 0 && (!selectedCardButton.getGlobalVisibleRect(rect) || selectedCardButton.getHeight() != rect.height())) {
            int finalNewSelectedRowIdx = newSelectedRowIdx;
            equityCalculatorBinding.scrollView.post(
                () -> equityCalculatorBinding.scrollView.smoothScrollTo(
                    0,
                    playerRowList.get(finalNewSelectedRowIdx - 1).getBottom() - equityCalculatorBinding.scrollView.getHeight()
                )
            );
        }

        calculateOdds();
    }

    public void setCardImage(int rowIdx, int cardIdx, String cardStr) {
        ImageButton cardButton = cardButtonListOfLists.get(rowIdx).get(cardIdx);
        Integer id = suitRankDrawableMap.get(cardStr);
        assert id != null;
        cardButton.setImageResource(id);
    }

    public void calculateOdds() {
        List<CardRow> cardRows = viewModel.cardRows.getValue();
        assert cardRows != null;
        for (int rowIdx = 0; rowIdx < cardRows.size(); rowIdx++) {
            cardRows.get(rowIdx).stats = null;
        }
        viewModel.cardRows.setValue(cardRows);

        viewModel.resDesc.setValue(R.string.checking_random_subset);
        viewModel.calculateOdds();
    }

    public void addToStatsMatrix(
        TextView equity, TextView win, TextView tie, TextView highCard, TextView onePair, TextView twoPair, TextView threeOfAKind,
        TextView straight, TextView flush, TextView fullHouse, TextView fourOfAKind, TextView straightFlush
    ) {
        this.statsMatrix.add(
            Arrays.asList(
                equity,
                win,
                tie,
                highCard,
                onePair,
                twoPair,
                threeOfAKind,
                straight,
                flush,
                fullHouse,
                fourOfAKind,
                straightFlush
            )
        );
    }

    public void setRowViews(ConstraintLayout playerRow, TextView playerText, List<ImageButton> cardList, int cardMaxWidth, MaterialButton remove, MaterialButton statsButton, ConstraintLayout statsView) {
        playerRowList.add(playerRow);
        playerText.setText(getString(R.string.player, playerRowList.size()));

        initialiseCardButtons(cardList, cardMaxWidth);
        cardButtonListOfLists.add(cardList);

        removeRowList.add(remove);
        remove.setOnClickListener(v -> {
            final MaterialButton removeInput = (MaterialButton) v;
            int playerRemoveNumber = removeRowList.indexOf(removeInput) + 1;

            List<CardRow> cardRows = viewModel.cardRows.getValue();
            assert cardRows != null;
            cardRows.remove(playerRemoveNumber);
            viewModel.cardRows.setValue(cardRows);

            int[] selectedCard = viewModel.selectedCard.getValue();
            if (selectedCard != null && selectedCard[0] >= playerRemoveNumber) {
                for (int rowIdx = selectedCard[0]; rowIdx >= 0; rowIdx--) {
                    if (rowIdx == 0) {
                        viewModel.selectedCard.setValue(new int[]{0, 0});
                        break;
                    } else if (rowIdx < cardRows.size() && cardRows.get(rowIdx) instanceof SpecificCardsRow) {
                        viewModel.selectedCard.setValue(new int[]{rowIdx, selectedCard[1]});
                        break;
                    }
                }
            }

            calculateOdds();
        });

        statsButtonList.add(statsButton);
        statsViewList.add(statsView);

        statsButton.setOnClickListener(v -> {
            final MaterialButton statsButtonInput = (MaterialButton) v;
            int rowIdx = statsButtonList.indexOf(statsButtonInput);
            List<CardRow> cardRows = viewModel.cardRows.getValue();
            assert cardRows != null;
            cardRows.get(rowIdx + 1).isStatsVisible = !cardRows.get(rowIdx + 1).isStatsVisible;
            viewModel.cardRows.setValue(cardRows);
        });
    }
}