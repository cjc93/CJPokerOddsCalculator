package com.leslie.cjpokeroddscalculator;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.common.collect.HashBiMap;
import com.leslie.cjpokeroddscalculator.calculation.TexasHoldemExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.TexasHoldemMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.databinding.RangeSelectorBinding;
import com.leslie.cjpokeroddscalculator.databinding.TexasHoldemPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemLiveUpdate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TexasHoldemFragment extends EquityCalculatorFragment {
    RangeSelectorBinding rangeSelectorBinding;
    private ImageButton selectedRangeButton;
    private int selectedRangePosition;

    private MaterialButton selectedMatrixButton = null;
    private final int[] selectedMatrixPosition = new int[2];

    public LinearLayout[] twoCardsLayouts = new LinearLayout[10];

    public HashBiMap<Integer, ImageButton> rangePositionBiMap = HashBiMap.create();

    Map<MaterialButton, Integer> rangeSwitchRowMap = new HashMap<>();

    HashBiMap<MaterialButton, List<Integer>> inputMatrixMap;
    List<List<Set<String>>> matrixInput;
    public Bitmap emptyRangeBitmap;

    Map<ImageButton, List<Integer>> pairButtonSuitsMap = new HashMap<>();
    Map<ImageButton, List<Integer>> suitedButtonSuitsMap = new HashMap<>();
    Map<ImageButton, List<Integer>> offsuitButtonSuitsMap = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rangeSelectorBinding = RangeSelectorBinding.inflate(LayoutInflater.from(requireActivity()), equityCalculatorBinding.fullscreenUi, true);

        initialiseTexasHoldemVariables();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (rangeSelectorBinding.rangeSelector.getVisibility() == View.VISIBLE) {
                    rangeSelectorBinding.rangeSelector.setVisibility(View.GONE);
                    equityCalculatorBinding.mainUi.setVisibility(View.VISIBLE);
                } else {
                    setEnabled(false);
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        generateRangeSelector();

        winArray[0].setText(getString(R.string.two_decimal_perc, 47.97));
        winArray[1].setText(getString(R.string.two_decimal_perc, 47.97));
        tieArray[0].setText(getString(R.string.two_decimal_perc, 2.03));
        tieArray[1].setText(getString(R.string.two_decimal_perc, 2.03));

        equityCalculatorBinding.title.setText(getString(R.string.texas_hold_em_poker));

        for (ImageButton r : rangePositionBiMap.values()) {
            r.setOnClickListener(rangeSelectorListener);
        }

        rangeSelectorBinding.rangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                float finalValue = 0;
                for (java.util.Map.Entry<Integer, List<Integer>> entry : GlobalStatic.bestHandsMap.entrySet()) {
                    List<Integer> matrixPosition = entry.getValue();
                    int cumulativeHands = entry.getKey();
                    if (cumulativeHands <= value) {
                        Objects.requireNonNull(inputMatrixMap.inverse().get(matrixPosition)).setBackgroundColor(Color.YELLOW);
                        finalValue = cumulativeHands;
                    } else {
                        Objects.requireNonNull(inputMatrixMap.inverse().get(matrixPosition)).setBackgroundColor(Color.LTGRAY);
                    }
                }
                slider.setValue(finalValue);
            }
            rangeSelectorBinding.handsPerc.setText(getString(R.string.hands_perc, slider.getValue() / 1326.0 * 100));
        });

        rangeSelectorBinding.rangeSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                clearSuitSelectorUI();
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                float selectedValue = slider.getValue();
                for (java.util.Map.Entry<Integer, List<Integer>> entry : GlobalStatic.bestHandsMap.entrySet()) {
                    List<Integer> matrixPosition = entry.getValue();
                    int row = matrixPosition.get(0);
                    int col = matrixPosition.get(1);

                    Set<String> suits = matrixInput.get(row).get(col);

                    if (entry.getKey() <= selectedValue) {
                        GlobalStatic.addAllSuits(suits, row, col);
                    } else {
                        suits.clear();
                    }
                }
            }
        });

        rangeSelectorBinding.done.setOnClickListener(v -> {
            RangeRow rangeRow = (RangeRow) this.cardRows[selectedRangePosition];

            rangeRow.matrix = GlobalStatic.copyMatrix(this.matrixInput);

            Bitmap matrixBitmap = Bitmap.createBitmap(13, 13, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < 13; i++)  {
                for (int j = 0; j < 13; j++)  {
                    Set<String> suits = rangeRow.matrix.get(i).get(j);
                    if (GlobalStatic.isAllSuits(suits, i, j)) {
                        matrixBitmap.setPixel(j, i, Color.YELLOW);
                    } else if (suits.isEmpty()) {
                        matrixBitmap.setPixel(j, i, Color.LTGRAY);
                    } else {
                        matrixBitmap.setPixel(j, i, Color.CYAN);
                    }
                }
            }

            selectedRangeButton.setImageBitmap(Bitmap.createScaledBitmap(matrixBitmap, cardHeight, cardHeight, false));
            matrixBitmap.recycle();

            rangeSelectorBinding.rangeSelector.setVisibility(View.GONE);
            equityCalculatorBinding.mainUi.setVisibility(View.VISIBLE);

            calculate_odds();
        });

        equityCalculatorBinding.homeButton.setOnClickListener(view1 -> NavHostFragment.findNavController(this).navigate(R.id.action_TexasHoldemFragment_to_HomeFragment));

        monteCarloProc = () -> {
            try {
                TexasHoldemMonteCarloCalc calcObj = new TexasHoldemMonteCarloCalc();
                calcObj.calculate(cardRows, playersRemainingNo, new TexasHoldemLiveUpdate(this));
            } catch (InterruptedException ignored) { }
        };

        exactCalcProc = () -> {
            try {
                TexasHoldemExactCalc calcObj = new TexasHoldemExactCalc();
                calcObj.calculate(cardRows, playersRemainingNo, new TexasHoldemFinalUpdate(this));
            } catch (InterruptedException ignored) { }
        };
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

        rangeSelectorBinding = null;
    }

    public void setCardsPerHand() {
        cardsPerHand = 2;
    }

    public void initialiseTexasHoldemVariables() {
        suitedButtonSuitsMap.put(rangeSelectorBinding.suits01, Arrays.asList(1, 1));
        suitedButtonSuitsMap.put(rangeSelectorBinding.suits02, Arrays.asList(2, 2));
        suitedButtonSuitsMap.put(rangeSelectorBinding.suits11, Arrays.asList(3, 3));
        suitedButtonSuitsMap.put(rangeSelectorBinding.suits12, Arrays.asList(4, 4));

        pairButtonSuitsMap.put(rangeSelectorBinding.suits01, Arrays.asList(1, 2));
        pairButtonSuitsMap.put(rangeSelectorBinding.suits02, Arrays.asList(1, 3));
        pairButtonSuitsMap.put(rangeSelectorBinding.suits11, Arrays.asList(1, 4));
        pairButtonSuitsMap.put(rangeSelectorBinding.suits12, Arrays.asList(2, 3));
        pairButtonSuitsMap.put(rangeSelectorBinding.suits21, Arrays.asList(2, 4));
        pairButtonSuitsMap.put(rangeSelectorBinding.suits22, Arrays.asList(3, 4));

        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits00, Arrays.asList(2, 1));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits01, Arrays.asList(1, 2));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits02, Arrays.asList(1, 3));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits03, Arrays.asList(3, 1));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits10, Arrays.asList(4, 1));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits11, Arrays.asList(1, 4));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits12, Arrays.asList(2, 3));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits13, Arrays.asList(3, 2));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits20, Arrays.asList(4, 2));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits21, Arrays.asList(2, 4));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits22, Arrays.asList(3, 4));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits23, Arrays.asList(4, 3));
    }

    public void generate_main_layout() {
        super.generate_main_layout();

        for (int i = 0; i < 10; i++) {
            TexasHoldemPlayerRowBinding binding_player_row = TexasHoldemPlayerRowBinding.inflate(LayoutInflater.from(requireActivity()), equityCalculatorBinding.playerRows, true);
            player_row_array[i] = binding_player_row.getRoot();
            equityArray[i] = binding_player_row.equity;
            winArray[i] = binding_player_row.win;
            tieArray[i] = binding_player_row.tie;
            rangePositionBiMap.put(i + 1, binding_player_row.range);
            twoCardsLayouts[i] = binding_player_row.twoCards;
            cardPositionBiMap.put(Arrays.asList(i + 1, 0), binding_player_row.card1);
            cardPositionBiMap.put(Arrays.asList(i + 1, 1), binding_player_row.card2);
            removeRowMap.put(binding_player_row.remove, i + 1);
            rangeSwitchRowMap.put(binding_player_row.handRangeButton, i + 1);

            binding_player_row.playerText.setText(getString(R.string.player, i + 1));
            binding_player_row.remove.setOnClickListener(removePlayerListener);
            binding_player_row.handRangeButton.setOnClickListener(rangeSwitchListener);
        }

        this.emptyRangeBitmap = Bitmap.createBitmap(cardHeight, cardHeight, Bitmap.Config.ARGB_8888);
        this.emptyRangeBitmap.eraseColor(Color.LTGRAY);

        for (ImageButton r : rangePositionBiMap.values()) {
            r.setImageBitmap(emptyRangeBitmap);
            r.setMaxHeight(cardHeight);
        }
    }

    private void generateRangeSelector() {
        LinearLayout.LayoutParams rowParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams buttonParam = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        buttonParam.setMargins(1, 1,1,1);

        int squareHeight = displayMetrics.widthPixels / 13;
        this.inputMatrixMap = HashBiMap.create();
        for (int row_idx = 0; row_idx < 13; row_idx++) {
            LinearLayout row = new LinearLayout(requireActivity());
            row.setLayoutParams(rowParam);

            for (int col_idx = 0; col_idx < 13; col_idx++) {
                MaterialButton b = new MaterialButton(requireActivity());
                b.setLayoutParams(buttonParam);
                b.setPadding(0, 0, 0, 0);
                b.setHeight(squareHeight);
                b.setMinimumHeight(squareHeight);
                b.setBackgroundColor(Color.LTGRAY);
                b.setTextColor(Color.BLACK);
                b.setAllCaps(false);
                b.setTextSize(11);
                b.setCornerRadius(0);
                b.setInsetBottom(0);
                b.setInsetTop(0);
                b.setStrokeColor(ColorStateList.valueOf(Color.RED));
                b.setOnClickListener(matrixListener);

                if (row_idx == col_idx) {
                    b.setText(getString(R.string.matrix_str, GlobalStatic.matrixStrings[row_idx], GlobalStatic.matrixStrings[row_idx], ""));
                } else if (col_idx > row_idx) {
                    b.setText(getString(R.string.matrix_str, GlobalStatic.matrixStrings[row_idx], GlobalStatic.matrixStrings[col_idx], "s"));
                } else {
                    b.setText(getString(R.string.matrix_str, GlobalStatic.matrixStrings[col_idx], GlobalStatic.matrixStrings[row_idx], "o"));
                }

                row.addView(b);
                this.inputMatrixMap.put(b, Arrays.asList(row_idx, col_idx));
            }
            rangeSelectorBinding.rangeMatrix.addView(row);
        }

        for (ImageButton b : offsuitButtonSuitsMap.keySet()) {
            b.setOnClickListener(suitsListener);
        }
    }

    private final View.OnClickListener rangeSwitchListener = v -> {
        final MaterialButton rangeSwitchInput = (MaterialButton) v;
        int playerRangeSwitchNumber = rangeSwitchRowMap.get(rangeSwitchInput);

        if (cardRows[playerRangeSwitchNumber] instanceof SpecificCardsRow) {
            for (int i = 0; i < 2; i++) {
                setInputCardVisible(playerRangeSwitchNumber, i);
            }

            twoCardsLayouts[playerRangeSwitchNumber - 1].setVisibility(View.GONE);
            cardRows[playerRangeSwitchNumber] = new RangeRow();
            ImageButton b = this.rangePositionBiMap.get(playerRangeSwitchNumber);
            assert b != null;
            b.setImageBitmap(this.emptyRangeBitmap);
            b.setVisibility(View.VISIBLE);
        } else {
            setEmptyHandRow(playerRangeSwitchNumber);
        }

        hideCardSelector();

        calculate_odds();
    };

    public void setEmptyHandRow(int row) {
        super.setEmptyHandRow(row);
        Objects.requireNonNull(rangePositionBiMap.get(row)).setVisibility(View.GONE);
        twoCardsLayouts[row - 1].setVisibility(View.VISIBLE);
    }

    private final View.OnClickListener rangeSelectorListener = v -> {
        ImageButton rangeSelectorInput = (ImageButton) v;
        selectedRangeButton = rangeSelectorInput;
        selectedRangePosition = rangePositionBiMap.inverse().get(rangeSelectorInput);

        RangeRow rangeRow = (RangeRow) this.cardRows[selectedRangePosition];

        this.matrixInput = GlobalStatic.copyMatrix(rangeRow.matrix);

        int handCount = 0;

        for (int row_idx = 0; row_idx < 13; row_idx++) {
            for (int col_idx = 0; col_idx < 13; col_idx++) {
                Set<String> suits = this.matrixInput.get(row_idx).get(col_idx);
                if (GlobalStatic.isAllSuits(suits, row_idx, col_idx)) {
                    Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(row_idx, col_idx))).setBackgroundColor(Color.YELLOW);
                } else if (suits.isEmpty()) {
                    Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(row_idx, col_idx))).setBackgroundColor(Color.LTGRAY);
                } else {
                    Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(row_idx, col_idx))).setBackgroundColor(Color.CYAN);
                }

                handCount += suits.size();
            }
        }

        rangeSelectorBinding.rangeSlider.setValue(handCount);

        clearSuitSelectorUI();

        equityCalculatorBinding.mainUi.setVisibility(View.GONE);
        rangeSelectorBinding.rangeSelector.setVisibility(View.VISIBLE);
    };


    private final View.OnClickListener matrixListener = v -> {
        MaterialButton matrixButton = (MaterialButton) v;

        List<Integer> matrixPosition = inputMatrixMap.get(matrixButton);
        assert matrixPosition != null;
        int row = matrixPosition.get(0);
        int col = matrixPosition.get(1);

        Set<String> suits = this.matrixInput.get(row).get(col);

        if (GlobalStatic.isAllSuits(suits, row, col)) {
            rangeSelectorBinding.rangeSlider.setValue(rangeSelectorBinding.rangeSlider.getValue() - suits.size());
            suits.clear();
            matrixButton.setBackgroundColor(Color.LTGRAY);
        } else if (suits.isEmpty()) {
            GlobalStatic.addAllSuits(suits, row, col);

            rangeSelectorBinding.rangeSlider.setValue(rangeSelectorBinding.rangeSlider.getValue() + suits.size());

            matrixButton.setBackgroundColor(Color.YELLOW);
        }

        if (selectedMatrixButton != null) {
            selectedMatrixButton.setStrokeWidth(0);
        }

        selectedMatrixPosition[0] = row;
        selectedMatrixPosition[1] = col;

        selectedMatrixButton = matrixButton;
        selectedMatrixButton.setStrokeWidth(2);

        if (row == col) {
            int rank = GlobalStatic.convertMatrixPositionToRankInt(row);
            setSuitSelectorUI(pairButtonSuitsMap, rank, rank, suits);
        } else if (col > row) {
            int highRank = GlobalStatic.convertMatrixPositionToRankInt(row);
            int lowRank = GlobalStatic.convertMatrixPositionToRankInt(col);
            setSuitSelectorUI(suitedButtonSuitsMap, highRank, lowRank, suits);
        } else {
            int highRank = GlobalStatic.convertMatrixPositionToRankInt(col);
            int lowRank = GlobalStatic.convertMatrixPositionToRankInt(row);
            setSuitSelectorUI(offsuitButtonSuitsMap, highRank, lowRank, suits);
        }

        rangeSelectorBinding.suitSelectorText.setText(R.string.choose_suits);
    };

    private final View.OnClickListener suitsListener = v -> {
        ImageButton suitsButton = (ImageButton) v;

        int row = selectedMatrixPosition[0];
        int col = selectedMatrixPosition[1];

        Set<String> suits = this.matrixInput.get(row).get(col);
        List<Integer> s;
        if (row == col) {
            s = pairButtonSuitsMap.get(suitsButton);
        } else if (col > row) {
            s = suitedButtonSuitsMap.get(suitsButton);
        } else {
            s = offsuitButtonSuitsMap.get(suitsButton);
        }

        assert s != null;
        String currentSuit = GlobalStatic.suitToStr.get(s.get(0)) + GlobalStatic.suitToStr.get(s.get(1));
        if (suits.contains(currentSuit)) {
            suits.remove(currentSuit);
            suitsButton.setBackgroundResource(0);
            rangeSelectorBinding.rangeSlider.setValue(rangeSelectorBinding.rangeSlider.getValue() - 1);
        } else {
            suits.add(currentSuit);
            suitsButton.setBackgroundResource(R.drawable.border_selector);
            rangeSelectorBinding.rangeSlider.setValue(rangeSelectorBinding.rangeSlider.getValue() + 1);
        }

        if (GlobalStatic.isAllSuits(suits, row, col)) {
            Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(row, col))).setBackgroundColor(Color.YELLOW);
        } else if (suits.isEmpty()) {
            Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(row, col))).setBackgroundColor(Color.LTGRAY);
        } else {
            Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(row, col))).setBackgroundColor(Color.CYAN);
        }
    };


    private void setSuitSelectorUI(Map<ImageButton, List<Integer>> buttonSuitsMap, int highRank, int lowRank, Set<String> suits) {
        for (ImageButton b : offsuitButtonSuitsMap.keySet()) {
            List<Integer> s = buttonSuitsMap.get(b);
            if (s == null) {
                b.setVisibility(View.INVISIBLE);
            } else {
                Drawable leftCard = ContextCompat.getDrawable(requireActivity(), GlobalStatic.suitRankDrawableMap.get(s.get(0)).get(highRank));
                Drawable rightCard = ContextCompat.getDrawable(requireActivity(), GlobalStatic.suitRankDrawableMap.get(s.get(1)).get(lowRank));

                LayerDrawable combinedDrawable = new LayerDrawable(new Drawable[] {leftCard, rightCard});
                assert rightCard != null;
                combinedDrawable.setLayerInsetRight(0, rightCard.getIntrinsicWidth());
                combinedDrawable.setLayerGravity(1, Gravity.RIGHT);

                b.setImageDrawable(combinedDrawable);

                String currentSuit = GlobalStatic.suitToStr.get(s.get(0)) + GlobalStatic.suitToStr.get(s.get(1));

                if (suits.contains(currentSuit)) {
                    b.setBackgroundResource(R.drawable.border_selector);
                } else {
                    b.setBackgroundResource(0);
                }

                b.setVisibility(View.VISIBLE);
            }
        }
    }

    private void clearSuitSelectorUI() {
        if (selectedMatrixButton != null) {
            selectedMatrixButton.setStrokeWidth(0);
        }

        for (ImageButton b : offsuitButtonSuitsMap.keySet()) {
            b.setVisibility(View.INVISIBLE);
        }

        rangeSelectorBinding.suitSelectorText.setText(R.string.select_a_hand_to_choose_suits);
    }

}