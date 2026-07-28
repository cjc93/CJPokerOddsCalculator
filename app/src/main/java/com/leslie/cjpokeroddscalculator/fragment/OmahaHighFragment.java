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

        assert getArguments() != null;
        viewModel.init(getArguments().getInt("cardsPerHand"));

        fragmentId = R.id.OmahaHighFragment;
        homeButtonActionId = R.id.action_OmahaHighFragment_to_HomeFragment;

        if (viewModel.cardsPerHand == 5) {
            maxPlayers = 9;
            this.playerCardMaxWidth = (int) (displayMetrics.widthPixels * 0.16);
            fragmentName = "OmahaHigh5";
            titleTextId = R.string.omaha_high_5_card_equity_calculator;
        } else if (viewModel.cardsPerHand == 6) {
            maxPlayers = 7;
            this.playerCardMaxWidth = (int) (displayMetrics.widthPixels * 0.8 / 6.0);
            fragmentName = "OmahaHigh6";
            titleTextId = R.string.omaha_high_6_card_equity_calculator;
        } else {
            maxPlayers = 10;
            this.playerCardMaxWidth = this.boardCardMaxWidth;
            fragmentName = "OmahaHigh";
            titleTextId = R.string.omaha_high_equity_calculator;
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