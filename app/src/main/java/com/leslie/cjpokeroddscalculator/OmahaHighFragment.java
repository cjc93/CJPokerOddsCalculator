package com.leslie.cjpokeroddscalculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.leslie.cjpokeroddscalculator.calculation.OmahaMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.databinding.OmahaHighPlayerRowBinding;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaLiveUpdate;

import java.util.Arrays;

public class OmahaHighFragment extends EquityCalculatorFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        winArray[0].setText(getString(R.string.two_decimal_perc, 49.33));
        winArray[1].setText(getString(R.string.two_decimal_perc, 49.33));
        tieArray[0].setText(getString(R.string.two_decimal_perc, 1.34));
        tieArray[1].setText(getString(R.string.two_decimal_perc, 1.34));

        equityCalculatorBinding.title.setText(getString(R.string.omaha_high_poker));

        equityCalculatorBinding.homeButton.setOnClickListener(view1 -> NavHostFragment.findNavController(this).navigate(R.id.action_OmahaHighFragment_to_HomeFragment));

        monteCarloProc = () -> {
            try {
                OmahaMonteCarloCalc calcObj = new OmahaMonteCarloCalc();
                calcObj.calculate(cardRows, playersRemainingNo, new OmahaLiveUpdate(this, calcObj));
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

    public void setCardsPerHand() {
        cardsPerHand = 4;
    }

    public void generate_main_layout() {
        super.generate_main_layout();

        for (int i = 0; i < 10; i++) {
            OmahaHighPlayerRowBinding binding_player_row = OmahaHighPlayerRowBinding.inflate(LayoutInflater.from(requireActivity()), equityCalculatorBinding.playerRows, true);
            player_row_array[i] = binding_player_row.getRoot();
            equityArray[i] = binding_player_row.equity;
            winArray[i] = binding_player_row.win;
            tieArray[i] = binding_player_row.tie;
            cardPositionBiMap.put(Arrays.asList(i + 1, 0), binding_player_row.card1);
            cardPositionBiMap.put(Arrays.asList(i + 1, 1), binding_player_row.card2);
            cardPositionBiMap.put(Arrays.asList(i + 1, 2), binding_player_row.card3);
            cardPositionBiMap.put(Arrays.asList(i + 1, 3), binding_player_row.card4);
            removeRowMap.put(binding_player_row.remove, i + 1);

            binding_player_row.playerText.setText(getString(R.string.player, i + 1));
            binding_player_row.remove.setOnClickListener(removePlayerListener);
        }
    }
}