package com.leslie.cjpokeroddscalculator.fragment;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;
import com.leslie.cjpokeroddscalculator.viewmodel.OmahaHiLo6ViewModel;

public class OmahaHiLo6Fragment extends OmahaHiLoFragment {

    @Override
    protected Class<? extends EquityCalculatorViewModel> getViewModelClass() {
        return OmahaHiLo6ViewModel.class;
    }

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        cardsPerHand = 6;
        maxPlayers = 7;
        this.playerCardMaxWidth = (int) (displayMetrics.widthPixels * 0.8 / 6.0);
        fragmentName = "OmahaHiLo6";
        fragmentId = R.id.OmahaHiLo6Fragment;
        homeButtonActionId = R.id.action_OmahaHiLo6Fragment_to_HomeFragment;
        titleTextId = R.string.omaha_hi_lo_6_card_equity_calculator;
    }
}