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

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private CardselectorBinding binding_card_selector;

    private final ImageButton[][] player_cards_array = new ImageButton[10][];
    private final ImageButton[] all_cards_array = new ImageButton[25];
    private final LinearLayout[] player_row_array = new LinearLayout[10];
    public TextView[] win_array = new TextView[10];
    private RadioButton[] rank_radio_array;
    private ImageButton selector_input;
    private Dialog dialog;
    public Thread monte_carlo_thread = null;
    public Thread exact_calc_thread = null;

    int players_remaining_no = 2, rank_checked_id = -1;

    HashMap<Integer, int[]> card_id_position_map = new HashMap<>();
    HashMap<Integer, Integer> remove_id_row_map = new HashMap<>();

    int[][][] cards = {
        {
            {0, 0},
            {0, 0},
            {0, 0},
            {0, 0},
            {0, 0},
        },
        {
            {0, 0},
            {0, 0},
        },
        {
            {0, 0},
            {0, 0},
        },
        {
            {0, 0},
            {0, 0},
        },
        {
            {0, 0},
            {0, 0},
        },
        {
            {0, 0},
            {0, 0},
        },
        {
            {0, 0},
            {0, 0},
        },
        {
            {0, 0},
            {0, 0},
        },
        {
            {0, 0},
            {0, 0},
        },
        {
            {0, 0},
            {0, 0},
        },
        {
            {0, 0},
            {0, 0},
        },
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.playersremaining.setText(getString(R.string.players_remaining, players_remaining_no));

        all_cards_array[0] = binding.flop1;
        all_cards_array[1] = binding.flop2;
        all_cards_array[2] = binding.flop3;
        all_cards_array[3] = binding.turn;
        all_cards_array[4] = binding.river;

        card_id_position_map.put(binding.flop1.getId(), new int[] {0, 0});
        card_id_position_map.put(binding.flop2.getId(), new int[] {0, 1});
        card_id_position_map.put(binding.flop3.getId(), new int[] {0, 2});
        card_id_position_map.put(binding.turn.getId(), new int[] {0, 3});
        card_id_position_map.put(binding.river.getId(), new int[] {0, 4});


        for (int i = 0; i < 10; i++) {
            com.leslie.cjpokeroddscalculator.databinding.PlayerRowBinding binding_player_row = PlayerRowBinding.inflate(LayoutInflater.from(MainActivity.this), binding.playerRows, true);
            binding_player_row.playerText.setText(getString(R.string.player, i + 1));
            player_cards_array[i] = new ImageButton[] {binding_player_row.card1, binding_player_row.card2};
            player_row_array[i] = binding_player_row.getRoot();
            win_array[i] = binding_player_row.win;
            all_cards_array[i * 2 + 5] = binding_player_row.card1;
            all_cards_array[i * 2 + 6] = binding_player_row.card2;
            binding_player_row.remove.setOnClickListener(remove_player_listener);
            binding_player_row.card1.setId(View.generateViewId());
            binding_player_row.card2.setId(View.generateViewId());
            binding_player_row.remove.setId(View.generateViewId());
            card_id_position_map.put(binding_player_row.card1.getId(), new int[] {i + 1, 0});
            card_id_position_map.put(binding_player_row.card2.getId(), new int[] {i + 1, 1});
            remove_id_row_map.put(binding_player_row.remove.getId(), i + 1);
        }

        for (int i = 2; i < 10; i++) {
            player_row_array[i].setVisibility(View.GONE);
        }

        for (ImageButton b : all_cards_array) {
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
            for (ImageButton b : all_cards_array) {
                set_card(b, 0, 0);
            }

            calculate_odds();
        });
    }

    private final View.OnClickListener remove_player_listener = new View.OnClickListener() {
        public void onClick(View v) {

            final Button remove_input = (Button) v;
            int player_remove_number = remove_id_row_map.get(remove_input.getId());

            if(players_remaining_no > 2){
                players_remaining_no--;
                binding.playersremaining.setText(getString(R.string.players_remaining, players_remaining_no));
                player_row_array[players_remaining_no].setVisibility(View.GONE);

                for (int i = player_remove_number; i <= players_remaining_no; i++) {
                    set_card(player_cards_array[i - 1][0], cards[i + 1][0][0], cards[i + 1][0][1]);
                    set_card(player_cards_array[i - 1][1], cards[i + 1][1][0], cards[i + 1][1][1]);
                }

                set_card(player_cards_array[players_remaining_no][0], 0, 0);
                set_card(player_cards_array[players_remaining_no][1], 0, 0);

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

            rank_radio_array = new RadioButton[] {
                binding_card_selector.radio2,
                binding_card_selector.radio3,
                binding_card_selector.radio4,
                binding_card_selector.radio5,
                binding_card_selector.radio6,
                binding_card_selector.radio7,
                binding_card_selector.radio8,
                binding_card_selector.radio9,
                binding_card_selector.radio10,
                binding_card_selector.radio11,
                binding_card_selector.radio12,
                binding_card_selector.radio13,
                binding_card_selector.radio14
            };

            binding_card_selector.radioGroupSuit.setOnCheckedChangeListener((group, checkedId) -> {
                if(rank_checked_id != -1) {
                    card_selected(
                        convert_id_to_number(binding_card_selector.radioGroupSuit.getCheckedRadioButtonId()),
                        convert_id_to_number(rank_checked_id)
                    );
                }
                else{
                    for (RadioButton r : rank_radio_array) {
                        r.setVisibility(View.VISIBLE);
                    }

                    for (int row = 0; row <= players_remaining_no; row++) {
                        for (int[] card : cards[row]) {
                            if(card[0] == convert_id_to_number(binding_card_selector.radioGroupSuit.getCheckedRadioButtonId())){
                                hide_rank_radio_button(card[1]);
                            }
                        }
                    }
                }
            });

            for (RadioButton r : rank_radio_array) {
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

            for (RadioButton r : rank_radio_array) {
                r.setChecked(false);
            }

            rank_input.setChecked(true);

            if(binding_card_selector.radioGroupSuit.getCheckedRadioButtonId() != -1) {
                card_selected(
                    convert_id_to_number(binding_card_selector.radioGroupSuit.getCheckedRadioButtonId()),
                    convert_id_to_number(rank_checked_id)
                );
            }
            else{
                binding_card_selector.radioDiamond.setVisibility(View.VISIBLE);
                binding_card_selector.radioClub.setVisibility(View.VISIBLE);
                binding_card_selector.radioHeart.setVisibility(View.VISIBLE);
                binding_card_selector.radioSpade.setVisibility(View.VISIBLE);

                for (int row = 0; row <= players_remaining_no; row++) {
                    for (int[] card : cards[row]) {
                        if(card[1] == convert_id_to_number(rank_checked_id)){
                            hide_suit_radio_button(card[0]);
                        }
                    }
                }
            }
        }
    };

    public void set_card(ImageButton card_button, int suit, int rank) {
        int row_idx = Objects.requireNonNull(card_id_position_map.get(card_button.getId()))[0];
        int card_idx = Objects.requireNonNull(card_id_position_map.get(card_button.getId()))[1];
        cards[row_idx][card_idx][0] = suit;
        cards[row_idx][card_idx][1] = rank;
        card_button.setImageResource(get_card_id(suit, rank));
    }

    public void card_selected(int suit, int rank) {
        set_card(selector_input, suit, rank);
        rank_checked_id = -1;
        dialog.dismiss();
        calculate_odds();
    }

    public void hide_rank_radio_button(int rank_no) {
        switch(rank_no) {
            case 2:
                binding_card_selector.radio2.setVisibility(View.INVISIBLE);
                break;
            case 3:
                binding_card_selector.radio3.setVisibility(View.INVISIBLE);
                break;
            case 4:
                binding_card_selector.radio4.setVisibility(View.INVISIBLE);
                break;
            case 5:
                binding_card_selector.radio5.setVisibility(View.INVISIBLE);
                break;
            case 6:
                binding_card_selector.radio6.setVisibility(View.INVISIBLE);
                break;
            case 7:
                binding_card_selector.radio7.setVisibility(View.INVISIBLE);
                break;
            case 8:
                binding_card_selector.radio8.setVisibility(View.INVISIBLE);
                break;
            case 9:
                binding_card_selector.radio9.setVisibility(View.INVISIBLE);
                break;
            case 10:
                binding_card_selector.radio10.setVisibility(View.INVISIBLE);
                break;
            case 11:
                binding_card_selector.radio11.setVisibility(View.INVISIBLE);
                break;
            case 12:
                binding_card_selector.radio12.setVisibility(View.INVISIBLE);
                break;
            case 13:
                binding_card_selector.radio13.setVisibility(View.INVISIBLE);
                break;
            case 14:
                binding_card_selector.radio14.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void hide_suit_radio_button(int suit_no) {
        switch(suit_no) {
            case 1:
                binding_card_selector.radioDiamond.setVisibility(View.INVISIBLE);
                break;
            case 2:
                binding_card_selector.radioClub.setVisibility(View.INVISIBLE);
                break;
            case 3:
                binding_card_selector.radioHeart.setVisibility(View.INVISIBLE);
                break;
            case 4:
                binding_card_selector.radioSpade.setVisibility(View.INVISIBLE);
                break;
        }
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

    public int convert_id_to_number(int checkedRadioButtonId) {
        if (checkedRadioButtonId == R.id.radio_diamond) {
            return 1;
        }
        else if (checkedRadioButtonId == R.id.radio_club || checkedRadioButtonId == R.id.radio_2) {
            return 2;
        }
        else if (checkedRadioButtonId == R.id.radio_heart || checkedRadioButtonId == R.id.radio_3) {
            return 3;
        }
        else if (checkedRadioButtonId == R.id.radio_spade || checkedRadioButtonId == R.id.radio_4) {
            return 4;
        }
        else if (checkedRadioButtonId == R.id.radio_5) {
            return 5;
        }
        else if (checkedRadioButtonId == R.id.radio_6) {
            return 6;
        }
        else if (checkedRadioButtonId == R.id.radio_7) {
            return 7;
        }
        else if (checkedRadioButtonId == R.id.radio_8) {
            return 8;
        }
        else if (checkedRadioButtonId == R.id.radio_9) {
            return 9;
        }
        else if (checkedRadioButtonId == R.id.radio_10) {
            return 10;
        }
        else if (checkedRadioButtonId == R.id.radio_11) {
            return 11;
        }
        else if (checkedRadioButtonId == R.id.radio_12) {
            return 12;
        }
        else if (checkedRadioButtonId == R.id.radio_13) {
            return 13;
        }
        else if (checkedRadioButtonId == R.id.radio_14) {
            return 14;
        }

        return checkedRadioButtonId;
    }

    private int get_card_id(int suit, int rank) {
        switch(suit) {
            case 0:
                return R.drawable.unknown_button;
            case 1:
                switch(rank) {
                    case 2:
                        return R.drawable.d2_button;
                    case 3:
                        return R.drawable.d3_button;
                    case 4:
                        return R.drawable.d4_button;
                    case 5:
                        return R.drawable.d5_button;
                    case 6:
                        return R.drawable.d6_button;
                    case 7:
                        return R.drawable.d7_button;
                    case 8:
                        return R.drawable.d8_button;
                    case 9:
                        return R.drawable.d9_button;
                    case 10:
                        return R.drawable.d10_button;
                    case 11:
                        return R.drawable.d11_button;
                    case 12:
                        return R.drawable.d12_button;
                    case 13:
                        return R.drawable.d13_button;
                    case 14:
                        return R.drawable.d14_button;
                }
            case 2:
                switch(rank) {
                    case 2:
                        return R.drawable.c2_button;
                    case 3:
                        return R.drawable.c3_button;
                    case 4:
                        return R.drawable.c4_button;
                    case 5:
                        return R.drawable.c5_button;
                    case 6:
                        return R.drawable.c6_button;
                    case 7:
                        return R.drawable.c7_button;
                    case 8:
                        return R.drawable.c8_button;
                    case 9:
                        return R.drawable.c9_button;
                    case 10:
                        return R.drawable.c10_button;
                    case 11:
                        return R.drawable.c11_button;
                    case 12:
                        return R.drawable.c12_button;
                    case 13:
                        return R.drawable.c13_button;
                    case 14:
                        return R.drawable.c14_button;
                }
            case 3:
                switch(rank) {
                    case 2:
                        return R.drawable.h2_button;
                    case 3:
                        return R.drawable.h3_button;
                    case 4:
                        return R.drawable.h4_button;
                    case 5:
                        return R.drawable.h5_button;
                    case 6:
                        return R.drawable.h6_button;
                    case 7:
                        return R.drawable.h7_button;
                    case 8:
                        return R.drawable.h8_button;
                    case 9:
                        return R.drawable.h9_button;
                    case 10:
                        return R.drawable.h10_button;
                    case 11:
                        return R.drawable.h11_button;
                    case 12:
                        return R.drawable.h12_button;
                    case 13:
                        return R.drawable.h13_button;
                    case 14:
                        return R.drawable.h14_button;
                }
            case 4:
                switch(rank) {
                    case 2:
                        return R.drawable.s2_button;
                    case 3:
                        return R.drawable.s3_button;
                    case 4:
                        return R.drawable.s4_button;
                    case 5:
                        return R.drawable.s5_button;
                    case 6:
                        return R.drawable.s6_button;
                    case 7:
                        return R.drawable.s7_button;
                    case 8:
                        return R.drawable.s8_button;
                    case 9:
                        return R.drawable.s9_button;
                    case 10:
                        return R.drawable.s10_button;
                    case 11:
                        return R.drawable.s11_button;
                    case 12:
                        return R.drawable.s12_button;
                    case 13:
                        return R.drawable.s13_button;
                    case 14:
                        return R.drawable.s14_button;
                }
        }

        return 0;
    }

}