package com.leslie.cjpokeroddscalculator.fragment;

import static com.leslie.cjpokeroddscalculator.GlobalStatic.rankStrings;
import static com.leslie.cjpokeroddscalculator.GlobalStatic.suitRankDrawableMap;
import static com.leslie.cjpokeroddscalculator.GlobalStatic.suitStrings;
import static com.leslie.cjpokeroddscalculator.GlobalStatic.writeToDataStore;

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

    public Thread monte_carlo_thread = null;
    public Thread exact_calc_thread = null;

    public List<ConstraintLayout> playerRowList = new ArrayList<>();
    public List<TextView> equityList = new ArrayList<>();
    public List<TextView> winList = new ArrayList<>();
    public List<TextView> tieList = new ArrayList<>();
    List<MaterialButton> removeRowList = new ArrayList<>();
    List<CardRow> cardRows = new ArrayList<>();

    public List<List<ImageButton>> cardButtonListOfLists = new ArrayList<>();
    HashBiMap<ImageButton, String> inputSuitRankMap;
    
    DisplayMetrics displayMetrics = new DisplayMetrics();
    int cardMaxHeight;
    int cardMaxWidth;

    int cardsPerHand;

    public String fragmentName;

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

        writeToDataStore(((MainActivity) requireActivity()).dataStore, PreferencesKeys.stringKey("start_fragment"), fragmentName);

        setSelectedCard(1, 0);

        equityCalculatorBinding.playersremaining.setText(getString(R.string.players_remaining, playerRowList.size()));

        equityCalculatorBinding.addplayer.setOnClickListener(v -> {
            if(playerRowList.size() < 10){
                addPlayerRow();

                equityCalculatorBinding.playersremaining.setText(getString(R.string.players_remaining, playerRowList.size()));

                calculate_odds();
            }
            else{
                Toast.makeText(requireActivity(), "Max number of players is 10", Toast.LENGTH_SHORT).show();
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

            equityCalculatorBinding.scrollView.post(() -> equityCalculatorBinding.scrollView.smoothScrollTo(0, 0));

            calculate_odds();
        });

        equityCalculatorBinding.buttonUnknown.setOnClickListener(v -> set_value_to_selected_card(""));
    }

    public abstract void addPlayerRow();

    public void initialiseCardButtons(List<ImageButton> cardButtons) {
        for (ImageButton card : cardButtons) {
            card.setMaxHeight(cardMaxHeight);
            card.setMaxWidth(cardMaxWidth);
            card.setOnClickListener(selector_listener);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (monte_carlo_thread != null) {
            monte_carlo_thread.interrupt();
        }

        if (exact_calc_thread != null) {
            exact_calc_thread.interrupt();
        }

        equityCalculatorBinding = null;
    }

    public void checkClickToHideCardSelector(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startClickTime = System.currentTimeMillis();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
                Rect outRect = new Rect();
                boolean hideCardSelectorFlag = true;

                equityCalculatorBinding.addplayer.getGlobalVisibleRect(outRect);
                if (outRect.top < (int) event.getRawY()) {
                    hideCardSelectorFlag = false;
                }

                if (hideCardSelectorFlag) {
                    for (List<ImageButton> row : cardButtonListOfLists) {
                        for (ImageButton card : row) {
                            card.getGlobalVisibleRect(outRect);
                            if (outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                                hideCardSelectorFlag = false;
                                break;
                            }
                        }
                    }
                }

                if (hideCardSelectorFlag) {
                    for (MaterialButton b : removeRowList) {
                        b.getGlobalVisibleRect(outRect);
                        if (outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            hideCardSelectorFlag = false;
                            break;
                        }
                    }
                }

                if (hideCardSelectorFlag) {
                    hideCardSelectorFlag = checkAdditionalButtonsToHideCardSelector(event);
                }

                if (hideCardSelectorFlag) {
                    hideCardSelector();
                }
            }
        }
    }

    public boolean checkAdditionalButtonsToHideCardSelector(MotionEvent event) {
        return true;
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
        // getDefaultDisplay is deprecated, when minSdk >= 30, we should fix this
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        cardMaxHeight = (int) (displayMetrics.heightPixels * 0.12);
        cardMaxWidth = (int) (displayMetrics.widthPixels * 0.2);
    }

    public void generateMainLayout() {
        List<ImageButton> cardList = Arrays.asList(
            equityCalculatorBinding.flop1,
            equityCalculatorBinding.flop2,
            equityCalculatorBinding.flop3,
            equityCalculatorBinding.turn,
            equityCalculatorBinding.river
        );

        initialiseCardButtons(cardList);
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
                b.setOnClickListener(input_card_listener);
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

    public final View.OnClickListener removePlayerListener = v -> {
        final MaterialButton remove_input = (MaterialButton) v;
        int playerRemoveNumber = removeRowList.indexOf(remove_input) + 1;

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

        calculate_odds();
    };

    public void removePlayerRow(int playerRemoveNumber) {
        equityCalculatorBinding.playerRows.removeView(playerRowList.get(playerRemoveNumber - 1));

        playerRowList.remove(playerRemoveNumber - 1);

        equityList.remove(playerRemoveNumber - 1);
        winList.remove(playerRemoveNumber - 1);
        tieList.remove(playerRemoveNumber - 1);

        removeRowList.remove(playerRemoveNumber - 1);

        cardButtonListOfLists.remove(playerRemoveNumber);

        cardRows.remove(playerRemoveNumber);

        for (int i = playerRemoveNumber - 1; i < playerRowList.size(); i++) {
            ((TextView) playerRowList.get(i).findViewById(R.id.player_text)).setText(getString(R.string.player, i + 1));
        }
    }

    public void setEmptyHandRow(int row) {
        cardRows.set(row, new SpecificCardsRow(cardsPerHand));
        for (int i = 0; i < cardsPerHand; i++) {
            setCardImage(row, i, "");
        }
    }

    public final View.OnClickListener selector_listener = v -> {
        int row_idx;
        int card_idx = 0;

        for (row_idx = 0; row_idx < cardButtonListOfLists.size(); row_idx++) {
            card_idx = cardButtonListOfLists.get(row_idx).indexOf((ImageButton) v);
            if (card_idx != -1) {
                break;
            }
        }

        setSelectedCard(row_idx, card_idx);
        showCardSelector();
    };

    private final View.OnClickListener input_card_listener = v -> {
        ImageButton card_input = (ImageButton) v;
        card_input.setVisibility(View.INVISIBLE);

        String cardStr = inputSuitRankMap.get(card_input);
        set_value_to_selected_card(cardStr);
    };

    private void set_next_selected_card() {
        if ((selectedRowIdx == 0 && selectedCardIdx < 4) || selectedCardIdx < (cardsPerHand - 1)) {
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
        if(!selectedCardButton.getGlobalVisibleRect(rect) || selectedCardButton.getHeight() != rect.height() ) {
            equityCalculatorBinding.scrollView.post(
                () -> equityCalculatorBinding.scrollView.smoothScrollTo(
                    0,
                    playerRowList.get(selectedRowIdx - 1).getBottom() - equityCalculatorBinding.scrollView.getHeight()
                )
            );
        }
    }

    public void setSelectedCard(int row_idx, int card_idx) {
        if (selectedRowIdx != null && selectedRowIdx < cardButtonListOfLists.size()) {
            cardButtonListOfLists.get(selectedRowIdx).get(selectedCardIdx).setBackgroundResource(0);
        }

        selectedRowIdx = row_idx;
        selectedCardIdx = card_idx;

        cardButtonListOfLists.get(row_idx).get(card_idx).setBackgroundResource(R.drawable.selected_border);
    }

    public void set_card_value(int row_idx, int card_idx, String cardStr) {
        SpecificCardsRow cardRow = (SpecificCardsRow) cardRows.get(row_idx);
        cardRow.cards[card_idx] = cardStr;

        setCardImage(row_idx, card_idx, cardStr);
    }

    public void set_value_to_selected_card(String cardStr) {
        if (selectedRowIdx != null) {
            setInputCardVisible(selectedRowIdx, selectedCardIdx);

            set_card_value(selectedRowIdx, selectedCardIdx, cardStr);
            set_next_selected_card();
            calculate_odds();
        }
    }

    public void setInputCardVisible(int row_idx, int card_idx) {
        String cardStr = ((SpecificCardsRow) cardRows.get(row_idx)).cards[card_idx];

        if (!Objects.equals(cardStr, "")) {
            ImageButton card = inputSuitRankMap.inverse().get(cardStr);
            assert card != null;
            card.setVisibility(View.VISIBLE);
        }
    }

    public void setCardImage(int row_idx, int card_idx, String cardStr) {
        ImageButton card_button = cardButtonListOfLists.get(row_idx).get(card_idx);
        Integer id = suitRankDrawableMap.get(cardStr);
        assert id != null;
        card_button.setImageResource(id);
    }

    public void calculate_odds() {
        if (monte_carlo_thread != null) {
            monte_carlo_thread.interrupt();
        }

        if (exact_calc_thread != null) {
            exact_calc_thread.interrupt();
        }

        clearNumbers();

        equityCalculatorBinding.resDesc.setText(R.string.checking_random_subset);

        monte_carlo_thread = new Thread(null, monteCarloProc);
        exact_calc_thread = new Thread(null, exactCalcProc);

        monte_carlo_thread.start();
        exact_calc_thread.start();
    }

    public void clearNumbers() {
        for(int i = 0; i < equityList.size(); i++) {
            equityList.get(i).setText("");
            winList.get(i).setText("");
            tieList.get(i).setText("");
        }
    }

    public Runnable monteCarloProc;

    public Runnable exactCalcProc;
}