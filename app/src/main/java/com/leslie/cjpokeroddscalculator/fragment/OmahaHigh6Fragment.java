package com.leslie.cjpokeroddscalculator.fragment;

import com.leslie.cjpokeroddscalculator.R;

public class OmahaHigh6Fragment extends OmahaHighFragment {

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        cardsPerHand = 6;
        maxPlayers = 7;
        this.playerCardMaxWidth = (int) (displayMetrics.widthPixels * 0.8 / 6.0);
        fragmentName = "OmahaHigh6";
        fragmentId = R.id.OmahaHigh6Fragment;
        homeButtonActionId = R.id.action_OmahaHigh6Fragment_to_HomeFragment;

        initialStats = new double[]{
            50.0,
            48.64,
            2.73,
            0.14,
            9.78,
            34.79,
            10.32,
            18.60,
            12.24,
            12.89,
            1.01,
            0.22
        };

        titleTextId = R.string.omaha_high_6_card_equity_calculator;
    }
}