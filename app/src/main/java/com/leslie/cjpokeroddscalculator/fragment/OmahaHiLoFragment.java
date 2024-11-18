package com.leslie.cjpokeroddscalculator.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.calculation.OmahaExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.OmahaMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaHiLoPoker;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.databinding.OmahaHiloPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaLiveUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;

import java.util.Arrays;
import java.util.List;

public class OmahaHiLoFragment extends OmahaHighFragment {

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        for (int i = 0; i < 2; i++) {
            statsMatrix.get(i).get(0).setText(getString(R.string.two_decimal_perc, 50.0));

            statsMatrix.get(i).get(1).setText(getString(R.string.two_decimal_perc, 49.29));
            statsMatrix.get(i).get(2).setText(getString(R.string.two_decimal_perc, 23.98));

            statsMatrix.get(i).get(3).setText(getString(R.string.two_decimal_perc, 1.42));
            statsMatrix.get(i).get(4).setText(getString(R.string.two_decimal_perc, 1.68));

            statsMatrix.get(i).get(5).setText(getString(R.string.two_decimal_perc, 2.98));
            statsMatrix.get(i).get(6).setText(getString(R.string.two_decimal_perc, 26.45));
            statsMatrix.get(i).get(7).setText(getString(R.string.two_decimal_perc, 36.86));
            statsMatrix.get(i).get(8).setText(getString(R.string.two_decimal_perc, 8.79));
            statsMatrix.get(i).get(9).setText(getString(R.string.two_decimal_perc, 11.28));
            statsMatrix.get(i).get(10).setText(getString(R.string.two_decimal_perc, 6.73));
            statsMatrix.get(i).get(11).setText(getString(R.string.two_decimal_perc, 6.35));
            statsMatrix.get(i).get(12).setText(getString(R.string.two_decimal_perc, 0.48));
            statsMatrix.get(i).get(13).setText(getString(R.string.two_decimal_perc, 0.09));

            statsMatrix.get(i).get(14).setText(getString(R.string.two_decimal_perc, 34.81));
        }

        equityCalculatorBinding.title.setText(getString(R.string.omaha_high_low_equity_calculator));

        monteCarloProc = () -> {
            try {
                OmahaMonteCarloCalc calcObj = new OmahaMonteCarloCalc();
                OmahaOutputResult omahaOutputResult = new OmahaLiveUpdate(this, calcObj);
                calcObj.setOmahaPokerObj(new OmahaHiLoPoker(omahaOutputResult));
                calcObj.calculate(cardRows);
            } catch (InterruptedException ignored) { }
        };

        exactCalcProc = () -> {
            try {
                OmahaExactCalc calcObj = new OmahaExactCalc();
                OmahaOutputResult omahaOutputResult = new OmahaFinalUpdate(this, calcObj);
                calcObj.setOmahaPokerObj(new OmahaHiLoPoker(omahaOutputResult));
                calcObj.calculate(cardRows);
            } catch (InterruptedException ignored) { }
        };
    }

    @Override
    public void addPlayerRow() {
        OmahaHiloPlayerRowBinding bindingPlayerRow = OmahaHiloPlayerRowBinding.inflate(LayoutInflater.from(requireActivity()), equityCalculatorBinding.playerRows, true);

        TextView lowText = new TextView(requireActivity(), null, 0, R.style.StatsText);
        lowText.setId(View.generateViewId());
        lowText.setText(getString(R.string.low));

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

        playerRowList.add(bindingPlayerRow.getRoot());
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

        List<ImageButton> cardList = Arrays.asList(
            bindingPlayerRow.card1,
            bindingPlayerRow.card2,
            bindingPlayerRow.card3,
            bindingPlayerRow.card4
        );

        initialiseCardButtons(cardList);
        cardButtonListOfLists.add(cardList);

        cardRows.add(new SpecificCardsRow(cardsPerHand));

        removeRowList.add(bindingPlayerRow.remove);
        statsButtonMap.put(bindingPlayerRow.statsButton, bindingPlayerRow.statsView.getRoot());

        bindingPlayerRow.playerText.setText(getString(R.string.player, playerRowList.size()));
        bindingPlayerRow.remove.setOnClickListener(removePlayerListener);
        bindingPlayerRow.statsButton.setOnClickListener(statsButtonListener);
    }
    
    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        fragmentName = "OmahaHiLo";
        fragmentId = R.id.OmahaHiLoFragment;
        homeButtonActionId = R.id.action_OmahaHiLoFragment_to_HomeFragment;
    }
}