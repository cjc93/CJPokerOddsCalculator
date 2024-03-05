package com.leslie.cjpokeroddscalculator.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Group;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.common.collect.HashBiMap;
import com.leslie.cjpokeroddscalculator.GlobalStatic;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.RangeRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
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
    private ImageButton selectedRangeButton;
    private int selectedRangePosition;

    public Group[] twoCardsLayouts = new Group[10];

    public HashBiMap<Integer, ImageButton> rangePositionBiMap = HashBiMap.create();

    Map<MaterialButton, Integer> rangeSwitchRowMap = new HashMap<>();

    public Bitmap emptyRangeBitmap;

    Map<MaterialButton, Group> statsButtonMap = new HashMap<>();

    public TextView[][] handStats = new TextView[10][9];

    public RangeSelector rangeSelector;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rangeSelector = new RangeSelector(this);

        rangeSelector.rangeSelectorBinding = RangeSelectorBinding.inflate(LayoutInflater.from(requireActivity()), equityCalculatorBinding.fullscreenUi, true);

        rangeSelector.initialiseVariables();
        rangeSelector.addBackPressedCallback();
        rangeSelector.generateRangeSelector();
        rangeSelector.setListeners();
        rangeSelector.setFragmentResultListeners();

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

        rangeSelector.rangeSelectorBinding = null;
    }

    public void initialiseVariables() {
        super.initialiseVariables();

        cardsPerHand = 2;
        fragmentName = "TexasHoldem";
    }

    public void generateMainLayout() {
        super.generateMainLayout();

        for (int i = 0; i < 10; i++) {
            TexasHoldemPlayerRowBinding bindingPlayerRow = TexasHoldemPlayerRowBinding.inflate(LayoutInflater.from(requireActivity()), equityCalculatorBinding.playerRows, true);
            ConstraintLayout playerRow = bindingPlayerRow.getRoot();
            playerRow.setId(View.generateViewId());
            player_row_array[i] = playerRow;
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
        Group statsView = statsButtonMap.get(statsButtonInput);

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

        rangeSelector.updateRangeSelector(rangeRow.matrix);
        
        equityCalculatorBinding.mainUi.setVisibility(View.GONE);
        rangeSelector.rangeSelectorBinding.rangeSelector.setVisibility(View.VISIBLE);
    };

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

    public void updateRange(List<List<Set<String>>> matrixInput) {
        RangeRow rangeRow = (RangeRow) this.cardRows[selectedRangePosition];

        rangeRow.matrix = GlobalStatic.copyMatrix(matrixInput);

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

        calculate_odds();
    }
}