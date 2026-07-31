package com.leslie.cjpokeroddscalculator.fragment;

import static com.leslie.cjpokeroddscalculator.util.AndroidStatic.dpToPx;
import static com.leslie.cjpokeroddscalculator.util.AndroidStatic.navControllerNavigateWithArgs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leslie.cjpokeroddscalculator.util.GlobalStatic;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.adapter.PlayerAdapter;
import com.leslie.cjpokeroddscalculator.adapter.PlayerViewHolder;
import com.leslie.cjpokeroddscalculator.adapter.TexasHoldemPlayerViewHolder;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.RangeRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.databinding.TexasHoldemPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;
import com.leslie.cjpokeroddscalculator.viewmodel.TexasHoldemViewModel;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TexasHoldemFragment extends EquityCalculatorFragment {
    public int rangeCardSize;
    private final Gson gson = new Gson();

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("range_selector_result", getViewLifecycleOwner(), (requestKey, result) -> {
            String matrixJson = result.getString("matrix");
            if (matrixJson != null) {
                List<List<Set<String>>> matrix = gson.fromJson(matrixJson, new TypeToken<List<List<Set<String>>>>(){}.getType());
                updateRange(matrix);
            }
        });
    }

    @Override
    protected Class<? extends EquityCalculatorViewModel> getViewModelClass() {
        return TexasHoldemViewModel.class;
    }

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        viewModel.init(2);

        maxPlayers = 10;
        fragmentName = "TexasHoldem";
        fragmentId = R.id.TexasHoldemFragment;
        homeButtonActionId = R.id.action_TexasHoldemFragment_to_HomeFragment;
        rangeCardSize = Math.min(boardCardMaxHeight, boardCardMaxWidth * 350 / 250) - dpToPx(requireContext(), 10);
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
        TexasHoldemViewModel texasHoldemViewModel = (TexasHoldemViewModel) viewModel;
        texasHoldemViewModel.selectedRangePosition = rowIdx;

        RangeRow rangeRow = (RangeRow) Objects.requireNonNull(viewModel.playerCardRows.getValue()).get(rowIdx);

        Bundle args = new Bundle();
        args.putString("matrix", gson.toJson(rangeRow.matrix));

        navControllerNavigateWithArgs(this, fragmentId, R.id.action_TexasHoldemFragment_to_RangeSelectorFragment, args);
    }

    public void updateRange(List<List<Set<String>>> matrixInput) {
        List<CardRow> newCardRows = viewModel.getPlayerCardRowsCopy();

        TexasHoldemViewModel texasHoldemViewModel = (TexasHoldemViewModel) viewModel;
        Integer selectedRangePosition = texasHoldemViewModel.selectedRangePosition;

        RangeRow rangeRow = (RangeRow) newCardRows.get(selectedRangePosition);
        rangeRow.matrix = GlobalStatic.copyMatrix(matrixInput);

        viewModel.playerCardRows.setValue(newCardRows);

        calculateOdds();

        texasHoldemViewModel.selectedRangePosition = null;
    }
}