package com.leslie.cjpokeroddscalculator.fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.adapter.OmahaHiLoPlayerViewHolder;
import com.leslie.cjpokeroddscalculator.adapter.PlayerAdapter;
import com.leslie.cjpokeroddscalculator.adapter.PlayerViewHolder;
import com.leslie.cjpokeroddscalculator.databinding.OmahaHiloPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;
import com.leslie.cjpokeroddscalculator.viewmodel.OmahaHiLoViewModel;

public class OmahaHiLoFragment extends OmahaHighFragment {

    @Override
    protected Class<? extends EquityCalculatorViewModel> getViewModelClass() {
        return OmahaHiLoViewModel.class;
    }

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        fragmentName = "OmahaHiLo";
        fragmentId = R.id.OmahaHiLoFragment;
        homeButtonActionId = R.id.action_OmahaHiLoFragment_to_HomeFragment;
        titleTextId = R.string.omaha_high_low_equity_calculator;
    }

    @Override
    public PlayerAdapter createPlayerAdapter() {
        return new PlayerAdapter(this) {
            @NonNull
            @Override
            public PlayerViewHolder createPlayerViewHolder(ViewGroup parent) {
                OmahaHiloPlayerRowBinding binding = OmahaHiloPlayerRowBinding.inflate(LayoutInflater.from(requireActivity()), parent, false);
                return new OmahaHiLoPlayerViewHolder(binding, listener, boardCardMaxHeight, playerCardMaxWidth, viewModel.cardsPerHand);
            }
        };
    }
}