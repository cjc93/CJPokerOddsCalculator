package com.leslie.cjpokeroddscalculator.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.calculation.OmahaExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.OmahaMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.databinding.OmahaHighPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaLiveUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;

import java.util.Arrays;
import java.util.List;

public class OmahaHighFragment extends EquityCalculatorFragment {

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        for (int i = 0; i < 2; i++) {
            statsMatrix.get(i).get(0).setText(getString(R.string.two_decimal_perc, 50.0));
            statsMatrix.get(i).get(1).setText(getString(R.string.two_decimal_perc, 49.29));
            statsMatrix.get(i).get(2).setText(getString(R.string.two_decimal_perc, 1.42));

            statsMatrix.get(i).get(3).setText(getString(R.string.two_decimal_perc, 2.99));
            statsMatrix.get(i).get(4).setText(getString(R.string.two_decimal_perc, 26.47));
            statsMatrix.get(i).get(5).setText(getString(R.string.two_decimal_perc, 36.83));
            statsMatrix.get(i).get(6).setText(getString(R.string.two_decimal_perc, 8.79));
            statsMatrix.get(i).get(7).setText(getString(R.string.two_decimal_perc, 11.27));
            statsMatrix.get(i).get(8).setText(getString(R.string.two_decimal_perc, 6.72));
            statsMatrix.get(i).get(9).setText(getString(R.string.two_decimal_perc, 6.35));
            statsMatrix.get(i).get(10).setText(getString(R.string.two_decimal_perc, 0.48));
            statsMatrix.get(i).get(11).setText(getString(R.string.two_decimal_perc, 0.09));
        }

        equityCalculatorBinding.title.setText(getString(R.string.omaha_high_equity_calculator));

        monteCarloProc = () -> {
            try {
                OmahaMonteCarloCalc calcObj = new OmahaMonteCarloCalc();
                OmahaOutputResult omahaOutputResult = new OmahaLiveUpdate(this, calcObj);
                calcObj.setOmahaPokerObj(new OmahaPoker(omahaOutputResult));
                calcObj.calculate(cardRows);
            } catch (InterruptedException ignored) { }
        };

        exactCalcProc = () -> {
            try {
                OmahaExactCalc calcObj = new OmahaExactCalc();
                OmahaOutputResult omahaOutputResult = new OmahaFinalUpdate(this, calcObj);
                calcObj.setOmahaPokerObj(new OmahaPoker(omahaOutputResult));
                calcObj.calculate(cardRows);
            } catch (InterruptedException ignored) { }
        };
    }

    @Override
    public void addPlayerRow() {
        OmahaHighPlayerRowBinding bindingPlayerRow = OmahaHighPlayerRowBinding.inflate(LayoutInflater.from(requireActivity()), equityCalculatorBinding.playerRows, true);

        playerRowList.add(bindingPlayerRow.getRoot());
        statsMatrix.add(
            Arrays.asList(
                bindingPlayerRow.equity,
                bindingPlayerRow.win,
                bindingPlayerRow.tie,
                bindingPlayerRow.statsView.highCard,
                bindingPlayerRow.statsView.onePair,
                bindingPlayerRow.statsView.twoPair,
                bindingPlayerRow.statsView.threeOfAKind,
                bindingPlayerRow.statsView.straight,
                bindingPlayerRow.statsView.flush,
                bindingPlayerRow.statsView.fullHouse,
                bindingPlayerRow.statsView.fourOfAKind,
                bindingPlayerRow.statsView.straightFlush
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

        cardsPerHand = 4;
        fragmentName = "OmahaHigh";
        fragmentId = R.id.OmahaHighFragment;
        homeButtonActionId = R.id.action_OmahaHighFragment_to_HomeFragment;
    }
}