package com.leslie.cjpokeroddscalculator.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.leslie.cjpokeroddscalculator.GlobalStatic;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.adapter.PlayerAdapter;
import com.leslie.cjpokeroddscalculator.adapter.PlayerViewHolder;
import com.leslie.cjpokeroddscalculator.adapter.TexasHoldemPlayerViewHolder;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.RangeRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.databinding.RangeSelectorBinding;
import com.leslie.cjpokeroddscalculator.databinding.TexasHoldemPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;
import com.leslie.cjpokeroddscalculator.viewmodel.TexasHoldemViewModel;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TexasHoldemFragment extends EquityCalculatorFragment {
    public RangeSelector rangeSelector;
    public int rangeCardSize;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rangeSelector = new RangeSelector(this);
        rangeSelector.rangeSelectorBinding = RangeSelectorBinding.inflate(LayoutInflater.from(requireActivity()), equityCalculatorBinding.fullscreenUi, true);

        rangeSelector.initialiseVariables();
        rangeSelector.addBackPressedCallback();
        rangeSelector.generateRangeSelector();
        rangeSelector.observeLiveData();
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
    public PlayerAdapter createPlayerAdapter() {
        return new PlayerAdapter(this) {
            @NonNull
            @Override
            public PlayerViewHolder createPlayerViewHolder(ViewGroup parent) {
                TexasHoldemPlayerRowBinding binding = TexasHoldemPlayerRowBinding.inflate(LayoutInflater.from(requireActivity()), parent, false);
                return new TexasHoldemPlayerViewHolder(binding, listener, boardCardMaxHeight, boardCardMaxWidth, rangeCardSize);
            }
        };
    }

    @Override
    public void onToggleRangeHand(int rowIdx) {
        List<CardRow> newCardRows = viewModel.getCardRowsCopy();
        if (newCardRows.get(rowIdx) instanceof SpecificCardsRow) {
            newCardRows.set(rowIdx, new RangeRow(null, newCardRows.get(rowIdx).isStatsVisible));
            viewModel.setSelectedCardPositionInCardRows(newCardRows, null, null);
            viewModel.cardRows.setValue(newCardRows);
            onShowRangeSelector(rowIdx);
        } else {
            newCardRows.set(rowIdx, new SpecificCardsRow(null, newCardRows.get(rowIdx).isStatsVisible, viewModel.cardsPerHand, 0));
            viewModel.setSelectedCardPositionInCardRows(newCardRows, rowIdx, 0);
            viewModel.cardRows.setValue(newCardRows);
        }

        calculateOdds();
    }

    @Override
    public void onShowRangeSelector(int rowIdx) {
        RangeRow rangeRow = (RangeRow) Objects.requireNonNull(viewModel.cardRows.getValue()).get(rowIdx);

        rangeSelector.updateRangeSelector(rangeRow.matrix);

        TexasHoldemViewModel texasHoldemViewModel = (TexasHoldemViewModel) viewModel;
        texasHoldemViewModel.selectedRangePosition.setValue(rowIdx);
    }

    public void updateRange(List<List<Set<String>>> matrixInput) {
        List<CardRow> newCardRows = viewModel.getCardRowsCopy();

        TexasHoldemViewModel texasHoldemViewModel = (TexasHoldemViewModel) viewModel;
        Integer selectedRangePosition = texasHoldemViewModel.selectedRangePosition.getValue();
        assert selectedRangePosition != null;

        RangeRow rangeRow = (RangeRow) newCardRows.get(selectedRangePosition);

        rangeRow.matrix = GlobalStatic.copyMatrix(matrixInput);
        viewModel.cardRows.setValue(newCardRows);

        calculateOdds();
    }
}