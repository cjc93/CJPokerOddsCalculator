package com.leslie.cjpokeroddscalculator.fragment;

import static com.leslie.cjpokeroddscalculator.GlobalStatic.deleteKeyFromDataStore;
import static com.leslie.cjpokeroddscalculator.GlobalStatic.getDataFromDataStoreIfExist;
import static com.leslie.cjpokeroddscalculator.GlobalStatic.rankStrings;
import static com.leslie.cjpokeroddscalculator.GlobalStatic.suitRankDrawableMap;
import static com.leslie.cjpokeroddscalculator.GlobalStatic.writeToDataStore;

import static java.lang.Math.min;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leslie.cjpokeroddscalculator.GlobalStatic;
import com.leslie.cjpokeroddscalculator.MainActivity;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.databinding.RangeSelectorBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.reactivex.rxjava3.core.Single;

public class RangeSelector {
    private final TexasHoldemFragment texasHoldemFragment;
    public RangeSelectorBinding rangeSelectorBinding;
    private MaterialButton selectedMatrixButton = null;
    private final int[] selectedMatrixPosition = new int[2];
    HashBiMap<MaterialButton, List<Integer>> inputMatrixMap;
    List<List<Set<String>>> matrixInput;
    Map<ImageButton, String> pairButtonSuitsMap = new HashMap<>();
    Map<ImageButton, String> suitedButtonSuitsMap = new HashMap<>();
    Map<ImageButton, String> offsuitButtonSuitsMap = new HashMap<>();
    Map<String, MaterialButton> presetHandRangeMap = new HashMap<>();
    Gson gson = new Gson();

    public RangeSelector(TexasHoldemFragment texasHoldemFragment) {
        this.texasHoldemFragment = texasHoldemFragment;
    }

    public void addBackPressedCallback() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (rangeSelectorBinding.rangeSelector.getVisibility() == View.VISIBLE) {
                    rangeSelectorBinding.rangeSelector.setVisibility(View.GONE);
                    texasHoldemFragment.equityCalculatorBinding.mainUi.setVisibility(View.VISIBLE);
                } else {
                    setEnabled(false);
                    texasHoldemFragment.requireActivity().getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        };
        texasHoldemFragment.requireActivity().getOnBackPressedDispatcher().addCallback(texasHoldemFragment.getViewLifecycleOwner(), callback);
    }

    public void generateRangeSelector() {
        int squareLength = min(texasHoldemFragment.displayMetrics.widthPixels - 12, texasHoldemFragment.displayMetrics.heightPixels / 2)  / 13;
        this.inputMatrixMap = HashBiMap.create();

        for (int rowIdx = 0; rowIdx < 13; rowIdx++) {
            for (int colIdx = 0; colIdx < 13; colIdx++) {
                MaterialButton b = new MaterialButton(texasHoldemFragment.requireActivity());
                b.setId(View.generateViewId());
                b.setPadding(0, 0, 0, 0);
                b.setHeight(squareLength);
                b.setMinimumHeight(squareLength);
                b.setMinimumWidth(squareLength);
                b.setMinWidth(squareLength);
                b.setBackgroundColor(Color.LTGRAY);
                b.setTextColor(Color.BLACK);
                b.setAllCaps(false);
                b.setTextSize(11);
                b.setCornerRadius(0);
                b.setInsetBottom(0);
                b.setInsetTop(0);
                b.setStrokeColor(ColorStateList.valueOf(Color.RED));
                b.setOnClickListener(matrixListener);

                if (rowIdx == colIdx) {
                    b.setText(texasHoldemFragment.getString(R.string.matrix_str, GlobalStatic.rankStrings[rowIdx], GlobalStatic.rankStrings[rowIdx], ""));
                } else if (colIdx > rowIdx) {
                    b.setText(texasHoldemFragment.getString(R.string.matrix_str, GlobalStatic.rankStrings[rowIdx], GlobalStatic.rankStrings[colIdx], "s"));
                } else {
                    b.setText(texasHoldemFragment.getString(R.string.matrix_str, GlobalStatic.rankStrings[colIdx], GlobalStatic.rankStrings[rowIdx], "o"));
                }

                rangeSelectorBinding.rangeSelector.addView(b);
                this.inputMatrixMap.put(b, Arrays.asList(rowIdx, colIdx));
            }
        }


        for (int rowIdx = 0; rowIdx < 13; rowIdx++) {
            for (int colIdx = 0; colIdx < 13; colIdx++) {
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.horizontalChainStyle = ConstraintLayout.LayoutParams.CHAIN_PACKED;

                if (rowIdx == 0) {
                    layoutParams.topToTop = ConstraintSet.PARENT_ID;
                } else {
                    layoutParams.topToBottom = Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(rowIdx - 1, colIdx))).getId();
                    layoutParams.topMargin = 1;
                }

                if (colIdx == 0) {
                    layoutParams.leftToLeft = ConstraintSet.PARENT_ID;
                    layoutParams.rightToLeft = Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(rowIdx, 1))).getId();
                } else if (colIdx == 12) {
                    layoutParams.rightToRight = ConstraintSet.PARENT_ID;
                    layoutParams.leftToRight = Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(rowIdx, 11))).getId();
                    layoutParams.leftMargin = 1;
                } else {
                    layoutParams.leftToRight = Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(rowIdx, colIdx - 1))).getId();
                    layoutParams.rightToLeft = Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(rowIdx, colIdx + 1))).getId();
                    layoutParams.leftMargin = 1;
                }

                MaterialButton button = Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(rowIdx, colIdx)));
                button.setLayoutParams(layoutParams);
            }
        }

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) rangeSelectorBinding.rangeSlider.getLayoutParams();
        layoutParams.topToBottom = Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(12, 0))).getId();
        rangeSelectorBinding.rangeSlider.setLayoutParams(layoutParams);

        String rangeNamesJson = getDataFromDataStoreIfExist(((MainActivity) texasHoldemFragment.requireActivity()).dataStore, PreferencesKeys.stringKey("texas_holdem_equity_calculator_range_names"));

        if (rangeNamesJson != null) {
            List<String> rangeNameList = gson.fromJson(rangeNamesJson, new TypeToken<List<String>>(){}.getType());

            for (String rangeName : rangeNameList) {
                appendPresetRangeButton(rangeName);
            }
        }

        for (ImageButton b : offsuitButtonSuitsMap.keySet()) {
            b.setOnClickListener(suitsListener);
        }
    }

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
            String rank = rankStrings[row];
            setSuitSelectorUI(pairButtonSuitsMap, rank, rank, suits);
        } else if (col > row) {
            String highRank = rankStrings[row];
            String lowRank = rankStrings[col];
            setSuitSelectorUI(suitedButtonSuitsMap, highRank, lowRank, suits);
        } else {
            String highRank = rankStrings[col];
            String lowRank = rankStrings[row];
            setSuitSelectorUI(offsuitButtonSuitsMap, highRank, lowRank, suits);
        }

        rangeSelectorBinding.suitSelectorText.setText(R.string.choose_suits);
    };

    public void appendPresetRangeButton(String rangeName) {
        MaterialButton b = new MaterialButton(texasHoldemFragment.requireActivity());
        b.setId(View.generateViewId());
        b.setText(rangeName);
        b.setTextSize(12);
        b.setCornerRadius(20);
        b.setPadding(40, 30, 40, 30);
        b.setMinimumHeight(0);
        b.setMinHeight(0);
        b.setMinWidth(0);
        b.setMinimumWidth(0);

        b.setOnClickListener(v -> {
            Preferences.Key<String> RANGE_NAME_KEY = PreferencesKeys.stringKey("thec_" + b.getText());
            String matrixJson = ((MainActivity) texasHoldemFragment.requireActivity()).dataStore.data().map(prefs -> prefs.get(RANGE_NAME_KEY)).blockingFirst();
            List<List<Set<String>>> savedMatrix = gson.fromJson(matrixJson, new TypeToken<List<List<Set<String>>>>(){}.getType());

            updateRangeSelector(savedMatrix);
        });

        b.setOnLongClickListener(v -> {
            EditPresetHandRangeFragment dialog = EditPresetHandRangeFragment.newInstance((String) b.getText(), new ArrayList<>(presetHandRangeMap.keySet()));
            dialog.show(texasHoldemFragment.getParentFragmentManager(), "EDIT_PRESET_HAND_RANGE_DIALOG");
            return true;
        });

        presetHandRangeMap.put(rangeName, b);

        rangeSelectorBinding.presetHandRangeInitialText.setVisibility(View.GONE);
        rangeSelectorBinding.presetHandRangeLayout.addView(b);
        rangeSelectorBinding.presetHandRangeFlow.addView(b);
    }

    private final View.OnClickListener suitsListener = v -> {
        ImageButton suitsButton = (ImageButton) v;

        int row = selectedMatrixPosition[0];
        int col = selectedMatrixPosition[1];

        Set<String> suits = this.matrixInput.get(row).get(col);
        String currentSuit;
        if (row == col) {
            currentSuit = pairButtonSuitsMap.get(suitsButton);
        } else if (col > row) {
            currentSuit = suitedButtonSuitsMap.get(suitsButton);
        } else {
            currentSuit = offsuitButtonSuitsMap.get(suitsButton);
        }

        assert currentSuit != null;
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

    private void setSuitSelectorUI(Map<ImageButton, String> buttonSuitsMap, String highRank, String lowRank, Set<String> suits) {
        for (ImageButton b : offsuitButtonSuitsMap.keySet()) {
            String currentSuit = buttonSuitsMap.get(b);
            if (currentSuit == null) {
                b.setVisibility(View.INVISIBLE);
            } else {
                Integer leftID = suitRankDrawableMap.get(highRank + currentSuit.charAt(0));
                assert leftID != null;
                Drawable leftCard = ContextCompat.getDrawable(texasHoldemFragment.requireActivity(), leftID);

                Integer rightID = suitRankDrawableMap.get(lowRank + currentSuit.charAt(1));
                assert rightID != null;
                Drawable rightCard = ContextCompat.getDrawable(texasHoldemFragment.requireActivity(), rightID);

                LayerDrawable combinedDrawable = new LayerDrawable(new Drawable[] {leftCard, rightCard});

                assert rightCard != null;
                combinedDrawable.setLayerInsetRight(0, rightCard.getIntrinsicWidth());
                combinedDrawable.setLayerGravity(1, Gravity.END);

                b.setImageDrawable(combinedDrawable);

                if (suits.contains(currentSuit)) {
                    b.setBackgroundResource(R.drawable.border_selector);
                } else {
                    b.setBackgroundResource(0);
                }

                b.setVisibility(View.VISIBLE);
            }
        }
    }

    public void updateRangeSelector(List<List<Set<String>>> matrix) {
        this.matrixInput = GlobalStatic.copyMatrix(matrix);

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

    public void setListeners() {
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
            rangeSelectorBinding.handsPerc.setText(texasHoldemFragment.getString(R.string.hands_perc, slider.getValue() / 1326.0 * 100));
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

        rangeSelectorBinding.addRangeButton.setOnClickListener(v -> {
            AddPresetHandRangeFragment dialog = AddPresetHandRangeFragment.newInstance(new ArrayList<>(presetHandRangeMap.keySet()));
            dialog.show(texasHoldemFragment.getParentFragmentManager(), "ADD_PRESET_HAND_RANGE_DIALOG");
        });

        rangeSelectorBinding.done.setOnClickListener(v -> {
            texasHoldemFragment.updateRange(this.matrixInput);
            rangeSelectorBinding.rangeSelector.setVisibility(View.GONE);
            texasHoldemFragment.equityCalculatorBinding.mainUi.setVisibility(View.VISIBLE);
        });
    }

    public void setFragmentResultListeners() {
        texasHoldemFragment.requireActivity().getSupportFragmentManager().setFragmentResultListener("add_preset_hand_range", texasHoldemFragment.getViewLifecycleOwner(), (requestKey, result) -> {
            String rangeName = (String) result.get("range_name");

            writeToDataStore(
                    ((MainActivity) texasHoldemFragment.requireActivity()).dataStore,
                    PreferencesKeys.stringKey("thec_" + rangeName),
                    gson.toJson(this.matrixInput)
            );

            Preferences.Key<String> ALL_NAMES_KEY = PreferencesKeys.stringKey("texas_holdem_equity_calculator_range_names");

            String rangeNamesJson = getDataFromDataStoreIfExist(((MainActivity) texasHoldemFragment.requireActivity()).dataStore, ALL_NAMES_KEY);

            if (rangeNamesJson == null) {
                writeToDataStore(
                        ((MainActivity) texasHoldemFragment.requireActivity()).dataStore,
                        ALL_NAMES_KEY,
                        gson.toJson(Collections.singletonList(rangeName))
                );
            } else {
                List<String> rangeNameList = gson.fromJson(rangeNamesJson, new TypeToken<List<String>>(){}.getType());
                rangeNameList.add(rangeName);
                writeToDataStore(((MainActivity) texasHoldemFragment.requireActivity()).dataStore, ALL_NAMES_KEY, gson.toJson(rangeNameList));
            }

            appendPresetRangeButton(rangeName);
        });

        texasHoldemFragment.requireActivity().getSupportFragmentManager().setFragmentResultListener("rename_preset_hand_range", texasHoldemFragment.getViewLifecycleOwner(), (requestKey, result) -> {
            String oldRangeName = (String) result.get("old_range_name");
            String newRangeName = (String) result.get("new_range_name");

            Preferences.Key<String> OLD_RANGE_NAME_KEY = PreferencesKeys.stringKey("thec_" + oldRangeName);
            Preferences.Key<String> NEW_RANGE_NAME_KEY = PreferencesKeys.stringKey("thec_" + newRangeName);

            String matrixJson = ((MainActivity) texasHoldemFragment.requireActivity()).dataStore.data().map(prefs -> prefs.get(OLD_RANGE_NAME_KEY)).blockingFirst();

            deleteKeyFromDataStore(((MainActivity) texasHoldemFragment.requireActivity()).dataStore, OLD_RANGE_NAME_KEY);

            writeToDataStore(
                    ((MainActivity) texasHoldemFragment.requireActivity()).dataStore,
                    NEW_RANGE_NAME_KEY,
                    matrixJson
            );

            Preferences.Key<String> ALL_NAMES_KEY = PreferencesKeys.stringKey("texas_holdem_equity_calculator_range_names");

            String rangeNamesJson = ((MainActivity) texasHoldemFragment.requireActivity()).dataStore.data().map(prefs -> prefs.get(ALL_NAMES_KEY)).blockingFirst();

            List<String> rangeNameList = gson.fromJson(rangeNamesJson, new TypeToken<List<String>>(){}.getType());
            assert rangeNameList != null;
            if (Collections.replaceAll(rangeNameList, oldRangeName, newRangeName)) {
                writeToDataStore(((MainActivity) texasHoldemFragment.requireActivity()).dataStore, ALL_NAMES_KEY, gson.toJson(rangeNameList));
            }

            MaterialButton presetHandRangeButton = presetHandRangeMap.get(oldRangeName);
            assert presetHandRangeButton != null;
            presetHandRangeButton.setText(newRangeName);

            presetHandRangeMap.remove(oldRangeName);
            presetHandRangeMap.put(newRangeName, presetHandRangeButton);
        });

        texasHoldemFragment.requireActivity().getSupportFragmentManager().setFragmentResultListener("delete_preset_hand_range", texasHoldemFragment.getViewLifecycleOwner(), (requestKey, result) -> {
            String rangeName = (String) result.get("range_name");

            ((MainActivity) texasHoldemFragment.requireActivity()).dataStore.updateDataAsync(prefsIn -> {
                MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
                mutablePreferences.remove(PreferencesKeys.stringKey("thec_" + rangeName));
                return Single.just(mutablePreferences);
            });

            Preferences.Key<String> ALL_NAMES_KEY = PreferencesKeys.stringKey("texas_holdem_equity_calculator_range_names");

            String rangeNamesJson = ((MainActivity) texasHoldemFragment.requireActivity()).dataStore.data().map(prefs -> prefs.get(ALL_NAMES_KEY)).blockingFirst();

            List<String> rangeNameList = gson.fromJson(rangeNamesJson, new TypeToken<List<String>>(){}.getType());
            assert rangeNameList != null;
            rangeNameList.remove(rangeName);
            writeToDataStore(((MainActivity) texasHoldemFragment.requireActivity()).dataStore, ALL_NAMES_KEY, gson.toJson(rangeNameList));

            MaterialButton presetHandRangeButton = presetHandRangeMap.get(rangeName);
            rangeSelectorBinding.presetHandRangeLayout.removeView(presetHandRangeButton);
            rangeSelectorBinding.presetHandRangeFlow.removeView(presetHandRangeButton);

            presetHandRangeMap.remove(rangeName);

            if (presetHandRangeMap.isEmpty()) {
                rangeSelectorBinding.presetHandRangeInitialText.setVisibility(View.VISIBLE);
            }
        });
    }

    public void initialiseVariables() {
        suitedButtonSuitsMap.put(rangeSelectorBinding.suits3, "ss");
        suitedButtonSuitsMap.put(rangeSelectorBinding.suits4, "hh");
        suitedButtonSuitsMap.put(rangeSelectorBinding.suits9, "cc");
        suitedButtonSuitsMap.put(rangeSelectorBinding.suits10, "dd");

        // The order of characters in these strings needs to be the same as GlobalStatic.pairSuits
        pairButtonSuitsMap.put(rangeSelectorBinding.suits1, "hs");
        pairButtonSuitsMap.put(rangeSelectorBinding.suits2, "cs");
        pairButtonSuitsMap.put(rangeSelectorBinding.suits3, "ds");
        pairButtonSuitsMap.put(rangeSelectorBinding.suits4, "ch");
        pairButtonSuitsMap.put(rangeSelectorBinding.suits5, "dh");
        pairButtonSuitsMap.put(rangeSelectorBinding.suits6, "dc");

        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits1, "sh");
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits2, "sc");
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits3, "sd");
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits4, "hc");
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits5, "hd");
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits6, "cd");
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits7, "hs");
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits8, "cs");
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits9, "ds");
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits10, "ch");
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits11, "dh");
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits12, "dc");
    }
}
