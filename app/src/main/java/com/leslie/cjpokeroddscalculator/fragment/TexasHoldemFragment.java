package com.leslie.cjpokeroddscalculator.fragment;

import static com.leslie.cjpokeroddscalculator.GlobalStatic.deleteKeyFromDataStore;
import static com.leslie.cjpokeroddscalculator.GlobalStatic.getDataFromDataStoreIfExist;
import static com.leslie.cjpokeroddscalculator.GlobalStatic.writeToDataStore;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leslie.cjpokeroddscalculator.GlobalStatic;
import com.leslie.cjpokeroddscalculator.MainActivity;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.RangeRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.calculation.TexasHoldemExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.TexasHoldemMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.databinding.RangeSelectorBinding;
import com.leslie.cjpokeroddscalculator.databinding.TexasHoldemPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemLiveUpdate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.reactivex.rxjava3.core.Single;

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
    Map<MaterialButton, LinearLayout> statsButtonMap = new HashMap<>();
    Map<String, MaterialButton> presetHandRangeMap = new HashMap<>();

    public TextView[][] handStats = new TextView[10][9];
    Gson gson = new Gson();

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

        for (int i = 0; i < 2; i++) {
            equityArray[i].setText(getString(R.string.two_decimal_perc, 50.0));
            winArray[i].setText(getString(R.string.two_decimal_perc, 47.97));
            tieArray[i].setText(getString(R.string.two_decimal_perc, 2.03));
            handStats[i][0].setText(getString(R.string.two_decimal_perc, 17.41));
            handStats[i][1].setText(getString(R.string.two_decimal_perc, 43.82));
            handStats[i][2].setText(getString(R.string.two_decimal_perc, 23.5));
            handStats[i][3].setText(getString(R.string.two_decimal_perc, 4.83));
            handStats[i][4].setText(getString(R.string.two_decimal_perc, 4.62));
            handStats[i][5].setText(getString(R.string.two_decimal_perc, 3.03));
            handStats[i][6].setText(getString(R.string.two_decimal_perc, 2.6));
            handStats[i][7].setText(getString(R.string.two_decimal_perc, 0.17));
            handStats[i][8].setText(getString(R.string.two_decimal_perc, 0.03));
        }

        equityCalculatorBinding.title.setText(getString(R.string.texas_hold_em_equity_calculator));

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

        rangeSelectorBinding.addRangeButton.setOnClickListener(v -> {
            AddPresetHandRangeFragment dialog = AddPresetHandRangeFragment.newInstance(new ArrayList<>(presetHandRangeMap.keySet()));
            dialog.show(getParentFragmentManager(), "ADD_PRESET_HAND_RANGE_DIALOG");
        });

        setFragmentResultListeners();

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

    public void setFragmentResultListeners() {
        requireActivity().getSupportFragmentManager().setFragmentResultListener("add_preset_hand_range", getViewLifecycleOwner(), (requestKey, result) -> {
            String rangeName = (String) result.get("range_name");

            writeToDataStore(
                    ((MainActivity) requireActivity()).dataStore,
                    PreferencesKeys.stringKey("thec_" + rangeName),
                    gson.toJson(this.matrixInput)
            );

            Preferences.Key<String> ALL_NAMES_KEY = PreferencesKeys.stringKey("texas_holdem_equity_calculator_range_names");

            String rangeNamesJson = getDataFromDataStoreIfExist(((MainActivity) requireActivity()).dataStore, ALL_NAMES_KEY);

            if (rangeNamesJson == null) {
                writeToDataStore(
                        ((MainActivity) requireActivity()).dataStore,
                        ALL_NAMES_KEY,
                        gson.toJson(Collections.singletonList(rangeName))
                );
            } else {
                List<String> rangeNameList = gson.fromJson(rangeNamesJson, new TypeToken<List<String>>(){}.getType());
                rangeNameList.add(rangeName);
                writeToDataStore(((MainActivity) requireActivity()).dataStore, ALL_NAMES_KEY, gson.toJson(rangeNameList));
            }

            appendPresetRangeButton(rangeName);
        });

        requireActivity().getSupportFragmentManager().setFragmentResultListener("rename_preset_hand_range", getViewLifecycleOwner(), (requestKey, result) -> {
            String oldRangeName = (String) result.get("old_range_name");
            String newRangeName = (String) result.get("new_range_name");

            Preferences.Key<String> OLD_RANGE_NAME_KEY = PreferencesKeys.stringKey("thec_" + oldRangeName);
            Preferences.Key<String> NEW_RANGE_NAME_KEY = PreferencesKeys.stringKey("thec_" + newRangeName);

            String matrixJson = ((MainActivity) requireActivity()).dataStore.data().map(prefs -> prefs.get(OLD_RANGE_NAME_KEY)).blockingFirst();

            deleteKeyFromDataStore(((MainActivity) requireActivity()).dataStore, OLD_RANGE_NAME_KEY);

            writeToDataStore(
                    ((MainActivity) requireActivity()).dataStore,
                    NEW_RANGE_NAME_KEY,
                    matrixJson
            );

            Preferences.Key<String> ALL_NAMES_KEY = PreferencesKeys.stringKey("texas_holdem_equity_calculator_range_names");

            String rangeNamesJson = ((MainActivity) requireActivity()).dataStore.data().map(prefs -> prefs.get(ALL_NAMES_KEY)).blockingFirst();

            List<String> rangeNameList = gson.fromJson(rangeNamesJson, new TypeToken<List<String>>(){}.getType());
            assert rangeNameList != null;
            Collections.replaceAll(rangeNameList, oldRangeName, newRangeName);
            writeToDataStore(((MainActivity) requireActivity()).dataStore, ALL_NAMES_KEY, gson.toJson(rangeNameList));

            MaterialButton presetHandRangeButton = presetHandRangeMap.get(oldRangeName);
            assert presetHandRangeButton != null;
            presetHandRangeButton.setText(newRangeName);

            presetHandRangeMap.remove(oldRangeName);
            presetHandRangeMap.put(newRangeName, presetHandRangeButton);
        });

        requireActivity().getSupportFragmentManager().setFragmentResultListener("delete_preset_hand_range", getViewLifecycleOwner(), (requestKey, result) -> {
            String rangeName = (String) result.get("range_name");

            ((MainActivity) requireActivity()).dataStore.updateDataAsync(prefsIn -> {
                MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
                mutablePreferences.remove(PreferencesKeys.stringKey("thec_" + rangeName));
                return Single.just(mutablePreferences);
            });

            Preferences.Key<String> ALL_NAMES_KEY = PreferencesKeys.stringKey("texas_holdem_equity_calculator_range_names");

            String rangeNamesJson = ((MainActivity) requireActivity()).dataStore.data().map(prefs -> prefs.get(ALL_NAMES_KEY)).blockingFirst();

            List<String> rangeNameList = gson.fromJson(rangeNamesJson, new TypeToken<List<String>>(){}.getType());
            assert rangeNameList != null;
            rangeNameList.remove(rangeName);
            writeToDataStore(((MainActivity) requireActivity()).dataStore, ALL_NAMES_KEY, gson.toJson(rangeNameList));

            MaterialButton presetHandRangeButton = presetHandRangeMap.get(rangeName);
            rangeSelectorBinding.presetHandRangeLayout.removeView(presetHandRangeButton);
            rangeSelectorBinding.presetHandRangeFlow.removeView(presetHandRangeButton);

            presetHandRangeMap.remove(rangeName);

            if (presetHandRangeMap.isEmpty()) {
                rangeSelectorBinding.presetHandRangeInitialText.setVisibility(View.VISIBLE);
            }
        });
    }

    public void appendPresetRangeButton(String rangeName) {
        MaterialButton b = new MaterialButton(requireActivity());
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
            String matrixJson = ((MainActivity) requireActivity()).dataStore.data().map(prefs -> prefs.get(RANGE_NAME_KEY)).blockingFirst();
            List<List<Set<String>>> savedMatrix = gson.fromJson(matrixJson, new TypeToken<List<List<Set<String>>>>(){}.getType());

            updateRangeSelector(savedMatrix);
        });

        b.setOnLongClickListener(v -> {
            EditPresetHandRangeFragment dialog = EditPresetHandRangeFragment.newInstance((String) b.getText(), new ArrayList<>(presetHandRangeMap.keySet()));
            dialog.show(getParentFragmentManager(), "EDIT_PRESET_HAND_RANGE_DIALOG");
            return true;
        });

        presetHandRangeMap.put(rangeName, b);

        rangeSelectorBinding.presetHandRangeInitialText.setVisibility(View.GONE);
        rangeSelectorBinding.presetHandRangeLayout.addView(b);
        rangeSelectorBinding.presetHandRangeFlow.addView(b);
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

    public void initialiseVariables() {
        super.initialiseVariables();

        cardsPerHand = 2;
        fragmentName = "TexasHoldem";
    }

    public void initialiseTexasHoldemVariables() {
        suitedButtonSuitsMap.put(rangeSelectorBinding.suits3, Arrays.asList(1, 1));
        suitedButtonSuitsMap.put(rangeSelectorBinding.suits4, Arrays.asList(2, 2));
        suitedButtonSuitsMap.put(rangeSelectorBinding.suits9, Arrays.asList(3, 3));
        suitedButtonSuitsMap.put(rangeSelectorBinding.suits10, Arrays.asList(4, 4));

        pairButtonSuitsMap.put(rangeSelectorBinding.suits1, Arrays.asList(1, 2));
        pairButtonSuitsMap.put(rangeSelectorBinding.suits2, Arrays.asList(1, 3));
        pairButtonSuitsMap.put(rangeSelectorBinding.suits3, Arrays.asList(1, 4));
        pairButtonSuitsMap.put(rangeSelectorBinding.suits4, Arrays.asList(2, 3));
        pairButtonSuitsMap.put(rangeSelectorBinding.suits5, Arrays.asList(2, 4));
        pairButtonSuitsMap.put(rangeSelectorBinding.suits6, Arrays.asList(3, 4));

        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits1, Arrays.asList(1, 2));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits2, Arrays.asList(1, 3));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits3, Arrays.asList(1, 4));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits4, Arrays.asList(2, 3));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits5, Arrays.asList(2, 4));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits6, Arrays.asList(3, 4));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits7, Arrays.asList(2, 1));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits8, Arrays.asList(3, 1));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits9, Arrays.asList(4, 1));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits10, Arrays.asList(3, 2));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits11, Arrays.asList(4, 2));
        offsuitButtonSuitsMap.put(rangeSelectorBinding.suits12, Arrays.asList(4, 3));
    }

    public void generateMainLayout() {
        super.generateMainLayout();

        for (int i = 0; i < 10; i++) {
            TexasHoldemPlayerRowBinding bindingPlayerRow = TexasHoldemPlayerRowBinding.inflate(LayoutInflater.from(requireActivity()), equityCalculatorBinding.playerRows, true);
            player_row_array[i] = bindingPlayerRow.getRoot();
            equityArray[i] = bindingPlayerRow.equity;
            winArray[i] = bindingPlayerRow.win;
            tieArray[i] = bindingPlayerRow.tie;
            handStats[i][0] = bindingPlayerRow.highCard;
            handStats[i][1] = bindingPlayerRow.onePair;
            handStats[i][2] = bindingPlayerRow.twoPair;
            handStats[i][3] = bindingPlayerRow.threeOfAKind;
            handStats[i][4] = bindingPlayerRow.straight;
            handStats[i][5] = bindingPlayerRow.flush;
            handStats[i][6] = bindingPlayerRow.fullHouse;
            handStats[i][7] = bindingPlayerRow.fourOfAKind;
            handStats[i][8] = bindingPlayerRow.straightFlush;
            rangePositionBiMap.put(i + 1, bindingPlayerRow.range);
            twoCardsLayouts[i] = bindingPlayerRow.twoCards;
            cardPositionBiMap.put(Arrays.asList(i + 1, 0), bindingPlayerRow.card1);
            cardPositionBiMap.put(Arrays.asList(i + 1, 1), bindingPlayerRow.card2);
            removeRowMap.put(bindingPlayerRow.remove, i + 1);
            rangeSwitchRowMap.put(bindingPlayerRow.handRangeButton, i + 1);
            statsButtonMap.put(bindingPlayerRow.statsButton, bindingPlayerRow.statsView);

            bindingPlayerRow.playerText.setText(getString(R.string.player, i + 1));
            bindingPlayerRow.remove.setOnClickListener(removePlayerListener);
            bindingPlayerRow.handRangeButton.setOnClickListener(rangeSwitchListener);
            bindingPlayerRow.statsButton.setOnClickListener(statsButtonListener);
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

        String rangeNamesJson = getDataFromDataStoreIfExist(((MainActivity) requireActivity()).dataStore, PreferencesKeys.stringKey("texas_holdem_equity_calculator_range_names"));

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

    private final View.OnClickListener statsButtonListener = v -> {
        final MaterialButton statsButtonInput = (MaterialButton) v;
        LinearLayout statsView = statsButtonMap.get(statsButtonInput);

        assert statsView != null;
        if (statsView.getVisibility() == View.VISIBLE) {
            statsView.setVisibility(View.GONE);
        } else {
            statsView.setVisibility(View.VISIBLE);
        }
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

        updateRangeSelector(rangeRow.matrix);
        
        equityCalculatorBinding.mainUi.setVisibility(View.GONE);
        rangeSelectorBinding.rangeSelector.setVisibility(View.VISIBLE);
    };

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

    public boolean checkAdditionalButtonsToHideCardSelector(MotionEvent event) {
        Rect outRect = new Rect();
        for (Button b : statsButtonMap.keySet()) {
            b.getGlobalVisibleRect(outRect);
            if (outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                return false;
            }
        }

        return true;
    }

    public void clearNumbers() {
        super.clearNumbers();
        for(int i = 0; i < playersRemainingNo; i++) {
            for(int j = 0; j < 9; j++) {
                handStats[i][j].setText("");
            }
        }
    }
}