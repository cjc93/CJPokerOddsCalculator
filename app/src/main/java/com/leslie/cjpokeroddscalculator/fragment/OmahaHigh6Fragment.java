package com.leslie.cjpokeroddscalculator.fragment;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;
import com.leslie.cjpokeroddscalculator.viewmodel.OmahaHigh6ViewModel;

public class OmahaHigh6Fragment extends OmahaHighFragment {

    @Override
    protected Class<? extends EquityCalculatorViewModel> getViewModelClass() {
        return OmahaHigh6ViewModel.class;
    }

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        maxPlayers = 7;
        this.playerCardMaxWidth = (int) (displayMetrics.widthPixels * 0.8 / 6.0);
        fragmentName = "OmahaHigh6";
        fragmentId = R.id.OmahaHigh6Fragment;
        homeButtonActionId = R.id.action_OmahaHigh6Fragment_to_HomeFragment;
        titleTextId = R.string.omaha_high_6_card_equity_calculator;
    }
}