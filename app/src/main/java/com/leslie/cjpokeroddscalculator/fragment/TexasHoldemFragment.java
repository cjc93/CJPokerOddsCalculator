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
import com.leslie.cjpokeroddscalculator.databinding.RangeSelectorBinding;
import com.leslie.cjpokeroddscalculator.databinding.TexasHoldemPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;
import com.leslie.cjpokeroddscalculator.viewmodel.TexasHoldemViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TexasHoldemFragment extends EquityCalculatorFragment {
    public List<Group> twoCardsGroups = new ArrayList<>();

    public List<ImageButton> rangeButtonList = new ArrayList<>();

    List<MaterialButton> handRangeSwitchList = new ArrayList<>();

    public RangeSelector rangeSelector;

    int rangeCardSize;

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
    public void observeLiveData() {
        super.observeLiveData();

        TexasHoldemViewModel texasHoldemViewModel = (TexasHoldemViewModel) viewModel;

        texasHoldemViewModel.selectedRangePosition.observe(getViewLifecycleOwner(), selectedRangePosition -> {
            if (selectedRangePosition == null) {
                rangeSelector.rangeSelectorBinding.rangeSelector.setVisibility(View.GONE);
                equityCalculatorBinding.mainUi.setVisibility(View.VISIBLE);
            } else {
                RangeRow rangeRow = (RangeRow) Objects.requireNonNull(texasHoldemViewModel.cardRows.getValue()).get(selectedRangePosition);

                rangeSelector.updateRangeSelector(rangeRow.matrix);

                equityCalculatorBinding.mainUi.setVisibility(View.GONE);
                rangeSelector.rangeSelectorBinding.rangeSelector.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rangeSelector.rangeSelectorBinding = null;
    }

    @Override
    protected Class<? extends EquityCalculatorViewModel> getViewModelClass() {
        return TexasHoldemViewModel.class;
    }

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        maxPlayers = 10;
        fragmentName = "TexasHoldem";
        fragmentId = R.id.TexasHoldemFragment;
        homeButtonActionId = R.id.action_TexasHoldemFragment_to_HomeFragment;
        rangeCardSize = Math.min(boardCardMaxHeight, boardCardMaxWidth * 350 / 250) - (int) (10 * getResources().getDisplayMetrics().density);
        titleTextId = R.string.texas_hold_em_equity_calculator;
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
            TexasHoldemViewModel texasHoldemViewModel = (TexasHoldemViewModel) viewModel;
            texasHoldemViewModel.selectedRangePosition.setValue(rangeButtonList.indexOf(rangeSelectorInput) + 1);
        });

        rangeButtonList.add(rangeButton);

        handRangeSwitchList.add(bindingPlayerRow.handRangeButton);

        bindingPlayerRow.handRangeButton.setOnClickListener(v -> {
            final MaterialButton rangeSwitchInput = (MaterialButton) v;
            int playerRangeSwitchNumber = handRangeSwitchList.indexOf(rangeSwitchInput) + 1;

            List<CardRow> cardRows = viewModel.cardRows.getValue();
            assert cardRows != null;
            if (cardRows.get(playerRangeSwitchNumber) instanceof SpecificCardsRow) {
                cardRows.set(playerRangeSwitchNumber, new RangeRow(null, cardRows.get(playerRangeSwitchNumber).isStatsVisible));
                TexasHoldemViewModel texasHoldemViewModel = (TexasHoldemViewModel) viewModel;
                texasHoldemViewModel.selectedRangePosition.setValue(playerRangeSwitchNumber);
                viewModel.cardRows.setValue(cardRows);
                viewModel.selectedCard.setValue(null);
            } else {
                cardRows.set(playerRangeSwitchNumber, new SpecificCardsRow(null, cardRows.get(playerRangeSwitchNumber).isStatsVisible, viewModel.cardsPerHand));
                viewModel.cardRows.setValue(cardRows);

                viewModel.selectedCard.setValue(new int[]{playerRangeSwitchNumber, 0});
            }

            calculateOdds();
        });
    }

    public void updateRange(List<List<Set<String>>> matrixInput) {
        List<CardRow> cardRows = viewModel.cardRows.getValue();
        assert cardRows != null;
        TexasHoldemViewModel texasHoldemViewModel = (TexasHoldemViewModel) viewModel;
        Integer selectedRangePosition = texasHoldemViewModel.selectedRangePosition.getValue();
        assert selectedRangePosition != null;
        RangeRow rangeRow = (RangeRow) cardRows.get(selectedRangePosition);

        rangeRow.matrix = GlobalStatic.copyMatrix(matrixInput);
        viewModel.cardRows.setValue(cardRows);

        calculateOdds();
    }

    @Override
    public void removeAllPlayerRows() {
        rangeButtonList.clear();
        twoCardsGroups.clear();
        handRangeSwitchList.clear();

        super.removeAllPlayerRows();
    }

    @Override
    public void setViewsFromCardRow(int rowIdx, CardRow cardRow) {
        super.setViewsFromCardRow(rowIdx, cardRow);

        if (cardRow instanceof SpecificCardsRow specificCardRow) {
            setViewsFromSpecificCardRow(rowIdx, specificCardRow);

            twoCardsGroups.get(rowIdx - 1).setVisibility(View.VISIBLE);
            rangeButtonList.get(rowIdx - 1).setVisibility(View.GONE);
            handRangeSwitchList.get(rowIdx - 1).setText(R.string.range);
        } else {
            RangeRow rangeRow = (RangeRow) cardRow;
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

            ImageButton rangeButton = rangeButtonList.get(rowIdx - 1);
            rangeButton.setImageBitmap(Bitmap.createScaledBitmap(matrixBitmap, rangeCardSize, rangeCardSize, false));
            matrixBitmap.recycle();

            twoCardsGroups.get(rowIdx - 1).setVisibility(View.GONE);
            rangeButtonList.get(rowIdx - 1).setVisibility(View.VISIBLE);
            handRangeSwitchList.get(rowIdx - 1).setText(R.string.hand);
        }
    }
}