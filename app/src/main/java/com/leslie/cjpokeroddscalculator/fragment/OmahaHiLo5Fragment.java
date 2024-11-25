package com.leslie.cjpokeroddscalculator.fragment;

import com.leslie.cjpokeroddscalculator.R;

public class OmahaHiLo5Fragment extends OmahaHiLoFragment {

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        cardsPerHand = 5;
        maxPlayers = 9;
        this.playerCardMaxWidth = (int) (displayMetrics.widthPixels * 0.16);
        fragmentName = "OmahaHiLo5";
        fragmentId = R.id.OmahaHiLo5Fragment;
        homeButtonActionId = R.id.action_OmahaHiLo5Fragment_to_HomeFragment;
        initialStats = new double[]{
            50.0,
            48.98,
            26.44,
            2.03,
            2.63,
            0.78,
            16.68,
            37.62,
            9.65,
            15.25,
            9.61,
            9.56,
            0.72,
            0.14,
            43.02
        };
        titleTextId = R.string.omaha_high_low_5_equity_calculator;
    }
}