package com.leslie.cjpokeroddscalculator.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.fragment.NavHostFragment;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.calculation.OmahaExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.OmahaMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.databinding.OmahaHighPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaLiveUpdate;

import java.util.Arrays;

public class OmahaHighFragment extends EquityCalculatorFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

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
                calcObj.calculate(cardRows, playersRemainingNo, new OmahaLiveUpdate(this, calcObj));
            } catch (InterruptedException ignored) { }
        };

        exactCalcProc = () -> {
            try {
                OmahaExactCalc calcObj = new OmahaExactCalc();
                calcObj.calculate(cardRows, playersRemainingNo, new OmahaFinalUpdate(this, calcObj));
            } catch (InterruptedException ignored) { }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (monte_carlo_thread != null) {
            monte_carlo_thread.interrupt();
        }

        if (exact_calc_thread != null) {
            exact_calc_thread.interrupt();
        }
    }

    public void initialiseVariables() {
        super.initialiseVariables();

        cardsPerHand = 4;
        fragmentName = "OmahaHigh";
    }

    public void generateMainLayout() {
        super.generateMainLayout();

        for (int i = 0; i < 10; i++) {
            OmahaHighPlayerRowBinding bindingPlayerRow = OmahaHighPlayerRowBinding.inflate(LayoutInflater.from(requireActivity()), equityCalculatorBinding.playerRows, true);
            ConstraintLayout playerRow = bindingPlayerRow.getRoot();
            playerRow.setId(View.generateViewId());
            playerRowList.add(playerRow);
            equityList.add(bindingPlayerRow.equity);
            winList.add(bindingPlayerRow.win);
            tieList.add(bindingPlayerRow.tie);

            cardListOfLists.add(
                Arrays.asList(
                    bindingPlayerRow.card1,
                    bindingPlayerRow.card2,
                    bindingPlayerRow.card3,
                    bindingPlayerRow.card4
                )
            );

            removeRowList.add(bindingPlayerRow.remove);

            bindingPlayerRow.playerText.setText(getString(R.string.player, i + 1));
            bindingPlayerRow.remove.setOnClickListener(removePlayerListener);
        }
    }
}