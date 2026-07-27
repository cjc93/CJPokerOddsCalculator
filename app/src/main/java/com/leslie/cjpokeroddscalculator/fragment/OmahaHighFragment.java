package com.leslie.cjpokeroddscalculator.fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.adapter.OmahaHighPlayerViewHolder;
import com.leslie.cjpokeroddscalculator.adapter.PlayerAdapter;
import com.leslie.cjpokeroddscalculator.adapter.PlayerViewHolder;
import com.leslie.cjpokeroddscalculator.databinding.OmahaHighPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;
import com.leslie.cjpokeroddscalculator.viewmodel.OmahaHighViewModel;

public class OmahaHighFragment extends EquityCalculatorFragment {

    public int playerCardMaxWidth;

    @Override
    protected Class<? extends EquityCalculatorViewModel> getViewModelClass() {
        return OmahaHighViewModel.class;
    }

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        maxPlayers = 10;
        this.playerCardMaxWidth = this.boardCardMaxWidth;
        fragmentName = "OmahaHigh";
        fragmentId = R.id.OmahaHighFragment;
        homeButtonActionId = R.id.action_OmahaHighFragment_to_HomeFragment;
        titleTextId = R.string.omaha_high_equity_calculator;
    }

    @Override
    public PlayerAdapter createPlayerAdapter() {
        return new PlayerAdapter(this) {
            @NonNull
            @Override
            public PlayerViewHolder createPlayerViewHolder(ViewGroup parent) {
                OmahaHighPlayerRowBinding binding = OmahaHighPlayerRowBinding.inflate(LayoutInflater.from(requireActivity()), parent, false);
                return new OmahaHighPlayerViewHolder(binding, listener, boardCardMaxHeight, playerCardMaxWidth, viewModel.cardsPerHand);
            }
        };
    }
}