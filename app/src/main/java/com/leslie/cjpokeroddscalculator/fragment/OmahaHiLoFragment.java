package com.leslie.cjpokeroddscalculator.fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.leslie.cjpokeroddscalculator.adapter.OmahaHiLoPlayerViewHolder;
import com.leslie.cjpokeroddscalculator.adapter.PlayerAdapter;
import com.leslie.cjpokeroddscalculator.adapter.PlayerViewHolder;
import com.leslie.cjpokeroddscalculator.databinding.OmahaHiloPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.viewmodel.OmahaHiLoViewModel;
import com.leslie.cjpokeroddscalculator.viewmodel.OmahaHighViewModel;

public class OmahaHiLoFragment extends OmahaHighFragment {

    @Override
    protected Class<? extends OmahaHighViewModel> getViewModelClass() {
        return OmahaHiLoViewModel.class;
    }

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        if (viewModel.cardsPerHand == 5) {
            fragmentName = "OmahaHiLo5";
        } else if (viewModel.cardsPerHand == 6) {
            fragmentName = "OmahaHiLo6";
        } else {
            fragmentName = "OmahaHiLo";
        }
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