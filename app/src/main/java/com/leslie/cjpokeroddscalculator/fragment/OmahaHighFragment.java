package com.leslie.cjpokeroddscalculator.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.calculation.OmahaExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.OmahaMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.databinding.OmahaHighPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaLiveUpdate;

import java.util.Arrays;
import java.util.List;

public class OmahaHighFragment extends EquityCalculatorFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        equityList.get(0).setText(getString(R.string.two_decimal_perc, 50.0));
        equityList.get(1).setText(getString(R.string.two_decimal_perc, 50.0));
        winList.get(0).setText(getString(R.string.two_decimal_perc, 49.33));
        winList.get(1).setText(getString(R.string.two_decimal_perc, 49.33));
        tieList.get(0).setText(getString(R.string.two_decimal_perc, 1.33));
        tieList.get(1).setText(getString(R.string.two_decimal_perc, 1.33));

        equityCalculatorBinding.title.setText(getString(R.string.omaha_high_equity_calculator));

        equityCalculatorBinding.homeButton.setOnClickListener(view1 -> NavHostFragment.findNavController(this).navigate(R.id.action_OmahaHighFragment_to_HomeFragment));

        monteCarloProc = () -> {
            try {
                OmahaMonteCarloCalc calcObj = new OmahaMonteCarloCalc();
                calcObj.calculate(cardRows, new OmahaLiveUpdate(this, calcObj));
            } catch (InterruptedException ignored) { }
        };

        exactCalcProc = () -> {
            try {
                OmahaExactCalc calcObj = new OmahaExactCalc();
                calcObj.calculate(cardRows, new OmahaFinalUpdate(this, calcObj));
            } catch (InterruptedException ignored) { }
        };
    }

    @Override
    public void addPlayerRow() {
        OmahaHighPlayerRowBinding bindingPlayerRow = OmahaHighPlayerRowBinding.inflate(LayoutInflater.from(requireActivity()), equityCalculatorBinding.playerRows, true);
        playerRowList.add(bindingPlayerRow.getRoot());
        equityList.add(bindingPlayerRow.equity);
        winList.add(bindingPlayerRow.win);
        tieList.add(bindingPlayerRow.tie);

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

        bindingPlayerRow.playerText.setText(getString(R.string.player, playerRowList.size()));
        bindingPlayerRow.remove.setOnClickListener(removePlayerListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (monteCarloThread != null) {
            monteCarloThread.interrupt();
        }

        if (exactCalcThread != null) {
            exactCalcThread.interrupt();
        }
    }

    @Override
    public void initialiseVariables() {
        super.initialiseVariables();

        cardsPerHand = 4;
        fragmentName = "OmahaHigh";
    }

    @Override
    public void generateMainLayout() {
        super.generateMainLayout();

        for (int i = 0; i < 2; i++) {
            addPlayerRow();
        }
    }
}