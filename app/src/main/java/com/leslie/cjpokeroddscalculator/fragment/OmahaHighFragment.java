package com.leslie.cjpokeroddscalculator.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.button.MaterialButton;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.calculation.OmahaExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.OmahaMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;
import com.leslie.cjpokeroddscalculator.databinding.OmahaHighPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaLiveUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;

import java.util.ArrayList;
import java.util.List;

public class OmahaHighFragment extends EquityCalculatorFragment {

    public int playerCardMaxWidth;

    @Override
    public void monteCarloProc() {
        try {
            OmahaMonteCarloCalc calcObj = new OmahaMonteCarloCalc(this.cardsPerHand);
            OmahaOutputResult omahaOutputResult = new OmahaLiveUpdate(this, calcObj);
            calcObj.setOmahaPokerObj(new OmahaPoker(omahaOutputResult));
            calcObj.calculate(cardRows);
        } catch (InterruptedException ignored) { }
    }

    @Override
    public void exactCalcProc() {
        try {
            OmahaExactCalc calcObj = new OmahaExactCalc(this.cardsPerHand);
            OmahaOutputResult omahaOutputResult = new OmahaFinalUpdate(this, calcObj);
            calcObj.setOmahaPokerObj(new OmahaPoker(omahaOutputResult));
            calcObj.calculate(cardRows);
        } catch (InterruptedException ignored) { }
    }

    @Override
    public void addPlayerRow() {
        OmahaHighPlayerRowBinding bindingPlayerRow = OmahaHighPlayerRowBinding.inflate(LayoutInflater.from(requireActivity()), equityCalculatorBinding.playerRows, true);

        List<ImageButton> cardList = createCardButtons(bindingPlayerRow.getRoot(), bindingPlayerRow.playerText, bindingPlayerRow.statsButton);

        setRowViews(bindingPlayerRow.getRoot(), bindingPlayerRow.playerText, cardList, this.playerCardMaxWidth, bindingPlayerRow.remove, bindingPlayerRow.statsButton, bindingPlayerRow.statsView.getRoot());

        addToStatsMatrix(
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
        );
    }

    public List<ImageButton> createCardButtons(ConstraintLayout playerRow, TextView playerText, MaterialButton statsButton) {
        List<ImageButton> cardList = new ArrayList<>();

        for (int i = 0; i < this.cardsPerHand; i++) {
            ImageButton card = new ImageButton(requireActivity(), null, 0, R.style.SelectCardButton);
            card.setId(View.generateViewId());
            cardList.add(card);
        }

        for (int i = 0; i < this.cardsPerHand; i++) {
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            );

            if (i == 0) {
                layoutParams.topToBottom = playerText.getId();
                layoutParams.leftToLeft = ConstraintSet.PARENT_ID;
                layoutParams.rightToLeft = cardList.get(i + 1).getId();
            } else if (i == this.cardsPerHand - 1) {
                layoutParams.topToBottom = playerText.getId();
                layoutParams.leftToRight = cardList.get(i - 1).getId();
                layoutParams.rightToRight = playerText.getId();
            } else {
                layoutParams.topToBottom = playerText.getId();
                layoutParams.leftToRight = cardList.get(i - 1).getId();
                layoutParams.rightToLeft = cardList.get(i + 1).getId();
            }

            cardList.get(i).setLayoutParams(layoutParams);

            playerRow.addView(cardList.get(i));
        }

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) statsButton.getLayoutParams();
        layoutParams.bottomToBottom = cardList.get(0).getId();
        statsButton.setLayoutParams(layoutParams);

        return cardList;
    }

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        cardsPerHand = 4;
        maxPlayers = 10;
        this.playerCardMaxWidth = this.boardCardMaxWidth;
        fragmentName = "OmahaHigh";
        fragmentId = R.id.OmahaHighFragment;
        homeButtonActionId = R.id.action_OmahaHighFragment_to_HomeFragment;
        initialStats = new double[]{
            50.0,
            49.29,
            1.42,
            2.99,
            26.47,
            36.83,
            8.79,
            11.27,
            6.72,
            6.35,
            0.48,
            0.09
        };
        titleTextId = R.string.omaha_high_equity_calculator;
    }
}