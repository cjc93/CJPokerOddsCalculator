package com.leslie.cjpokeroddscalculator.fragment;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;
import com.leslie.cjpokeroddscalculator.viewmodel.OmahaHiLo5ViewModel;

public class OmahaHiLo5Fragment extends OmahaHiLoFragment {

    @Override
    protected Class<? extends EquityCalculatorViewModel> getViewModelClass() {
        return OmahaHiLo5ViewModel.class;
    }

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        maxPlayers = 9;
        this.playerCardMaxWidth = (int) (displayMetrics.widthPixels * 0.16);
        fragmentName = "OmahaHiLo5";
        fragmentId = R.id.OmahaHiLo5Fragment;
        homeButtonActionId = R.id.action_OmahaHiLo5Fragment_to_HomeFragment;
        titleTextId = R.string.omaha_high_low_5_equity_calculator;
    }
}