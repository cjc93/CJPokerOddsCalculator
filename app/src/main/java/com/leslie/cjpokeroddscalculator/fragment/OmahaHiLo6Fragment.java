package com.leslie.cjpokeroddscalculator.fragment;

import com.leslie.cjpokeroddscalculator.R;

public class OmahaHiLo6Fragment extends OmahaHiLoFragment {

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        cardsPerHand = 6;
        maxPlayers = 7;
        this.playerCardMaxWidth = (int) (displayMetrics.widthPixels * 0.8 / 6.0);
        fragmentName = "OmahaHiLo6";
        fragmentId = R.id.OmahaHiLo6Fragment;
        homeButtonActionId = R.id.action_OmahaHiLo6Fragment_to_HomeFragment;

        initialStats = new double[]{
            50.0,
            48.62,
            27.25,
            2.75,
            3.72,
            0.14,
            9.79,
            34.85,
            10.27,
            18.55,
            12.25,
            12.92,
            1.01,
            0.22,
            48.89
        };

        titleTextId = R.string.omaha_hi_lo_6_card_equity_calculator;
    }
}