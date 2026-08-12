package com.leslie.cjpokeroddscalculator.fragment;

import static com.leslie.cjpokeroddscalculator.util.AndroidStatic.dpToPx;
import static com.leslie.cjpokeroddscalculator.util.GlobalStatic.rankStrings;
import static com.leslie.cjpokeroddscalculator.util.AndroidStatic.suitRankDrawableMap;

import static java.lang.Math.min;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.imageview.ShapeableImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leslie.cjpokeroddscalculator.util.AndroidStatic;
import com.leslie.cjpokeroddscalculator.util.GlobalStatic;
import com.leslie.cjpokeroddscalculator.MainActivity;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.databinding.RangeSelectorBinding;
import com.leslie.cjpokeroddscalculator.viewmodel.RangeSelectorViewModel;
import com.leslie.cjpokeroddscalculator.viewmodel.RangeSharedViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class RangeSelectorFragment extends Fragment {
    public RangeSelectorBinding rangeSelectorBinding;
    public RangeSelectorViewModel viewModel;
    private RangeSharedViewModel rangeSharedViewModel;

    private MaterialButton selectedMatrixButton = null;
    HashBiMap<MaterialButton, List<Integer>> inputMatrixMap;
    Map<ShapeableImageView, String> pairButtonSuitsMap = new HashMap<>();
    Map<ShapeableImageView, String> suitedButtonSuitsMap = new HashMap<>();
    Map<ShapeableImageView, String> offsuitButtonSuitsMap = new HashMap<>();
    Map<String, MaterialButton> savedHandRangeMap = new HashMap<>();
    Gson gson = new Gson();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rangeSelectorBinding = RangeSelectorBinding.inflate(inflater, container, false);
        return rangeSelectorBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RangeSelectorViewModel.class);
        rangeSharedViewModel = new ViewModelProvider(requireActivity()).get(RangeSharedViewModel.class);

        if (rangeSharedViewModel.matrix != null) {
            viewModel.updateRangeSelector(rangeSharedViewModel.matrix);
            rangeSharedViewModel.matrix = null;
        }

        initialiseVariables();
        generateRangeSelector();
        observeLiveData();
        setListeners();
        setFragmentResultListeners();
    }

    public void generateRangeSelector() {
        DisplayMetrics displayMetrics = AndroidStatic.getDisplayMetrics(requireActivity());

        int margin = dpToPx(requireContext(), 2);

        int squareLength, longSidePixels, shortSidePixels;
        if (displayMetrics.heightPixels > displayMetrics.widthPixels) {
            longSidePixels = displayMetrics.heightPixels;
            shortSidePixels = displayMetrics.widthPixels;
        } else {
            longSidePixels = displayMetrics.widthPixels;
            shortSidePixels = displayMetrics.heightPixels;
        }
        squareLength = (min(shortSidePixels, longSidePixels / 2) - 12 * margin) / 13;

        this.inputMatrixMap = HashBiMap.create();

        int cornerRadius = dpToPx(requireContext(), 4);

        for (int rowIdx = 0; rowIdx < 13; rowIdx++) {
            for (int colIdx = 0; colIdx < 13; colIdx++) {
                MaterialButton b = new MaterialButton(requireActivity());
                b.setId(View.generateViewId());
                b.setPadding(0, 0, 0, 0);
                b.setHeight(squareLength);
                b.setMinimumHeight(squareLength);
                b.setMinimumWidth(squareLength);
                b.setMinWidth(squareLength);
                b.setTextColor(Color.BLACK);
                b.setAllCaps(false);
                b.setTextSize(11);
                b.setCornerRadius(cornerRadius);
                b.setInsetBottom(0);
                b.setInsetTop(0);
                b.setStrokeColor(ColorStateList.valueOf(Color.RED));
                b.setOnClickListener(matrixListener);

                if (rowIdx == colIdx) {
                    b.setText(getString(R.string.matrix_str, GlobalStatic.rankStrings[rowIdx], GlobalStatic.rankStrings[rowIdx], ""));
                } else if (colIdx > rowIdx) {
                    b.setText(getString(R.string.matrix_str, GlobalStatic.rankStrings[rowIdx], GlobalStatic.rankStrings[colIdx], "s"));
                } else {
                    b.setText(getString(R.string.matrix_str, GlobalStatic.rankStrings[colIdx], GlobalStatic.rankStrings[rowIdx], "o"));
                }

                rangeSelectorBinding.matrix.addView(b);
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
                    layoutParams.topMargin = margin;
                }

                if (colIdx == 0) {
                    layoutParams.leftToLeft = ConstraintSet.PARENT_ID;
                    layoutParams.rightToLeft = Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(rowIdx, 1))).getId();
                } else if (colIdx == 12) {
                    layoutParams.rightToRight = ConstraintSet.PARENT_ID;
                    layoutParams.leftToRight = Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(rowIdx, 11))).getId();
                    layoutParams.leftMargin = margin;
                } else {
                    layoutParams.leftToRight = Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(rowIdx, colIdx - 1))).getId();
                    layoutParams.rightToLeft = Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(rowIdx, colIdx + 1))).getId();
                    layoutParams.leftMargin = margin;
                }

                MaterialButton button = Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(rowIdx, colIdx)));
                button.setLayoutParams(layoutParams);
            }
        }

        String rangeNamesJson = ((MainActivity) requireActivity()).dataStore.getDataFromDataStoreIfExist(PreferencesKeys.stringKey("texas_holdem_equity_calculator_range_names"));

        if (rangeNamesJson != null) {
            List<String> rangeNameList = gson.fromJson(rangeNamesJson, new TypeToken<List<String>>(){}.getType());

            for (String rangeName : rangeNameList) {
                appendSavedRangeButton(rangeName);
            }
        }

        for (ShapeableImageView b : offsuitButtonSuitsMap.keySet()) {
            b.setOnClickListener(suitsListener);
        }
    }

    private final View.OnClickListener matrixListener = v -> {
        MaterialButton matrixButton = (MaterialButton) v;

        List<Integer> matrixPosition = inputMatrixMap.get(matrixButton);
        assert matrixPosition != null;
        int row = matrixPosition.get(0);
        int col = matrixPosition.get(1);

        List<List<Set<String>>> matrixInput = viewModel.matrixInput.getValue();
        assert matrixInput != null;
        Set<String> suits = matrixInput.get(row).get(col);

        if (GlobalStatic.isAllSuits(suits, row, col)) {
            suits.clear();
        } else if (suits.isEmpty()) {
            GlobalStatic.addAllSuits(suits, row, col);
        }

        viewModel.matrixInput.setValue(matrixInput);

        viewModel.selectedMatrixPosition.setValue(new int[]{row, col});
    };

    public void appendSavedRangeButton(String rangeName) {
        MaterialButton b = new MaterialButton(requireActivity());
        b.setId(View.generateViewId());
        b.setText(rangeName);
        b.setTextSize(12);
        b.setTypeface(b.getTypeface(), Typeface.BOLD);
        b.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_content)));
        b.setCornerRadius(20);
        b.setPadding(40, 30, 40, 30);
        b.setMinimumHeight(0);
        b.setMinHeight(0);
        b.setMinWidth(0);
        b.setMinimumWidth(0);
        b.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        b.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_content)));
        b.setStrokeWidth(dpToPx(requireContext(), 1));

        b.setOnClickListener(v -> {
            Preferences.Key<String> RANGE_NAME_KEY = PreferencesKeys.stringKey("thec_" + b.getText());
            String matrixJson = ((MainActivity) requireActivity()).dataStore.getDataFromDataStoreIfExist(RANGE_NAME_KEY);

            List<List<Set<String>>> savedMatrix = gson.fromJson(matrixJson, new TypeToken<List<List<Set<String>>>>(){}.getType());

            viewModel.updateRangeSelector(savedMatrix);
        });

        b.setOnLongClickListener(v -> {
            EditSavedHandRangeFragment dialog = EditSavedHandRangeFragment.newInstance((String) b.getText(), new ArrayList<>(savedHandRangeMap.keySet()));
            dialog.show(getParentFragmentManager(), "EDIT_SAVED_HAND_RANGE_DIALOG");
            return true;
        });

        savedHandRangeMap.put(rangeName, b);

        rangeSelectorBinding.savedHandRangeInitialText.setVisibility(View.GONE);
        rangeSelectorBinding.savedHandRangeLayout.addView(b);
        rangeSelectorBinding.savedHandRangeFlow.addView(b);
    }

    private final View.OnClickListener suitsListener = v -> {
        ShapeableImageView suitsButton = (ShapeableImageView) v;

        int[] selectedMatrixPosition = viewModel.selectedMatrixPosition.getValue();
        assert selectedMatrixPosition != null;
        int row = selectedMatrixPosition[0];
        int col = selectedMatrixPosition[1];

        List<List<Set<String>>> matrixInput = viewModel.matrixInput.getValue();
        assert matrixInput != null;
        Set<String> suits = matrixInput.get(row).get(col);

        String currentSuit;
        if (row == col) {
            currentSuit = pairButtonSuitsMap.get(suitsButton);
        } else if (col > row) {
            currentSuit = suitedButtonSuitsMap.get(suitsButton);
        } else {
            currentSuit = offsuitButtonSuitsMap.get(suitsButton);
        }

        if (suits.contains(currentSuit)) {
            suits.remove(currentSuit);
        } else {
            suits.add(currentSuit);
        }

        viewModel.matrixInput.setValue(matrixInput);
    };

    private void setSuitSelectorUIGivenRankSuit(Map<ShapeableImageView, String> buttonSuitsMap, String highRank, String lowRank, Set<String> suits) {
        for (ShapeableImageView b : offsuitButtonSuitsMap.keySet()) {
            String currentSuit = buttonSuitsMap.get(b);
            if (currentSuit == null) {
                b.setVisibility(View.INVISIBLE);
            } else {
                Integer leftID = suitRankDrawableMap.get(highRank + currentSuit.charAt(0));
                assert leftID != null;
                Drawable leftCard = ContextCompat.getDrawable(requireActivity(), leftID);

                Integer rightID = suitRankDrawableMap.get(lowRank + currentSuit.charAt(1));
                assert rightID != null;
                Drawable rightCard = ContextCompat.getDrawable(requireActivity(), rightID);

                LayerDrawable combinedDrawable = new LayerDrawable(new Drawable[] {leftCard, rightCard});

                assert rightCard != null;
                combinedDrawable.setLayerInsetRight(0, rightCard.getIntrinsicWidth());
                combinedDrawable.setLayerGravity(1, Gravity.END);

                b.setImageDrawable(combinedDrawable);

                if (suits.contains(currentSuit)) {
                    b.setStrokeWidth(dpToPx(b.getContext(), 3));
                } else {
                    b.setStrokeWidth(0);
                }

                b.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setSuitSelectorUIGivenRowCol(List<List<Set<String>>> matrixInput, int row, int col) {
        Set<String> suits = matrixInput.get(row).get(col);

        if (row == col) {
            String rank = rankStrings[row];
            setSuitSelectorUIGivenRankSuit(pairButtonSuitsMap, rank, rank, suits);
        } else if (col > row) {
            String highRank = rankStrings[row];
            String lowRank = rankStrings[col];
            setSuitSelectorUIGivenRankSuit(suitedButtonSuitsMap, highRank, lowRank, suits);
        } else {
            String highRank = rankStrings[col];
            String lowRank = rankStrings[row];
            setSuitSelectorUIGivenRankSuit(offsuitButtonSuitsMap, highRank, lowRank, suits);
        }
    }

    public void setListeners() {
        rangeSelectorBinding.rangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                List<List<Set<String>>> matrixInput = viewModel.matrixInput.getValue();
                assert matrixInput != null;

                float finalValue = 0;
                for (java.util.Map.Entry<Integer, List<Integer>> entry : GlobalStatic.bestHandsMap.entrySet()) {
                    List<Integer> matrixPosition = entry.getValue();
                    int cumulativeHands = entry.getKey();
                    int row = matrixPosition.get(0);
                    int col = matrixPosition.get(1);
                    Set<String> suits = matrixInput.get(row).get(col);
                    if (cumulativeHands <= value) {
                        GlobalStatic.addAllSuits(suits, row, col);
                        finalValue = cumulativeHands;
                    } else {
                        suits.clear();
                    }
                }
                viewModel.matrixInput.setValue(matrixInput);
                slider.setValue(finalValue);
            }
            rangeSelectorBinding.handsPerc.setText(getString(R.string.hands_perc, slider.getValue() / 1326.0 * 100));
        });

        rangeSelectorBinding.rangeSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                viewModel.selectedMatrixPosition.setValue(null);
                for (MaterialButton b : inputMatrixMap.keySet()) {
                    b.setClickable(false);
                }
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                for (MaterialButton b : inputMatrixMap.keySet()) {
                    b.setClickable(true);
                }
            }
        });

        rangeSelectorBinding.addRangeButton.setOnClickListener(v -> {
            AddSavedHandRangeFragment dialog = AddSavedHandRangeFragment.newInstance(new ArrayList<>(savedHandRangeMap.keySet()));
            dialog.show(getParentFragmentManager(), "ADD_SAVED_HAND_RANGE_DIALOG");
        });

        rangeSelectorBinding.done.setOnClickListener(v -> {
            rangeSharedViewModel.matrix = viewModel.matrixInput.getValue();
            getParentFragmentManager().popBackStack();
        });
    }

    public void setFragmentResultListeners() {
        getParentFragmentManager().setFragmentResultListener("add_saved_hand_range", getViewLifecycleOwner(), (requestKey, result) -> {
            String rangeName = result.getString("range_name");

            ((MainActivity) requireActivity()).dataStore.writeToDataStore(
                PreferencesKeys.stringKey("thec_" + rangeName),
                gson.toJson(viewModel.matrixInput.getValue())
            );

            Preferences.Key<String> ALL_NAMES_KEY = PreferencesKeys.stringKey("texas_holdem_equity_calculator_range_names");

            String rangeNamesJson = ((MainActivity) requireActivity()).dataStore.getDataFromDataStoreIfExist(ALL_NAMES_KEY);

            if (rangeNamesJson == null) {
                ((MainActivity) requireActivity()).dataStore.writeToDataStore(
                    ALL_NAMES_KEY,
                    gson.toJson(Collections.singletonList(rangeName))
                );
            } else {
                List<String> rangeNameList = gson.fromJson(rangeNamesJson, new TypeToken<List<String>>(){}.getType());
                rangeNameList.add(rangeName);
                ((MainActivity) requireActivity()).dataStore.writeToDataStore(ALL_NAMES_KEY, gson.toJson(rangeNameList));
            }

            appendSavedRangeButton(rangeName);
        });

        getParentFragmentManager().setFragmentResultListener("rename_saved_hand_range", getViewLifecycleOwner(), (requestKey, result) -> {
            String oldRangeName = result.getString("old_range_name");
            String newRangeName = result.getString("new_range_name");

            Preferences.Key<String> OLD_RANGE_NAME_KEY = PreferencesKeys.stringKey("thec_" + oldRangeName);
            Preferences.Key<String> NEW_RANGE_NAME_KEY = PreferencesKeys.stringKey("thec_" + newRangeName);

            String matrixJson = ((MainActivity) requireActivity()).dataStore.getDataFromDataStoreIfExist(OLD_RANGE_NAME_KEY);

            ((MainActivity) requireActivity()).dataStore.deleteKeyFromDataStore(OLD_RANGE_NAME_KEY);

            ((MainActivity) requireActivity()).dataStore.writeToDataStore(
                NEW_RANGE_NAME_KEY,
                matrixJson
            );

            Preferences.Key<String> ALL_NAMES_KEY = PreferencesKeys.stringKey("texas_holdem_equity_calculator_range_names");

            String rangeNamesJson = ((MainActivity) requireActivity()).dataStore.getDataFromDataStoreIfExist(ALL_NAMES_KEY);

            List<String> rangeNameList = gson.fromJson(rangeNamesJson, new TypeToken<List<String>>(){}.getType());
            if (Collections.replaceAll(rangeNameList, oldRangeName, newRangeName)) {
                ((MainActivity) requireActivity()).dataStore.writeToDataStore(ALL_NAMES_KEY, gson.toJson(rangeNameList));
            }

            MaterialButton savedHandRangeButton = savedHandRangeMap.get(oldRangeName);
            assert savedHandRangeButton != null;
            savedHandRangeButton.setText(newRangeName);

            savedHandRangeMap.remove(oldRangeName);
            savedHandRangeMap.put(newRangeName, savedHandRangeButton);
        });

        getParentFragmentManager().setFragmentResultListener("delete_saved_hand_range", getViewLifecycleOwner(), (requestKey, result) -> {
            String rangeName = result.getString("range_name");

            ((MainActivity) requireActivity()).dataStore.deleteKeyFromDataStore(PreferencesKeys.stringKey("thec_" + rangeName));

            Preferences.Key<String> ALL_NAMES_KEY = PreferencesKeys.stringKey("texas_holdem_equity_calculator_range_names");

            String rangeNamesJson = ((MainActivity) requireActivity()).dataStore.getDataFromDataStoreIfExist(ALL_NAMES_KEY);

            if (rangeNamesJson != null) {
                List<String> rangeNameList = gson.fromJson(rangeNamesJson, new TypeToken<List<String>>(){}.getType());
                rangeNameList.remove(rangeName);
                ((MainActivity) requireActivity()).dataStore.writeToDataStore(ALL_NAMES_KEY, gson.toJson(rangeNameList));
            }

            MaterialButton savedHandRangeButton = savedHandRangeMap.get(rangeName);
            rangeSelectorBinding.savedHandRangeFlow.removeView(savedHandRangeButton);
            rangeSelectorBinding.savedHandRangeLayout.removeView(savedHandRangeButton);

            savedHandRangeMap.remove(rangeName);

            if (savedHandRangeMap.isEmpty()) {
                rangeSelectorBinding.savedHandRangeInitialText.setVisibility(View.VISIBLE);
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

    public void observeLiveData() {
        viewModel.matrixInput.observe(getViewLifecycleOwner(), matrixInput -> {
            int handCount = 0;

            for (int row_idx = 0; row_idx < 13; row_idx++) {
                for (int col_idx = 0; col_idx < 13; col_idx++) {
                    Set<String> suits = matrixInput.get(row_idx).get(col_idx);
                    if (GlobalStatic.isAllSuits(suits, row_idx, col_idx)) {
                        Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(row_idx, col_idx))).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.all_suits));
                    } else if (suits.isEmpty()) {
                        Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(row_idx, col_idx))).setBackgroundColor(Color.parseColor("#E3E2E6"));
                    } else {
                        Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(row_idx, col_idx))).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.partial_suits));
                    }

                    handCount += suits.size();
                }
            }

            rangeSelectorBinding.rangeSlider.setValue(handCount);

            int[] selectedMatrixPosition = viewModel.selectedMatrixPosition.getValue();
            if (selectedMatrixPosition != null) {
                int row = selectedMatrixPosition[0];
                int col = selectedMatrixPosition[1];

                setSuitSelectorUIGivenRowCol(matrixInput, row, col);
            }
        });

        viewModel.selectedMatrixPosition.observe(getViewLifecycleOwner(), selectedMatrixPosition -> {
            if (selectedMatrixButton != null) {
                selectedMatrixButton.setStrokeWidth(0);
            }

            if (selectedMatrixPosition == null) {
                for (ShapeableImageView b : offsuitButtonSuitsMap.keySet()) {
                    b.setVisibility(View.INVISIBLE);
                }

                rangeSelectorBinding.suitSelectorText.setText(R.string.select_a_hand_to_choose_suits);
            } else {
                int row = selectedMatrixPosition[0];
                int col = selectedMatrixPosition[1];

                selectedMatrixButton = Objects.requireNonNull(inputMatrixMap.inverse().get(Arrays.asList(row, col)));
                selectedMatrixButton.setStrokeWidth(dpToPx(requireContext(), 2));

                rangeSelectorBinding.suitSelectorText.setText(R.string.choose_suits);

                setSuitSelectorUIGivenRowCol(Objects.requireNonNull(viewModel.matrixInput.getValue()), row, col);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rangeSelectorBinding = null;
    }
}
