package com.leslie.cjpokeroddscalculator;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.leslie.cjpokeroddscalculator.databinding.ActivityMainBinding;
import com.leslie.cjpokeroddscalculator.databinding.CardselectorBinding;
import com.leslie.cjpokeroddscalculator.databinding.PlayerRowBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.HashBiMap;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private CardselectorBinding binding_card_selector;

    private ImageButton selector_input;
    private Dialog dialog;
    public Thread monte_carlo_thread = null;
    public Thread exact_calc_thread = null;

    int players_remaining_no = 2, rank_checked_id = -1;

    private final LinearLayout[] player_row_array = new LinearLayout[10];
    public TextView[] win_array = new TextView[10];

    HashBiMap<List<Integer>, ImageButton> cardPositionBiMap = HashBiMap.create();
    HashMap<Button, Integer> remove_row_map = new HashMap<>();
    HashMap<Integer, RadioButton> rank_radio_map = new HashMap<>();
    HashMap<Integer, RadioButton> suit_radio_map = new HashMap<>();
    HashMap<Integer, Integer> radio_id_number_map = new HashMap<>();
    HashMap<Integer, HashMap<Integer, Integer>> suit_rank_drawable_map = new HashMap<>();

    int[][][] cards = new int[11][][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.playersremaining.setText(getString(R.string.players_remaining, players_remaining_no));

        initialise_variables();

        for (int i = 0; i < 10; i++) {
            PlayerRowBinding binding_player_row = PlayerRowBinding.inflate(LayoutInflater.from(MainActivity.this), binding.playerRows, true);

            player_row_array[i] = binding_player_row.getRoot();
            win_array[i] = binding_player_row.win;
            cardPositionBiMap.put(Arrays.asList(i + 1, 0), binding_player_row.card1);
            cardPositionBiMap.put(Arrays.asList(i + 1, 1), binding_player_row.card2);
            remove_row_map.put(binding_player_row.remove, i + 1);

            binding_player_row.playerText.setText(getString(R.string.player, i + 1));
            binding_player_row.remove.setOnClickListener(remove_player_listener);
        }

        for (int i = 2; i < 10; i++) {
            player_row_array[i].setVisibility(View.GONE);
        }

        for (ImageButton b : cardPositionBiMap.values()) {
            b.setOnClickListener(selector_listener);
        }

        binding.addplayer.setOnClickListener(v -> {
            if(players_remaining_no < 10){
                players_remaining_no++;
                binding.playersremaining.setText(getString(R.string.players_remaining, players_remaining_no));
                player_row_array[players_remaining_no - 1].setVisibility(View.VISIBLE);
                calculate_odds();
            }
            else{
                Toast.makeText(MainActivity.this, "Max number of players is 10", Toast.LENGTH_SHORT).show();
            }
        });

        binding.clear.setOnClickListener(v -> {
            for (ImageButton b : cardPositionBiMap.values()) {
                set_card(b, 0, 0);
            }

            calculate_odds();
        });
    }

    private void initialise_variables() {
        cards[0] = new int[][]{ {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0} };

        for (int i = 1; i <= 10; i++) {
            cards[i] = new int[][]{ {0, 0}, {0, 0} };
        }

        cardPositionBiMap.put(Arrays.asList(0, 0), binding.flop1);
        cardPositionBiMap.put(Arrays.asList(0, 1), binding.flop2);
        cardPositionBiMap.put(Arrays.asList(0, 2), binding.flop3);
        cardPositionBiMap.put(Arrays.asList(0, 3), binding.turn);
        cardPositionBiMap.put(Arrays.asList(0, 4), binding.river);

        radio_id_number_map.put(R.id.radio_diamond, 1);
        radio_id_number_map.put(R.id.radio_club, 2);
        radio_id_number_map.put(R.id.radio_heart, 3);
        radio_id_number_map.put(R.id.radio_spade, 4);
        radio_id_number_map.put(R.id.radio_2, 2);
        radio_id_number_map.put(R.id.radio_3, 3);
        radio_id_number_map.put(R.id.radio_4, 4);
        radio_id_number_map.put(R.id.radio_5, 5);
        radio_id_number_map.put(R.id.radio_6, 6);
        radio_id_number_map.put(R.id.radio_7, 7);
        radio_id_number_map.put(R.id.radio_8, 8);
        radio_id_number_map.put(R.id.radio_9, 9);
        radio_id_number_map.put(R.id.radio_10, 10);
        radio_id_number_map.put(R.id.radio_11, 11);
        radio_id_number_map.put(R.id.radio_12, 12);
        radio_id_number_map.put(R.id.radio_13, 13);
        radio_id_number_map.put(R.id.radio_14, 14);

        HashMap<Integer, Integer> temp_map = new HashMap<>();
        temp_map.put(0, R.drawable.unknown_button);
        suit_rank_drawable_map.put(0, temp_map);

        temp_map = new HashMap<>();
        temp_map.put(2, R.drawable.d2_button);
        temp_map.put(3, R.drawable.d3_button);
        temp_map.put(4, R.drawable.d4_button);
        temp_map.put(5, R.drawable.d5_button);
        temp_map.put(6, R.drawable.d6_button);
        temp_map.put(7, R.drawable.d7_button);
        temp_map.put(8, R.drawable.d8_button);
        temp_map.put(9, R.drawable.d9_button);
        temp_map.put(10, R.drawable.d10_button);
        temp_map.put(11, R.drawable.d11_button);
        temp_map.put(12, R.drawable.d12_button);
        temp_map.put(13, R.drawable.d13_button);
        temp_map.put(14, R.drawable.d14_button);
        suit_rank_drawable_map.put(1, temp_map);

        temp_map = new HashMap<>();
        temp_map.put(2, R.drawable.c2_button);
        temp_map.put(3, R.drawable.c3_button);
        temp_map.put(4, R.drawable.c4_button);
        temp_map.put(5, R.drawable.c5_button);
        temp_map.put(6, R.drawable.c6_button);
        temp_map.put(7, R.drawable.c7_button);
        temp_map.put(8, R.drawable.c8_button);
        temp_map.put(9, R.drawable.c9_button);
        temp_map.put(10, R.drawable.c10_button);
        temp_map.put(11, R.drawable.c11_button);
        temp_map.put(12, R.drawable.c12_button);
        temp_map.put(13, R.drawable.c13_button);
        temp_map.put(14, R.drawable.c14_button);
        suit_rank_drawable_map.put(2, temp_map);

        temp_map = new HashMap<>();
        temp_map.put(2, R.drawable.h2_button);
        temp_map.put(3, R.drawable.h3_button);
        temp_map.put(4, R.drawable.h4_button);
        temp_map.put(5, R.drawable.h5_button);
        temp_map.put(6, R.drawable.h6_button);
        temp_map.put(7, R.drawable.h7_button);
        temp_map.put(8, R.drawable.h8_button);
        temp_map.put(9, R.drawable.h9_button);
        temp_map.put(10, R.drawable.h10_button);
        temp_map.put(11, R.drawable.h11_button);
        temp_map.put(12, R.drawable.h12_button);
        temp_map.put(13, R.drawable.h13_button);
        temp_map.put(14, R.drawable.h14_button);
        suit_rank_drawable_map.put(3, temp_map);

        temp_map = new HashMap<>();
        temp_map.put(2, R.drawable.s2_button);
        temp_map.put(3, R.drawable.s3_button);
        temp_map.put(4, R.drawable.s4_button);
        temp_map.put(5, R.drawable.s5_button);
        temp_map.put(6, R.drawable.s6_button);
        temp_map.put(7, R.drawable.s7_button);
        temp_map.put(8, R.drawable.s8_button);
        temp_map.put(9, R.drawable.s9_button);
        temp_map.put(10, R.drawable.s10_button);
        temp_map.put(11, R.drawable.s11_button);
        temp_map.put(12, R.drawable.s12_button);
        temp_map.put(13, R.drawable.s13_button);
        temp_map.put(14, R.drawable.s14_button);
        suit_rank_drawable_map.put(4, temp_map);
    }

    private final View.OnClickListener remove_player_listener = new View.OnClickListener() {
        public void onClick(View v) {

            final Button remove_input = (Button) v;
            int player_remove_number = remove_row_map.get(remove_input);

            if(players_remaining_no > 2){
                players_remaining_no--;
                binding.playersremaining.setText(getString(R.string.players_remaining, players_remaining_no));
                player_row_array[players_remaining_no].setVisibility(View.GONE);

                for (int i = player_remove_number; i <= players_remaining_no; i++) {
                    set_card(Objects.requireNonNull(cardPositionBiMap.get(Arrays.asList(i, 0))), cards[i + 1][0][0], cards[i + 1][0][1]);
                    set_card(Objects.requireNonNull(cardPositionBiMap.get(Arrays.asList(i, 1))), cards[i + 1][1][0], cards[i + 1][1][1]);
                }

                set_card(Objects.requireNonNull(cardPositionBiMap.get(Arrays.asList(players_remaining_no + 1, 0))), 0, 0);
                set_card(Objects.requireNonNull(cardPositionBiMap.get(Arrays.asList(players_remaining_no + 1, 1))), 0, 0);

                calculate_odds();
            }
            else{
                Toast.makeText(MainActivity.this, "Min number of players is 2", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final View.OnClickListener selector_listener = new View.OnClickListener() {
        public void onClick(View v) {
            selector_input = (ImageButton) v;

            binding_card_selector = CardselectorBinding.inflate(LayoutInflater.from(MainActivity.this));

            dialog = new Dialog(MainActivity.this);
            dialog.setContentView(binding_card_selector.getRoot());
            dialog.setTitle("Select Card");
            dialog.setCancelable(true);

            rank_checked_id = -1;

            rank_radio_map.put(2, binding_card_selector.radio2);
            rank_radio_map.put(3, binding_card_selector.radio3);
            rank_radio_map.put(4, binding_card_selector.radio4);
            rank_radio_map.put(5, binding_card_selector.radio5);
            rank_radio_map.put(6, binding_card_selector.radio6);
            rank_radio_map.put(7, binding_card_selector.radio7);
            rank_radio_map.put(8, binding_card_selector.radio8);
            rank_radio_map.put(9, binding_card_selector.radio9);
            rank_radio_map.put(10, binding_card_selector.radio10);
            rank_radio_map.put(11, binding_card_selector.radio11);
            rank_radio_map.put(12, binding_card_selector.radio12);
            rank_radio_map.put(13, binding_card_selector.radio13);
            rank_radio_map.put(14, binding_card_selector.radio14);

            suit_radio_map.put(1, binding_card_selector.radioDiamond);
            suit_radio_map.put(2, binding_card_selector.radioClub);
            suit_radio_map.put(3, binding_card_selector.radioHeart);
            suit_radio_map.put(4, binding_card_selector.radioSpade);

            binding_card_selector.radioGroupSuit.setOnCheckedChangeListener((group, checkedId) -> {
                if(rank_checked_id != -1) {
                    card_selected(
                        radio_id_number_map.get(binding_card_selector.radioGroupSuit.getCheckedRadioButtonId()),
                        radio_id_number_map.get(rank_checked_id)
                    );
                }
                else{
                    for (RadioButton r : rank_radio_map.values()) {
                        r.setVisibility(View.VISIBLE);
                    }

                    for (int row = 0; row <= players_remaining_no; row++) {
                        for (int[] card : cards[row]) {
                            if(card[0] == radio_id_number_map.get(binding_card_selector.radioGroupSuit.getCheckedRadioButtonId())){
                                Objects.requireNonNull(rank_radio_map.get(card[1])).setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
            });

            for (RadioButton r : rank_radio_map.values()) {
                r.setOnClickListener(rank_listener);
            }

            binding_card_selector.buttonUnknown.setOnClickListener(v1 -> card_selected(0, 0));

            dialog.show();
        }
    };

    private final View.OnClickListener rank_listener = new View.OnClickListener() {
        public void onClick(View v) {

            final RadioButton rank_input = (RadioButton) v;

            rank_checked_id = rank_input.getId();

            for (RadioButton r : rank_radio_map.values()) {
                r.setChecked(false);
            }

            rank_input.setChecked(true);

            if(binding_card_selector.radioGroupSuit.getCheckedRadioButtonId() != -1) {
                card_selected(
                    radio_id_number_map.get(binding_card_selector.radioGroupSuit.getCheckedRadioButtonId()),
                    radio_id_number_map.get(rank_checked_id)
                );
            }
            else{
                binding_card_selector.radioDiamond.setVisibility(View.VISIBLE);
                binding_card_selector.radioClub.setVisibility(View.VISIBLE);
                binding_card_selector.radioHeart.setVisibility(View.VISIBLE);
                binding_card_selector.radioSpade.setVisibility(View.VISIBLE);

                for (int row = 0; row <= players_remaining_no; row++) {
                    for (int[] card : cards[row]) {
                        if(card[1] == radio_id_number_map.get(rank_checked_id)){
                            Objects.requireNonNull(suit_radio_map.get(card[0])).setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        }
    };

    public void set_card(ImageButton card_button, int suit, int rank) {
        List<Integer> position = cardPositionBiMap.inverse().get(card_button);
        assert position != null;
        cards[position.get(0)][position.get(1)][0] = suit;
        cards[position.get(0)][position.get(1)][1] = rank;
        card_button.setImageResource(suit_rank_drawable_map.get(suit).get(rank));
    }

    public void card_selected(int suit, int rank) {
        set_card(selector_input, suit, rank);
        rank_checked_id = -1;
        dialog.dismiss();
        calculate_odds();
    }

    private void calculate_odds() {
        if (monte_carlo_thread != null) {
            monte_carlo_thread.interrupt();
        }

        if (exact_calc_thread != null) {
            exact_calc_thread.interrupt();
        }

        for(int i = 0; i < players_remaining_no; i++) {
            win_array[i].setText(R.string.win_perc_empty);
            win_array[i].setTextColor(Color.WHITE);
        }

        monte_carlo_thread = new Thread(null, monte_carlo_proc);
        exact_calc_thread = new Thread(null, exact_calc_proc);

        monte_carlo_thread.start();
        exact_calc_thread.start();
    }

    private final Runnable monte_carlo_proc = () -> {
        try {
            MonteCarloCalc calc_obj = new MonteCarloCalc();
            calc_obj.monte_carl_calc(cards, players_remaining_no, new LiveUpdate(this, calc_obj));
        } catch (InterruptedException ignored) { }
    };

    private final Runnable exact_calc_proc = () -> {
        try {
            ExactCalc calc_obj = new ExactCalc();
            calc_obj.exact_calc(cards, players_remaining_no, new FinalUpdate(this, calc_obj));
        } catch (InterruptedException ignored) { }
    };
}