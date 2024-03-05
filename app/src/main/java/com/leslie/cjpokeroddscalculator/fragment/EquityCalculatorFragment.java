package com.leslie.cjpokeroddscalculator.fragment;

import static com.leslie.cjpokeroddscalculator.GlobalStatic.rankStrings;
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
import android.widget.Button;
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
import com.leslie.cjpokeroddscalculator.GlobalStatic;
import com.leslie.cjpokeroddscalculator.MainActivity;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.databinding.FragmentEquityCalculatorBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public abstract class EquityCalculatorFragment extends Fragment {

    public FragmentEquityCalculatorBinding equityCalculatorBinding;
    public long startClickTime;

    public ImageButton selected_card_button = null;
    public final int[] selected_card_position = new int[2];

    public Thread monte_carlo_thread = null;
    public Thread exact_calc_thread = null;

    public int playersRemainingNo;

    public final ConstraintLayout[] player_row_array = new ConstraintLayout[10];
    public TextView[] equityArray = new TextView[10];
    public TextView[] winArray = new TextView[10];
    public TextView[] tieArray = new TextView[10];

    public HashBiMap<List<Integer>, ImageButton> cardPositionBiMap = HashBiMap.create();
    Map<MaterialButton, Integer> removeRowMap = new HashMap<>();
    HashBiMap<ImageButton, String> inputSuitRankMap;

    CardRow[] cardRows = new CardRow[11];

    DisplayMetrics displayMetrics = new DisplayMetrics();
    int cardHeight;

    int cardsPerHand;

    public String fragmentName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        equityCalculatorBinding = FragmentEquityCalculatorBinding.inflate(inflater, container, false);
        return equityCalculatorBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialiseVariables();

        for (int i = 1; i <= 10; i++) {
            cardRows[i] = new SpecificCardsRow(cardsPerHand);
        }

        generateMainLayout();

        writeToDataStore(((MainActivity) requireActivity()).dataStore, PreferencesKeys.stringKey("start_fragment"), fragmentName);

        for (ImageButton card : cardPositionBiMap.values()) {
            card.setMaxHeight(cardHeight);
        }

        for (int i = 2; i < 10; i++) {
            player_row_array[i].setVisibility(View.GONE);
        }

        set_selected_card(1, 0);

        equityCalculatorBinding.playersremaining.setText(getString(R.string.players_remaining, playersRemainingNo));

        for (ImageButton b : cardPositionBiMap.values()) {
            b.setOnClickListener(selector_listener);
        }

        equityCalculatorBinding.addplayer.setOnClickListener(v -> {
            if(playersRemainingNo < 10){
                playersRemainingNo++;
                equityCalculatorBinding.playersremaining.setText(getString(R.string.players_remaining, playersRemainingNo));

                setEmptyHandRow(playersRemainingNo);

                player_row_array[playersRemainingNo - 1].setVisibility(View.VISIBLE);
                calculate_odds();
            }
            else{
                Toast.makeText(requireActivity(), "Max number of players is 10", Toast.LENGTH_SHORT).show();
            }
        });

        equityCalculatorBinding.clear.setOnClickListener(v -> {
            for (int i = 0; i < 11; i++) {
                if (cardRows[i] instanceof SpecificCardsRow) {
                    SpecificCardsRow cardRow = (SpecificCardsRow) cardRows[i];
                    for (int j = 0; j < cardRow.cards.length; j++) {
                        setInputCardVisible(i, j);
                    }
                }

                cardRows[i].clear(this, i);
            }

            if (equityCalculatorBinding.inputCards.getVisibility() == View.VISIBLE) {
                if (playersRemainingNo > 0 && cardRows[1] instanceof SpecificCardsRow) {
                    set_selected_card(1, 0);
                } else {
                    set_selected_card(0, 0);
                }
            }

            equityCalculatorBinding.scrollView.post(() -> equityCalculatorBinding.scrollView.smoothScrollTo(0, 0));

            calculate_odds();
        });

        equityCalculatorBinding.buttonUnknown.setOnClickListener(v -> set_value_to_selected_card(""));
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
                    for (ImageButton card : cardPositionBiMap.values()) {
                        card.getGlobalVisibleRect(outRect);
                        if (outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            hideCardSelectorFlag = false;
                            break;
                        }
                    }
                }

                if (hideCardSelectorFlag) {
                    for (Button b : removeRowMap.keySet()) {
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

    public void hideCardSelector() {
        equityCalculatorBinding.inputCards.setVisibility(View.GONE);
        equityCalculatorBinding.buttonUnknown.setVisibility(View.GONE);
        selected_card_button.setBackgroundResource(0);
    }

    public void initialiseVariables() {
        // getDefaultDisplay is deprecated, when minSdk >= 30, we should fix this
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        cardHeight = displayMetrics.heightPixels / 10;

        cardRows[0] = new SpecificCardsRow(5);

        playersRemainingNo = 2;

        cardPositionBiMap.put(Arrays.asList(0, 0), equityCalculatorBinding.flop1);
        cardPositionBiMap.put(Arrays.asList(0, 1), equityCalculatorBinding.flop2);
        cardPositionBiMap.put(Arrays.asList(0, 2), equityCalculatorBinding.flop3);
        cardPositionBiMap.put(Arrays.asList(0, 3), equityCalculatorBinding.turn);
        cardPositionBiMap.put(Arrays.asList(0, 4), equityCalculatorBinding.river);
    }

    public void generateMainLayout() {
        inputSuitRankMap = HashBiMap.create();
        for (String suit : suitStrings) {
            for (String rank : rankStrings) {
                ImageButton b = new ImageButton(requireActivity());
                b.setId(View.generateViewId());
                b.setBackgroundResource(0);
                b.setImageResource(GlobalStatic.suitRankDrawableMap.get(rank + suit));
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
        int player_remove_number = removeRowMap.get(remove_input);

        playersRemainingNo--;
        equityCalculatorBinding.playersremaining.setText(getString(R.string.players_remaining, playersRemainingNo));

        if (cardRows[player_remove_number] instanceof SpecificCardsRow) {
            for (int i = 0; i < cardsPerHand; i++) {
                setInputCardVisible(player_remove_number, i);
            }
        }

        for (int i = player_remove_number; i <= playersRemainingNo; i++) {
            cardRows[i] = cardRows[i + 1].copy();
            cardRows[i].copyImageBelow(this, i);
        }

        player_row_array[playersRemainingNo].setVisibility(View.GONE);

        if ((selected_card_position[0] > player_remove_number || selected_card_position[0] > playersRemainingNo) && equityCalculatorBinding.inputCards.getVisibility() == View.VISIBLE) {
            for (int i = selected_card_position[0] - 1; i >= 0; i--) {
                if (cardRows[i] instanceof SpecificCardsRow) {
                    set_selected_card(i, selected_card_position[1]);
                    break;
                }
            }
        }

        calculate_odds();
    };

    public void setEmptyHandRow(int row) {
        cardRows[row] = new SpecificCardsRow(cardsPerHand);
        for (int i = 0; i < cardsPerHand; i++) {
            setCardImage(row, i, "");
        }
    }

    private final View.OnClickListener selector_listener = v -> {
        List<Integer> position = cardPositionBiMap.inverse().get((ImageButton) v);
        assert position != null;
        set_selected_card(position.get(0), position.get(1));
        equityCalculatorBinding.inputCards.setVisibility(View.VISIBLE);
        equityCalculatorBinding.buttonUnknown.setVisibility(View.VISIBLE);
    };

    private final View.OnClickListener input_card_listener = v -> {
        ImageButton card_input = (ImageButton) v;
        card_input.setVisibility(View.INVISIBLE);

        String cardStr = inputSuitRankMap.get(card_input);
        set_value_to_selected_card(cardStr);
    };

    private void set_next_selected_card() {
        if ((selected_card_position[0] == 0 && selected_card_position[1] < 4) || selected_card_position[1] < (cardsPerHand - 1)) {
            set_selected_card(selected_card_position[0], selected_card_position[1] + 1);
        } else if ((selected_card_position[0] == 1 || selected_card_position[0] == playersRemainingNo) && selected_card_position[1] == (cardsPerHand - 1)) {
            set_selected_card(0, 0);
        } else {
            boolean foundNext = false;
            for (int i = selected_card_position[0] + 1; i < playersRemainingNo + 1; i++) {
                if (cardRows[i] instanceof SpecificCardsRow) {
                    set_selected_card(i, 0);
                    foundNext = true;
                    break;
                }
            }

            if (!foundNext) {
                set_selected_card(0, 0);
            }
        }

        Rect rect = new Rect();
        if(!selected_card_button.getGlobalVisibleRect(rect) || selected_card_button.getHeight() != rect.height() ) {
            equityCalculatorBinding.scrollView.post(
                () -> equityCalculatorBinding.scrollView.smoothScrollTo(
                    0,
                    player_row_array[selected_card_position[0] - 1].getBottom() - equityCalculatorBinding.scrollView.getHeight()
                )
            );
        }
    }

    private void set_selected_card(int row_idx, int card_idx) {
        if (selected_card_button != null) {
            selected_card_button.setBackgroundResource(0);
        }

        selected_card_position[0] = row_idx;
        selected_card_position[1] = card_idx;

        selected_card_button = cardPositionBiMap.get(Arrays.asList(row_idx, card_idx));
        assert selected_card_button != null;
        selected_card_button.setBackgroundResource(R.drawable.border_selector);
    }

    public void set_card_value(int row_idx, int card_idx, String cardStr) {
        SpecificCardsRow cardRow = (SpecificCardsRow) cardRows[row_idx];
        cardRow.cards[card_idx] = cardStr;

        setCardImage(row_idx, card_idx, cardStr);
    }

    public void set_value_to_selected_card(String cardStr) {
        setInputCardVisible(selected_card_position[0], selected_card_position[1]);

        set_card_value(selected_card_position[0], selected_card_position[1], cardStr);
        set_next_selected_card();
        calculate_odds();
    }

    public void setInputCardVisible(int row_idx, int card_idx) {
        String cardStr = ((SpecificCardsRow) cardRows[row_idx]).cards[card_idx];

        if (!Objects.equals(cardStr, "")) {
            ImageButton card = inputSuitRankMap.inverse().get(cardStr);
            assert card != null;
            card.setVisibility(View.VISIBLE);
        }
    }

    public void setCardImage(int row_idx, int card_idx, String cardStr) {
        ImageButton card_button = cardPositionBiMap.get(Arrays.asList(row_idx, card_idx));
        assert card_button != null;
        card_button.setImageResource(GlobalStatic.suitRankDrawableMap.get(cardStr));
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
        for(int i = 0; i < playersRemainingNo; i++) {
            equityArray[i].setText("");
            winArray[i].setText("");
            tieArray[i].setText("");
        }
    }

    public Runnable monteCarloProc;

    public Runnable exactCalcProc;
}