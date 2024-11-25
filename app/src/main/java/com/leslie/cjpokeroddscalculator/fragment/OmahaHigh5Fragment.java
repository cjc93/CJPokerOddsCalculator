package com.leslie.cjpokeroddscalculator.fragment;

import com.leslie.cjpokeroddscalculator.R;

public class OmahaHigh5Fragment extends OmahaHighFragment {

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        cardsPerHand = 5;
        maxPlayers = 9;
        this.playerCardMaxWidth = (int) (displayMetrics.widthPixels * 0.16);
        fragmentName = "OmahaHigh5";
        fragmentId = R.id.OmahaHigh5Fragment;
        homeButtonActionId = R.id.action_OmahaHigh5Fragment_to_HomeFragment;
        initialStats = new double[]{
            50.0,
            48.98,
            2.04,
            0.78,
            16.68,
            37.60,
            9.66,
            15.25,
            9.61,
            9.56,
            0.72,
            0.14
        };
        titleTextId = R.string.omaha_high_5_card_equity_calculator;
    }
}