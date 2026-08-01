package com.leslie.cjpokeroddscalculator.fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.leslie.cjpokeroddscalculator.adapter.OmahaHighPlayerViewHolder;
import com.leslie.cjpokeroddscalculator.adapter.PlayerAdapter;
import com.leslie.cjpokeroddscalculator.adapter.PlayerViewHolder;
import com.leslie.cjpokeroddscalculator.databinding.OmahaHighPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.viewmodel.OmahaHighViewModel;

public class OmahaHighFragment extends EquityCalculatorFragment<OmahaHighViewModel> {

    public int playerCardMaxWidth;

    @Override
    protected Class<? extends OmahaHighViewModel> getViewModelClass() {
        return OmahaHighViewModel.class;
    }

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        assert getArguments() != null;
        viewModel.init(getArguments().getInt("cardsPerHand"));

        if (viewModel.cardsPerHand == 5) {
            maxPlayers = 9;
            this.playerCardMaxWidth = (int) (displayMetrics.widthPixels * 0.16);
            fragmentName = "OmahaHigh5";
        } else if (viewModel.cardsPerHand == 6) {
            maxPlayers = 7;
            this.playerCardMaxWidth = (int) (displayMetrics.widthPixels * 0.8 / 6.0);
            fragmentName = "OmahaHigh6";
        } else {
            maxPlayers = 10;
            this.playerCardMaxWidth = this.boardCardMaxWidth;
            fragmentName = "OmahaHigh";
        }
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