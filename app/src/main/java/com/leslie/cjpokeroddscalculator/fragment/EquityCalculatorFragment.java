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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.common.collect.HashBiMap;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.MainActivity;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.databinding.FragmentEquityCalculatorBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public abstract class EquityCalculatorFragment extends Fragment {

    public FragmentEquityCalculatorBinding equityCalculatorBinding;
    public long startClickTime;

    Integer selectedRowIdx;
    Integer selectedCardIdx;

    public Thread monteCarloThread = null;
    public Thread exactCalcThread = null;

    public List<ConstraintLayout> playerRowList = new ArrayList<>();
    List<MaterialButton> removeRowList = new ArrayList<>();
    List<CardRow> cardRows = new ArrayList<>();

    public List<List<ImageButton>> cardButtonListOfLists = new ArrayList<>();
    HashBiMap<ImageButton, String> inputSuitRankMap;
    
    DisplayMetrics displayMetrics = new DisplayMetrics();
    int boardCardMaxHeight;
    int boardCardMaxWidth;

    int cardsPerHand;

    public String fragmentName;
    public int fragmentId;
    public int homeButtonActionId;

    public List<List<TextView>> statsMatrix = new ArrayList<>();

    List<MaterialButton> statsButtonList = new ArrayList<>();

    int maxPlayers;
    double[] initialStats;

    int titleTextId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        equityCalculatorBinding = FragmentEquityCalculatorBinding.inflate(inflater, container, false);
        return equityCalculatorBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialiseVariables();

        generateMainLayout();

        setButtonListeners();

        ((MainActivity) requireActivity()).dataStore.writeToDataStore(PreferencesKeys.stringKey("start_fragment"), fragmentName);
    }

    private void setButtonListeners () {
        equityCalculatorBinding.homeButton.setOnClickListener(v -> navControllerNavigate(this, fragmentId, homeButtonActionId));

        equityCalculatorBinding.addplayer.setOnClickListener(v -> {
            if(playerRowList.size() < this.maxPlayers){
                addPlayerRow();

                equityCalculatorBinding.playersremaining.setText(getString(R.string.players_remaining, playerRowList.size()));

                calculateOdds();
            }
            else{
                Toast.makeText(requireActivity(), "Max number of players is " + this.maxPlayers, Toast.LENGTH_SHORT).show();
            }
        });

        equityCalculatorBinding.clear.setOnClickListener(v -> {
            for (int i = 0; i < cardRows.size(); i++) {
                if (cardRows.get(i) instanceof SpecificCardsRow) {
                    SpecificCardsRow cardRow = (SpecificCardsRow) cardRows.get(i);
                    for (int j = 0; j < cardRow.cards.length; j++) {
                        setInputCardVisible(i, j);
                    }
                }

                cardRows.get(i).clear(this, i);
            }

            if (equityCalculatorBinding.inputCards.getVisibility() == View.VISIBLE) {
                if (cardRows.size() > 1 && cardRows.get(1) instanceof SpecificCardsRow) {
                    setSelectedCard(1, 0);
                } else {
                    setSelectedCard(0, 0);
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

                setSelectedCard(rowIdx, cardIdx);
                showCardSelector();
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (monteCarloThread != null) {
            monteCarloThread.interrupt();
        }

        if (exactCalcThread != null) {
            exactCalcThread.interrupt();
        }

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
                    hideCardSelector();
                }
            }
        }
    }

    public void showCardSelector() {
        equityCalculatorBinding.inputCards.setVisibility(View.VISIBLE);
        equityCalculatorBinding.buttonUnknown.setVisibility(View.VISIBLE);
    }

    public void hideCardSelector() {
        equityCalculatorBinding.inputCards.setVisibility(View.GONE);
        equityCalculatorBinding.buttonUnknown.setVisibility(View.GONE);

        if (selectedRowIdx != null && selectedRowIdx < cardButtonListOfLists.size()) {
            cardButtonListOfLists.get(selectedRowIdx).get(selectedCardIdx).setBackgroundResource(0);
        }

        selectedRowIdx = null;
        selectedCardIdx = null;
    }

    public void initialiseVariables() {
        // TODO: getDefaultDisplay is deprecated, when minSdk >= 30, we should fix this
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
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

        cardRows.add(new SpecificCardsRow(5));

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
                    if (selectedRowIdx != null) {
                        cardInput.setVisibility(View.INVISIBLE);
                    }

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

        for (int i = 0; i < 2; i++) {
            addPlayerRow();
        }

        equityCalculatorBinding.playersremaining.setText(getString(R.string.players_remaining, playerRowList.size()));

        setSelectedCard(1, 0);

        for (int playerIdx = 0; playerIdx < 2; playerIdx++) {
            for (int statIdx = 0; statIdx < initialStats.length; statIdx++) {
                statsMatrix.get(playerIdx).get(statIdx).setText(getString(R.string.two_decimal_perc, initialStats[statIdx]));
            }
        }
    }

    public void removePlayerRow(int playerRemoveNumber) {
        statsButtonList.remove(playerRemoveNumber - 1);
        statsMatrix.remove(playerRemoveNumber - 1);

        equityCalculatorBinding.playerRows.removeView(playerRowList.get(playerRemoveNumber - 1));

        playerRowList.remove(playerRemoveNumber - 1);

        removeRowList.remove(playerRemoveNumber - 1);

        cardButtonListOfLists.remove(playerRemoveNumber);

        cardRows.remove(playerRemoveNumber);

        for (int i = playerRemoveNumber - 1; i < playerRowList.size(); i++) {
            ((TextView) playerRowList.get(i).findViewById(R.id.player_text)).setText(getString(R.string.player, i + 1));
        }
    }

    public void setSelectedCard(int rowIdx, int cardIdx) {
        if (selectedRowIdx != null && selectedRowIdx < cardButtonListOfLists.size()) {
            cardButtonListOfLists.get(selectedRowIdx).get(selectedCardIdx).setBackgroundResource(0);
        }

        selectedRowIdx = rowIdx;
        selectedCardIdx = cardIdx;

        cardButtonListOfLists.get(rowIdx).get(cardIdx).setBackgroundResource(R.drawable.selected_border);
    }

    public void setValueToSelectedCard(String cardStr) {
        if (selectedRowIdx != null) {
            setInputCardVisible(selectedRowIdx, selectedCardIdx);

            SpecificCardsRow cardRow = (SpecificCardsRow) cardRows.get(selectedRowIdx);
            cardRow.cards[selectedCardIdx] = cardStr;

            setCardImage(selectedRowIdx, selectedCardIdx, cardStr);

            if ((selectedRowIdx == 0 && selectedCardIdx < 4) || (selectedRowIdx > 0 && selectedCardIdx < (cardsPerHand - 1))) {
                setSelectedCard(selectedRowIdx, selectedCardIdx + 1);
            } else if ((selectedRowIdx == 1 || selectedRowIdx == playerRowList.size()) && selectedCardIdx == (cardsPerHand - 1)) {
                setSelectedCard(0, 0);
            } else {
                boolean foundNext = false;
                for (int i = selectedRowIdx + 1; i < cardRows.size(); i++) {
                    if (cardRows.get(i) instanceof SpecificCardsRow) {
                        setSelectedCard(i, 0);
                        foundNext = true;
                        break;
                    }
                }

                if (!foundNext) {
                    setSelectedCard(0, 0);
                }
            }

            Rect rect = new Rect();
            ImageButton selectedCardButton = cardButtonListOfLists.get(selectedRowIdx).get(selectedCardIdx);
            if(selectedRowIdx > 0 && (!selectedCardButton.getGlobalVisibleRect(rect) || selectedCardButton.getHeight() != rect.height())) {
                equityCalculatorBinding.scrollView.post(
                    () -> {
                        if (selectedRowIdx != null) {
                            equityCalculatorBinding.scrollView.smoothScrollTo(
                                0,
                                playerRowList.get(selectedRowIdx - 1).getBottom() - equityCalculatorBinding.scrollView.getHeight()
                            );
                        }
                    }
                );
            }

            calculateOdds();
        }
    }

    public void setInputCardVisible(int rowIdx, int cardIdx) {
        String cardStr = ((SpecificCardsRow) cardRows.get(rowIdx)).cards[cardIdx];

        if (!Objects.equals(cardStr, "")) {
            ImageButton card = inputSuitRankMap.inverse().get(cardStr);
            assert card != null;
            card.setVisibility(View.VISIBLE);
        }
    }

    public void setCardImage(int rowIdx, int cardIdx, String cardStr) {
        ImageButton cardButton = cardButtonListOfLists.get(rowIdx).get(cardIdx);
        Integer id = suitRankDrawableMap.get(cardStr);
        assert id != null;
        cardButton.setImageResource(id);
    }

    public void calculateOdds() {
        if (monteCarloThread != null) {
            monteCarloThread.interrupt();
        }

        if (exactCalcThread != null) {
            exactCalcThread.interrupt();
        }

        for(int playerIdx = 0; playerIdx < statsMatrix.size(); playerIdx++) {
            for(int statsIdx = 0; statsIdx < statsMatrix.get(playerIdx).size(); statsIdx++) {
                statsMatrix.get(playerIdx).get(statsIdx).setText("");
            }
        }

        equityCalculatorBinding.resDesc.setText(R.string.checking_random_subset);

        monteCarloThread = new Thread(null, this::monteCarloProc);
        exactCalcThread = new Thread(null, this::exactCalcProc);

        monteCarloThread.start();
        exactCalcThread.start();
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

        cardRows.add(new SpecificCardsRow(cardsPerHand));

        removeRowList.add(remove);
        remove.setOnClickListener(v -> {
            final MaterialButton removeInput = (MaterialButton) v;
            int playerRemoveNumber = removeRowList.indexOf(removeInput) + 1;

            if (cardRows.get(playerRemoveNumber) instanceof SpecificCardsRow) {
                for (int i = 0; i < cardsPerHand; i++) {
                    setInputCardVisible(playerRemoveNumber, i);
                }
            }

            removePlayerRow(playerRemoveNumber);

            equityCalculatorBinding.playersremaining.setText(getString(R.string.players_remaining, playerRowList.size()));

            if (selectedRowIdx != null && selectedRowIdx >= playerRemoveNumber) {
                for (int i = selectedRowIdx; i >= 0; i--) {
                    if (i < cardRows.size() && cardRows.get(i) instanceof SpecificCardsRow) {
                        setSelectedCard(i, selectedCardIdx);
                        break;
                    }
                }
            }

            calculateOdds();
        });

        statsButtonList.add(statsButton);
        statsButton.setOnClickListener(v -> {
            if (statsView.getVisibility() == View.VISIBLE) {
                statsView.setVisibility(View.GONE);
            } else {
                statsView.setVisibility(View.VISIBLE);
            }
        });
    }

    public abstract void monteCarloProc();
    public abstract void exactCalcProc();
}