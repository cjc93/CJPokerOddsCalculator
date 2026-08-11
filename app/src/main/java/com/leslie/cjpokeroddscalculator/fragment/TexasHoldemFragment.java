package com.leslie.cjpokeroddscalculator.fragment;

import static com.leslie.cjpokeroddscalculator.util.AndroidStatic.dpToPx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.leslie.cjpokeroddscalculator.util.GlobalStatic;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.adapter.PlayerAdapter;
import com.leslie.cjpokeroddscalculator.adapter.PlayerViewHolder;
import com.leslie.cjpokeroddscalculator.adapter.TexasHoldemPlayerViewHolder;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.RangeRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.databinding.TexasHoldemPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.viewmodel.RangeSharedViewModel;
import com.leslie.cjpokeroddscalculator.viewmodel.TexasHoldemViewModel;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TexasHoldemFragment extends EquityCalculatorFragment<TexasHoldemViewModel> {
    public int rangeCardSize;
    private RangeSharedViewModel rangeSharedViewModel;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rangeSharedViewModel = new androidx.lifecycle.ViewModelProvider(requireActivity()).get(RangeSharedViewModel.class);

        if (rangeSharedViewModel.matrix != null) {
            updateRange(rangeSharedViewModel.matrix);
            rangeSharedViewModel.matrix = null;
        }
    }

    @Override
    protected Class<? extends TexasHoldemViewModel> getViewModelClass() {
        return TexasHoldemViewModel.class;
    }

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        viewModel.init(2);

        maxPlayers = 10;
        fragmentName = "TexasHoldem";
        rangeCardSize = Math.min(boardCardMaxHeight, boardCardMaxWidth * 350 / 250) - dpToPx(requireContext(), 10);
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
        List<CardRow> newCardRows = viewModel.getPlayerCardRowsCopy();
        if (newCardRows.get(rowIdx) instanceof SpecificCardsRow) {
            newCardRows.set(rowIdx, new RangeRow(newCardRows.get(rowIdx).isStatsVisible));
            viewModel.playerCardRows.setValue(newCardRows);
            viewModel.setSelectedCardPosition(null, null);
            calculateOdds();
            onShowRangeSelector(rowIdx);
        } else {
            newCardRows.set(rowIdx, new SpecificCardsRow(newCardRows.get(rowIdx).isStatsVisible, viewModel.cardsPerHand));
            viewModel.playerCardRows.setValue(newCardRows);
            viewModel.setSelectedCardPosition(rowIdx, 0);
            calculateOdds();
        }
    }

    @Override
    public void onShowRangeSelector(int rowIdx) {
        viewModel.savedStateHandle.set("selectedRangePosition", rowIdx);

        RangeRow rangeRow = (RangeRow) Objects.requireNonNull(viewModel.playerCardRows.getValue()).get(rowIdx);

        rangeSharedViewModel.matrix = GlobalStatic.copyMatrix(rangeRow.matrix);

        NavHostFragment.findNavController(this).navigate(R.id.action_TexasHoldemFragment_to_RangeSelectorFragment);
    }

    public void updateRange(List<List<Set<String>>> matrixInput) {
        List<CardRow> newCardRows = viewModel.getPlayerCardRowsCopy();

        Integer selectedRangePosition = viewModel.savedStateHandle.get("selectedRangePosition");
        assert selectedRangePosition != null;
        RangeRow rangeRow = (RangeRow) newCardRows.get(selectedRangePosition);
        rangeRow.matrix = GlobalStatic.copyMatrix(matrixInput);

        viewModel.playerCardRows.setValue(newCardRows);

        calculateOdds();

        viewModel.savedStateHandle.set("selectedRangePosition", null);
    }
}