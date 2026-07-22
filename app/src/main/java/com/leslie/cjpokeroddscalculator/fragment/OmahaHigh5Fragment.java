package com.leslie.cjpokeroddscalculator.fragment;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;
import com.leslie.cjpokeroddscalculator.viewmodel.OmahaHigh5ViewModel;

public class OmahaHigh5Fragment extends OmahaHighFragment {

    @Override
    protected Class<? extends EquityCalculatorViewModel> getViewModelClass() {
        return OmahaHigh5ViewModel.class;
    }

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        cardsPerHand = 5;
        maxPlayers = 9;
        this.playerCardMaxWidth = (int) (displayMetrics.widthPixels * 0.16);
        fragmentName = "OmahaHigh5";
        fragmentId = R.id.OmahaHigh5Fragment;
        homeButtonActionId = R.id.action_OmahaHigh5Fragment_to_HomeFragment;
        titleTextId = R.string.omaha_high_5_card_equity_calculator;
    }
}