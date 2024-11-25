package com.leslie.cjpokeroddscalculator.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.calculation.OmahaExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.OmahaMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaHiLoPoker;
import com.leslie.cjpokeroddscalculator.databinding.OmahaHiloPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaLiveUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;

import java.util.Arrays;
import java.util.List;

public class OmahaHiLoFragment extends OmahaHighFragment {

    @Override
    public void addPlayerRow() {
        OmahaHiloPlayerRowBinding bindingPlayerRow = OmahaHiloPlayerRowBinding.inflate(LayoutInflater.from(requireActivity()), equityCalculatorBinding.playerRows, true);

        List<ImageButton> cardList = createCardButtons(bindingPlayerRow.getRoot(), bindingPlayerRow.playerText, bindingPlayerRow.statsButton);

        setRowViews(bindingPlayerRow.getRoot(), bindingPlayerRow.playerText, cardList, this.playerCardMaxWidth, bindingPlayerRow.remove, bindingPlayerRow.statsButton, bindingPlayerRow.statsView.getRoot());

        TextView lowText = new TextView(requireActivity(), null, 0, R.style.StatsText);
        lowText.setId(View.generateViewId());
        lowText.setText(R.string.low);

        TextView lowPercent = new TextView(requireActivity(), null, 0, R.style.StatsText);
        lowPercent.setId(View.generateViewId());

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.topToBottom = bindingPlayerRow.statsView.straightFlushText.getId();
        layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;
        layoutParams.leftToRight = bindingPlayerRow.statsView.guideline3.getId();
        layoutParams.rightToLeft = lowPercent.getId();
        layoutParams.horizontalChainStyle = ConstraintLayout.LayoutParams.CHAIN_SPREAD_INSIDE;

        lowText.setLayoutParams(layoutParams);

        bindingPlayerRow.statsView.getRoot().addView(lowText);

        layoutParams = new ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.topToBottom = bindingPlayerRow.statsView.straightFlush.getId();
        layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;
        layoutParams.leftToRight = lowText.getId();
        layoutParams.rightToLeft = bindingPlayerRow.statsView.guideline4.getId();
        layoutParams.horizontalChainStyle = ConstraintLayout.LayoutParams.CHAIN_SPREAD_INSIDE;

        lowPercent.setLayoutParams(layoutParams);

        bindingPlayerRow.statsView.getRoot().addView(lowPercent);

        statsMatrix.add(
            Arrays.asList(
                bindingPlayerRow.equity,
                bindingPlayerRow.win,
                bindingPlayerRow.winLow,
                bindingPlayerRow.tie,
                bindingPlayerRow.tieLow,
                bindingPlayerRow.statsView.highCard,
                bindingPlayerRow.statsView.onePair,
                bindingPlayerRow.statsView.twoPair,
                bindingPlayerRow.statsView.threeOfAKind,
                bindingPlayerRow.statsView.straight,
                bindingPlayerRow.statsView.flush,
                bindingPlayerRow.statsView.fullHouse,
                bindingPlayerRow.statsView.fourOfAKind,
                bindingPlayerRow.statsView.straightFlush,
                lowPercent
            )
        );
    }
    
    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        fragmentName = "OmahaHiLo";
        fragmentId = R.id.OmahaHiLoFragment;
        homeButtonActionId = R.id.action_OmahaHiLoFragment_to_HomeFragment;
        initialStats = new double[]{
            50.0,
            49.29,
            23.98,
            1.42,
            1.68,
            2.98,
            26.45,
            36.86,
            8.79,
            11.28,
            6.73,
            6.35,
            0.48,
            0.09,
            34.81
        };
        titleTextId = R.string.omaha_high_low_equity_calculator;
    }

    @Override
    public void monteCarloProc() {
        try {
            OmahaMonteCarloCalc calcObj = new OmahaMonteCarloCalc(this.cardsPerHand);
            OmahaOutputResult omahaOutputResult = new OmahaLiveUpdate(this, calcObj);
            calcObj.setOmahaPokerObj(new OmahaHiLoPoker(omahaOutputResult));
            calcObj.calculate(cardRows);
        } catch (InterruptedException ignored) { }
    }

    @Override
    public void exactCalcProc() {
        try {
            OmahaExactCalc calcObj = new OmahaExactCalc(this.cardsPerHand);
            OmahaOutputResult omahaOutputResult = new OmahaFinalUpdate(this, calcObj);
            calcObj.setOmahaPokerObj(new OmahaHiLoPoker(omahaOutputResult));
            calcObj.calculate(cardRows);
        } catch (InterruptedException ignored) { }
    }
}