package com.leslie.cjpokeroddscalculator.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;

import com.google.android.material.button.MaterialButton;
import com.leslie.cjpokeroddscalculator.GlobalStatic;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
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
import java.util.List;
import java.util.Set;

public class TexasHoldemFragment extends EquityCalculatorFragment {
    public Integer selectedRangePosition;

    public List<Group> twoCardsGroups = new ArrayList<>();

    public List<ImageButton> rangeButtonList = new ArrayList<>();

    List<MaterialButton> handRangeSwitchList = new ArrayList<>();

    public Bitmap emptyRangeBitmap;

    public RangeSelector rangeSelector;

    int rangeCardApproxSize;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rangeSelector = new RangeSelector(this);

        rangeSelector.rangeSelectorBinding = RangeSelectorBinding.inflate(LayoutInflater.from(requireActivity()), equityCalculatorBinding.fullscreenUi, true);

        rangeSelector.initialiseVariables();
        rangeSelector.addBackPressedCallback();
        rangeSelector.generateRangeSelector();
        rangeSelector.setListeners();
        rangeSelector.setFragmentResultListeners();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rangeSelector.rangeSelectorBinding = null;
    }

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        cardsPerHand = 2;
        maxPlayers = 10;
        fragmentName = "TexasHoldem";
        fragmentId = R.id.TexasHoldemFragment;
        homeButtonActionId = R.id.action_TexasHoldemFragment_to_HomeFragment;
        rangeCardApproxSize = Math.min(boardCardMaxHeight, boardCardMaxWidth * 350 / 250);
        initialStats = new double[]{
            50.0,
            47.97,
            4.07,
            17.41,
            43.82,
            23.5,
            4.83,
            4.62,
            3.03,
            2.6,
            0.17,
            0.03
        };
        titleTextId = R.string.texas_hold_em_equity_calculator;
    }

    @Override
    public void generateMainLayout() {
        super.generateMainLayout();

        this.emptyRangeBitmap = Bitmap.createBitmap(rangeCardApproxSize, rangeCardApproxSize, Bitmap.Config.ARGB_8888);
        this.emptyRangeBitmap.eraseColor(Color.LTGRAY);
    }

    @Override
    public void addPlayerRow() {
        TexasHoldemPlayerRowBinding bindingPlayerRow = TexasHoldemPlayerRowBinding.inflate(LayoutInflater.from(requireActivity()), equityCalculatorBinding.playerRows, true);

        List<ImageButton> cardList = Arrays.asList(
            bindingPlayerRow.card1,
            bindingPlayerRow.card2
        );

        setRowViews(bindingPlayerRow.getRoot(), bindingPlayerRow.playerText, cardList, boardCardMaxWidth, bindingPlayerRow.remove, bindingPlayerRow.statsButton, bindingPlayerRow.statsView.getRoot());

        addToStatsMatrix(
            bindingPlayerRow.equity,
            bindingPlayerRow.win,
            bindingPlayerRow.tie,
            bindingPlayerRow.statsView.highCard,
            bindingPlayerRow.statsView.onePair,
            bindingPlayerRow.statsView.twoPair,
            bindingPlayerRow.statsView.threeOfAKind,
            bindingPlayerRow.statsView.straight,
            bindingPlayerRow.statsView.flush,
            bindingPlayerRow.statsView.fullHouse,
            bindingPlayerRow.statsView.fourOfAKind,
            bindingPlayerRow.statsView.straightFlush
        );

        twoCardsGroups.add(bindingPlayerRow.twoCards);

        ImageButton rangeButton = bindingPlayerRow.range;

        rangeButton.setOnClickListener(v -> {
            ImageButton rangeSelectorInput = (ImageButton) v;
            showRangeSelector(rangeButtonList.indexOf(rangeSelectorInput) + 1);
        });

        rangeButtonList.add(rangeButton);

        handRangeSwitchList.add(bindingPlayerRow.handRangeButton);

        bindingPlayerRow.handRangeButton.setOnClickListener(v -> {
            final MaterialButton rangeSwitchInput = (MaterialButton) v;
            int playerRangeSwitchNumber = handRangeSwitchList.indexOf(rangeSwitchInput) + 1;

            if (cardRows.get(playerRangeSwitchNumber) instanceof SpecificCardsRow) {
                for (int i = 0; i < 2; i++) {
                    setInputCardVisible(playerRangeSwitchNumber, i);
                }

                twoCardsGroups.get(playerRangeSwitchNumber - 1).setVisibility(View.GONE);
                cardRows.set(playerRangeSwitchNumber, new RangeRow());
                ImageButton b = this.rangeButtonList.get(playerRangeSwitchNumber - 1);
                b.setMaxHeight(cardButtonListOfLists.get(playerRangeSwitchNumber).get(0).getHeight());
                b.setImageBitmap(this.emptyRangeBitmap);
                b.setVisibility(View.VISIBLE);
                rangeSwitchInput.setText(R.string.hand);
                showRangeSelector(playerRangeSwitchNumber);
                hideCardSelector();
            } else {
                cardRows.set(playerRangeSwitchNumber, new SpecificCardsRow(cardsPerHand));
                for (int i = 0; i < cardsPerHand; i++) {
                    setCardImage(playerRangeSwitchNumber, i, "");
                }

                rangeButtonList.get(playerRangeSwitchNumber - 1).setVisibility(View.GONE);
                twoCardsGroups.get(playerRangeSwitchNumber - 1).setVisibility(View.VISIBLE);

                rangeSwitchInput.setText(R.string.range);
                setSelectedCard(playerRangeSwitchNumber, 0);
                showCardSelector();
            }

            calculateOdds();
        });
    }

    private void showRangeSelector(int row) {
        selectedRangePosition = row;

        RangeRow rangeRow = (RangeRow) this.cardRows.get(selectedRangePosition);

        rangeSelector.updateRangeSelector(rangeRow.matrix);

        equityCalculatorBinding.mainUi.setVisibility(View.GONE);
        rangeSelector.rangeSelectorBinding.rangeSelector.setVisibility(View.VISIBLE);
    }

    public void updateRange(List<List<Set<String>>> matrixInput) {
        if (selectedRangePosition != null) {
            CardRow cardRow = this.cardRows.get(selectedRangePosition);

            if (cardRow instanceof RangeRow) {
                RangeRow rangeRow = (RangeRow) cardRow;

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

                ImageButton rangeButton = rangeButtonList.get(selectedRangePosition - 1);
                int h = cardButtonListOfLists.get(selectedRangePosition).get(0).getHeight();
                rangeButton.setImageBitmap(Bitmap.createScaledBitmap(matrixBitmap, h, h, false));
                matrixBitmap.recycle();

                calculateOdds();
            }
        }
    }

    @Override
    public void removePlayerRow(int playerRemoveNumber) {
        rangeButtonList.remove(playerRemoveNumber - 1);
        twoCardsGroups.remove(playerRemoveNumber - 1);
        handRangeSwitchList.remove(playerRemoveNumber - 1);

        super.removePlayerRow(playerRemoveNumber);
    }

    @Override
    public void monteCarloProc() {
        try {
            TexasHoldemMonteCarloCalc calcObj = new TexasHoldemMonteCarloCalc();
            calcObj.calculate(cardRows, new TexasHoldemLiveUpdate(this));
        } catch (InterruptedException ignored) { }
    }

    @Override
    public void exactCalcProc() {
        try {
            TexasHoldemExactCalc calcObj = new TexasHoldemExactCalc();
            calcObj.calculate(cardRows, new TexasHoldemFinalUpdate(this));
        } catch (InterruptedException ignored) { }
    }
}