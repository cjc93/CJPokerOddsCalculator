package com.leslie.cjpokeroddscalculator;

import java.util.Arrays;
import java.util.Random;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.leslie.cjpokeroddscalculator.databinding.ActivityMainBinding;
import com.leslie.cjpokeroddscalculator.databinding.CardselectorBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private CardselectorBinding binding_card_selector;

    final private Handler handler = new Handler();

    private ImageButton[][] player_cards_array;
    private LinearLayout[] player_row_array;
    private TextView[] win_array;
    private ImageButton selector_input;
    private Dialog dialog;
    private Thread thread = null;

    float[] equity = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    int players_remaining_no = 2, rank_checked_id = -1, no_of_simulations=3000;

    final Random myRandom = new Random();

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

        player_cards_array = new ImageButton[][] {
            {binding.player11, binding.player12},
            {binding.player21, binding.player22},
            {binding.player31, binding.player32},
            {binding.player41, binding.player42},
            {binding.player51, binding.player52},
            {binding.player61, binding.player62},
            {binding.player71, binding.player72},
            {binding.player81, binding.player82},
            {binding.player91, binding.player92},
            {binding.player101, binding.player102},
        };

        player_row_array = new LinearLayout[] {
            binding.player3row,
            binding.player4row,
            binding.player5row,
            binding.player6row,
            binding.player7row,
            binding.player8row,
            binding.player9row,
            binding.player10row
        };

        win_array = new TextView[] {
            binding.winplayer1,
            binding.winplayer2,
            binding.winplayer3,
            binding.winplayer4,
            binding.winplayer5,
            binding.winplayer6,
            binding.winplayer7,
            binding.winplayer8,
            binding.winplayer9,
            binding.winplayer10
        };

        binding.flop1.setOnClickListener(selector_listener);
        binding.flop2.setOnClickListener(selector_listener);
        binding.flop3.setOnClickListener(selector_listener);
        binding.turn.setOnClickListener(selector_listener);
        binding.river.setOnClickListener(selector_listener);
        binding.player11.setOnClickListener(selector_listener);
        binding.player12.setOnClickListener(selector_listener);
        binding.player21.setOnClickListener(selector_listener);
        binding.player22.setOnClickListener(selector_listener);
        binding.player31.setOnClickListener(selector_listener);
        binding.player32.setOnClickListener(selector_listener);
        binding.player41.setOnClickListener(selector_listener);
        binding.player42.setOnClickListener(selector_listener);
        binding.player51.setOnClickListener(selector_listener);
        binding.player52.setOnClickListener(selector_listener);
        binding.player61.setOnClickListener(selector_listener);
        binding.player62.setOnClickListener(selector_listener);
        binding.player71.setOnClickListener(selector_listener);
        binding.player72.setOnClickListener(selector_listener);
        binding.player81.setOnClickListener(selector_listener);
        binding.player82.setOnClickListener(selector_listener);
        binding.player91.setOnClickListener(selector_listener);
        binding.player92.setOnClickListener(selector_listener);
        binding.player101.setOnClickListener(selector_listener);
        binding.player102.setOnClickListener(selector_listener);


        binding.removeplayer1.setOnClickListener(remove_player_listener);
        binding.removeplayer2.setOnClickListener(remove_player_listener);
        binding.removeplayer3.setOnClickListener(remove_player_listener);
        binding.removeplayer4.setOnClickListener(remove_player_listener);
        binding.removeplayer5.setOnClickListener(remove_player_listener);
        binding.removeplayer6.setOnClickListener(remove_player_listener);
        binding.removeplayer7.setOnClickListener(remove_player_listener);
        binding.removeplayer8.setOnClickListener(remove_player_listener);
        binding.removeplayer9.setOnClickListener(remove_player_listener);
        binding.removeplayer10.setOnClickListener(remove_player_listener);

        binding.addplayer.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(players_remaining_no < 10){
                    players_remaining_no++;
                    binding.playersremaining.setText("Players remaining: " + String.valueOf(players_remaining_no));
                    player_row_array[players_remaining_no - 3].setVisibility(View.VISIBLE);
                    calculate_odds();
                }
                else{
                    Toast.makeText(MainActivity.this, "Max number of players is 10", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.clear.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                int i;

                binding.player11.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player12.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.flop1.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.flop2.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.flop3.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.turn.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.river.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player21.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player22.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player31.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player32.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player41.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player42.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player51.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player52.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player61.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player62.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player71.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player72.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player81.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player82.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player91.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player92.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player101.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                binding.player102.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));

                for(i = 0; i < players_remaining_no; i++) {
                    win_array[i].setText("Win%:");
                    win_array[i].setTextColor(Color.WHITE);
                }

                for(i = 0; i < 5; i++){
                    cards[0][i][0] = 0;
                    cards[0][i][1] = 0;
                }

                for(i = 1; i <= players_remaining_no; i++){
                    cards[i][0][0] = 0;
                    cards[i][0][1] = 0;
                    cards[i][1][0] = 0;
                    cards[i][1][1] = 0;
                }

                calculate_odds();
            }
        });
    }

    private final View.OnClickListener remove_player_listener = new View.OnClickListener() {
        public void onClick(View v) {

            final Button remove_input = (Button) v;
            int player_remove_number = convert_remove_to_player(remove_input.getId());
            int i;

            if(players_remaining_no > 2){
                players_remaining_no--;
                binding.playersremaining.setText("Players remaining: " + String.valueOf(players_remaining_no));
                player_row_array[players_remaining_no - 2].setVisibility(View.GONE);

                for (i = player_remove_number; i <= players_remaining_no; i++ ){
                    cards[i][0][0] = cards[i + 1][0][0];
                    cards[i][0][1] = cards[i + 1][0][1];
                    cards[i][1][0] = cards[i + 1][1][0];
                    cards[i][1][1] = cards[i + 1][1][1];
                    player_cards_array[i - 1][0].setBackgroundResource(getResources().getIdentifier(convert_number_to_suit(cards[i][0][0]) + convert_number_to_rank(cards[i][0][1]) + "_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                    player_cards_array[i - 1][1].setBackgroundResource(getResources().getIdentifier(convert_number_to_suit(cards[i][1][0]) + convert_number_to_rank(cards[i][1][1]) + "_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                }

                cards[players_remaining_no + 1][0][0] = 0;
                cards[players_remaining_no + 1][0][1] = 0;
                cards[players_remaining_no + 1][1][0] = 0;
                cards[players_remaining_no + 1][1][1] = 0;
                player_cards_array[players_remaining_no][0].setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                player_cards_array[players_remaining_no][1].setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));

                for(i = 0; i < players_remaining_no + 1; i++) {
                    win_array[i].setText("Win%:");
                    win_array[i].setTextColor(Color.WHITE);
                }

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

            dialog = new Dialog(MainActivity.this);
            binding_card_selector = CardselectorBinding.inflate(LayoutInflater.from(MainActivity.this));
            dialog.setContentView(binding_card_selector.getRoot());
            dialog.setTitle("Select Card");
            dialog.setCancelable(true);

            rank_checked_id = -1;

            binding_card_selector.radioGroupSuit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    int i, j;

                    if(rank_checked_id != -1) {
                        cards[convert_selector_to_player(selector_input.getId())][convert_selector_to_position(selector_input.getId())][0] = convert_id_to_number(binding_card_selector.radioGroupSuit.getCheckedRadioButtonId());
                        cards[convert_selector_to_player(selector_input.getId())][convert_selector_to_position(selector_input.getId())][1] = convert_id_to_number(rank_checked_id);
                        selector_input.setBackgroundResource(getResources().getIdentifier(convert_id_to_text(binding_card_selector.radioGroupSuit.getCheckedRadioButtonId()) + convert_id_to_text(rank_checked_id) + "_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                        rank_checked_id = -1;
                        dialog.dismiss();
                        calculate_odds();
                    }
                    else{

                        binding_card_selector.radio2.setVisibility(View.VISIBLE);
                        binding_card_selector.radio3.setVisibility(View.VISIBLE);
                        binding_card_selector.radio4.setVisibility(View.VISIBLE);
                        binding_card_selector.radio5.setVisibility(View.VISIBLE);
                        binding_card_selector.radio6.setVisibility(View.VISIBLE);
                        binding_card_selector.radio7.setVisibility(View.VISIBLE);
                        binding_card_selector.radio8.setVisibility(View.VISIBLE);
                        binding_card_selector.radio9.setVisibility(View.VISIBLE);
                        binding_card_selector.radio10.setVisibility(View.VISIBLE);
                        binding_card_selector.radio11.setVisibility(View.VISIBLE);
                        binding_card_selector.radio12.setVisibility(View.VISIBLE);
                        binding_card_selector.radio13.setVisibility(View.VISIBLE);
                        binding_card_selector.radio14.setVisibility(View.VISIBLE);

                        for(i = 0; i < 5; i++){
                            if(cards[0][i][0] == convert_id_to_number(binding_card_selector.radioGroupSuit.getCheckedRadioButtonId())){
                                hide_rank_radio_button(cards[0][i][1]);
                            }
                        }

                        for(i = 1; i <= players_remaining_no; i++){
                            for(j = 0; j <  2; j++){
                                if(cards[i][j][0] == convert_id_to_number(binding_card_selector.radioGroupSuit.getCheckedRadioButtonId())) {
                                    hide_rank_radio_button(cards[i][j][1]);
                                }
                            }
                        }
                    }
                }
            });

            binding_card_selector.radio2.setOnClickListener(rank_listener);
            binding_card_selector.radio3.setOnClickListener(rank_listener);
            binding_card_selector.radio4.setOnClickListener(rank_listener);
            binding_card_selector.radio5.setOnClickListener(rank_listener);
            binding_card_selector.radio6.setOnClickListener(rank_listener);
            binding_card_selector.radio7.setOnClickListener(rank_listener);
            binding_card_selector.radio8.setOnClickListener(rank_listener);
            binding_card_selector.radio9.setOnClickListener(rank_listener);
            binding_card_selector.radio10.setOnClickListener(rank_listener);
            binding_card_selector.radio11.setOnClickListener(rank_listener);
            binding_card_selector.radio12.setOnClickListener(rank_listener);
            binding_card_selector.radio13.setOnClickListener(rank_listener);
            binding_card_selector.radio14.setOnClickListener(rank_listener);

            binding_card_selector.buttonUnknown.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    cards[convert_selector_to_player(selector_input.getId())][convert_selector_to_position(selector_input.getId())][0] = 0;
                    cards[convert_selector_to_player(selector_input.getId())][convert_selector_to_position(selector_input.getId())][1] = 0;
                    selector_input.setBackgroundResource(getResources().getIdentifier("unknown_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                    dialog.dismiss();
                    calculate_odds();
                }
            });

            dialog.show();
        }
    };

    private final View.OnClickListener rank_listener = new View.OnClickListener() {
        public void onClick(View v) {

            final RadioButton rank_input = (RadioButton) v;
            int i, j;

            rank_checked_id = rank_input.getId();

            binding_card_selector.radio2.setChecked(false);
            binding_card_selector.radio3.setChecked(false);
            binding_card_selector.radio4.setChecked(false);
            binding_card_selector.radio5.setChecked(false);
            binding_card_selector.radio6.setChecked(false);
            binding_card_selector.radio7.setChecked(false);
            binding_card_selector.radio8.setChecked(false);
            binding_card_selector.radio9.setChecked(false);
            binding_card_selector.radio10.setChecked(false);
            binding_card_selector.radio11.setChecked(false);
            binding_card_selector.radio12.setChecked(false);
            binding_card_selector.radio13.setChecked(false);
            binding_card_selector.radio14.setChecked(false);

            rank_input.setChecked(true);

            if(binding_card_selector.radioGroupSuit.getCheckedRadioButtonId() != -1) {
                cards[convert_selector_to_player(selector_input.getId())][convert_selector_to_position(selector_input.getId())][0] = convert_id_to_number(binding_card_selector.radioGroupSuit.getCheckedRadioButtonId());
                cards[convert_selector_to_player(selector_input.getId())][convert_selector_to_position(selector_input.getId())][1] = convert_id_to_number(rank_checked_id);
                selector_input.setBackgroundResource(getResources().getIdentifier(convert_id_to_text(binding_card_selector.radioGroupSuit.getCheckedRadioButtonId()) + convert_id_to_text(rank_checked_id) + "_button", "drawable", "com.leslie.cjpokeroddscalculator"));
                rank_checked_id = -1;
                dialog.dismiss();
                calculate_odds();
            }
            else{
                binding_card_selector.radioDiamond.setVisibility(View.VISIBLE);
                binding_card_selector.radioClub.setVisibility(View.VISIBLE);
                binding_card_selector.radioHeart.setVisibility(View.VISIBLE);
                binding_card_selector.radioSpade.setVisibility(View.VISIBLE);

                for(i = 0; i < 5; i++){
                    if(cards[0][i][1] == convert_id_to_number(rank_checked_id)){
                        hide_suit_radio_button(cards[0][i][0]);
                    }
                }

                for(i = 1; i <= players_remaining_no; i++){
                    for(j = 0; j <  2; j++){
                        if(cards[i][j][1] == convert_id_to_number(rank_checked_id)){
                            hide_suit_radio_button(cards[i][j][0]);
                        }
                    }
                }
            }
        }
    };

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
        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread(null, doBackgroundProc);
        thread.start();
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private final Runnable doBackgroundProc = new Runnable(){
        public void run(){
            try {
                poker_calculation(cards, players_remaining_no, no_of_simulations);

                handler.post(new Runnable() {
                    public void run() {

                        for(int i = 0; i < players_remaining_no; i++) {
                            win_array[i].setText("Win%: " + String.valueOf((float) Math.round(equity[i] * 1000) / 10) + "%");

                            if(equity[i] > 1 / (float) players_remaining_no + 0.02) {
                                win_array[i].setTextColor(Color.GREEN);
                            } else if (equity[i] < 1 / (float) players_remaining_no - 0.02) {
                                win_array[i].setTextColor(Color.RED);
                            } else {
                                win_array[i].setTextColor(Color.WHITE);
                            }
                        }

                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
            } catch (InterruptedException e) {
                return ;
            }
        }
    };

    private void poker_calculation(int[][][] all_cards, int players_remaining_no, int no_of_simulations) throws InterruptedException {

        int i, j, split_total, no_of_known = 0, no_of_unknown = 0, no_of_unknown_players = 0;
        int[][] unknown_positions = new int[25][2];
        int[] game_stats = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] known_players = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] deck = new int[52];
        float known_players_equity = 0;
        int[][][] all_cards_copy = new int[11][][];

        for(i = 0; i < 10; i++) {
            equity[i] = 0;
        }

        for(i = 0; i < 52; i++) {
            deck[i] = i;
        }

        for(i = 0; i < 5; i++) {
            if(all_cards[0][i][0] == 0) {
                unknown_positions[no_of_unknown][0] = 0;
                unknown_positions[no_of_unknown][1] = i;
                no_of_unknown++;
            } else {
                deck[(all_cards[0][i][0] - 1) * 13 + all_cards[0][i][1] - 2] = 52;
                no_of_known++;
            }
        }

        for(i = 1; i <= players_remaining_no; i++) {
            for(j = 0; j < 2; j++) {
                if(all_cards[i][j][0] == 0) {
                    unknown_positions[no_of_unknown][0] = i;
                    unknown_positions[no_of_unknown][1] = j;
                    no_of_unknown++;
                } else {
                    deck[(all_cards[i][j][0] - 1) * 13 + all_cards[i][j][1] - 2] = 52;
                    no_of_known++;
                }
            }

            if(all_cards[i][0][0] != 0 || all_cards[i][1][0] != 0) {
                known_players[i - 1] = 1;
            } else {
                no_of_unknown_players++;
            }
        }

        insertion_srt_array(deck, 52);

        for (i = 0; i < 11; i++) {
            all_cards_copy[i] = new int[all_cards[i].length][];
            for (j = 0; j < all_cards[i].length; j++) {
                all_cards_copy[i][j] = Arrays.copyOf(all_cards[i][j], all_cards[i][j].length);
            }
        }

        int[] random_numbers = new int[no_of_unknown];

//		    	no_of_simulations = 1;

        for(i = 0; i < no_of_simulations; i++){
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            random_numbers = random_no_generator(no_of_unknown, no_of_known, deck);

            for(j = 0; j < no_of_unknown; j++){
                all_cards_copy[unknown_positions[j][0]][unknown_positions[j][1]][0] = random_numbers[j] / 13 + 1;
                all_cards_copy[unknown_positions[j][0]][unknown_positions[j][1]][1] = random_numbers[j] % 13 + 2;
            }

            game_stats = game_judge(all_cards_copy, players_remaining_no, known_players);

            split_total = 0;
            for(j = 0; j < 10; j++) {
                split_total += game_stats[j];
            }

            for(j = 0; j < players_remaining_no; j++) {
                if(known_players[j] == 1) {
                    equity[j] += (float) game_stats[j] / (float) split_total;
                }
            }
        }

        for(i = 0; i < players_remaining_no; i++) {
            if(known_players[i] == 1) {
                equity[i] = equity[i] / no_of_simulations;
                known_players_equity += equity[i];
            }
        }

        for(i = 0; i < players_remaining_no; i++) {
            if(known_players[i] == 0) {
                equity[i] = (1 - known_players_equity) / no_of_unknown_players;
            }
        }
    }

    private void insertion_srt_array(int[] deck, int n) {
        for (int i = 1; i < n; i++) {
            int j = i;
            int B = deck[i];
            while ((j > 0) && (deck[j-1] > B)) {
                deck[j] = deck[j-1];
                j--;
            }
            deck[j] = B;
        }
    }


    private int[] game_judge(int[][][] all_cards, int players_remaining_no, int[] known_players) {
        int[] game_stats = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int i, j, k, unknown_player_win = 0;
        int[][] pre_sorted_cards = new int[5][2];
        int[][] sorted_cards = new int[7][2];
        int[] best_hand = {0, 0, 0, 0, 0, 0};
        int[] decision;

        for(i = 0; i < 5; i++) {
            pre_sorted_cards[i][0] = all_cards[0][i][0];
            pre_sorted_cards[i][1] = all_cards[0][i][1];
        }

        insertion_srt(pre_sorted_cards, 5);

        for(i = 1; i <= players_remaining_no; i++) {
            if(known_players[i - 1] == 1) {
                for(j = 0; j < 5; j++) {
                    sorted_cards[j][0] = pre_sorted_cards[j][0];
                    sorted_cards[j][1] = pre_sorted_cards[j][1];
                }

                sorted_cards[5][0] = all_cards[i][0][0];
                sorted_cards[5][1] = all_cards[i][0][1];
                sorted_cards[6][0] = all_cards[i][1][0];
                sorted_cards[6][1] = all_cards[i][1][1];

                insertion_srt(sorted_cards, 7);

                decision = decide_hand(best_hand, sorted_cards);

                for(j = 0; j < 6; j++) {
                    if(decision[j] > best_hand[j]) {
                        best_hand = decision;
                        for(k = 0; k < 10; k++) {
                            game_stats[k] = 0;
                        }
                        game_stats[i - 1] = 1;
                        break;
                    } else if(decision[j] < best_hand[j]) {
                        break;
                    }
                }

                if(j == 6) {
                    game_stats[i - 1] = 1;
                }
            }
        }

        for(i = 1; i <= players_remaining_no; i++) {
            if(known_players[i - 1] == 0) {
                for(j = 0; j < 5; j++) {
                    sorted_cards[j][0] = pre_sorted_cards[j][0];
                    sorted_cards[j][1] = pre_sorted_cards[j][1];
                }

                sorted_cards[5][0] = all_cards[i][0][0];
                sorted_cards[5][1] = all_cards[i][0][1];
                sorted_cards[6][0] = all_cards[i][1][0];
                sorted_cards[6][1] = all_cards[i][1][1];

                insertion_srt(sorted_cards, 7);

                decision = decide_hand(best_hand, sorted_cards);

                for(j = 0; j < 6; j++) {
                    if(decision[j] > best_hand[j]) {
                        for(k = 0; k < 10; k++) {
                            game_stats[k] = 0;
                        }
                        game_stats[i - 1] = 1;
                        unknown_player_win = 1;
                        break;
                    } else if(decision[j] < best_hand[j]) {
                        break;
                    }
                }

                if(unknown_player_win == 1) {
                    break;
                }

                if(j == 6) {
                    game_stats[i - 1] = 1;
                }
            }
        }

        return game_stats;
    }


    private void insertion_srt(int[][] pre_sorted_cards, int n) {

        for (int i = 1; i < n; i++) {

            int j = i;
            int B0 = pre_sorted_cards[i][0];
            int B1 = pre_sorted_cards[i][1];

            while (j > 0 && pre_sorted_cards[j - 1][1] < B1) {
                pre_sorted_cards[j][0] = pre_sorted_cards[j - 1][0];
                pre_sorted_cards[j][1] = pre_sorted_cards[j - 1][1];
                j--;
            }

            pre_sorted_cards[j][0] = B0;
            pre_sorted_cards[j][1] = B1;
        }
    }


    private int[] decide_hand(int[] best_hand, int[][] sorted_cards) {
        int[] decision;

        decision = straight_flush(sorted_cards);

        if(decision[0] > 0) {
            return decision;
        } else if(best_hand[0] == 9) {
            return decision;
        }

        decision = four_of_a_kind(sorted_cards);

        if(decision[0] > 0) {
            return decision;
        } else if(best_hand[0] >= 8) {
            return decision;
        }

        decision = full_house(sorted_cards);

        if(decision[0] > 0) {
            return decision;
        } else if(best_hand[0] >= 7) {
            return decision;
        }

        decision = flush(sorted_cards);

        if(decision[0] > 0) {
            return decision;
        } else if(best_hand[0] >= 6) {
            return decision;
        }

        decision = straight(sorted_cards);

        if(decision[0] > 0) {
            return decision;
        } else if(best_hand[0] >= 5) {
            return decision;
        }

        decision = three_of_a_kind(sorted_cards);

        if(decision[0] > 0) {
            return decision;
        } else if(best_hand[0] >= 4) {
            return decision;
        }

        decision = pairs(sorted_cards);

        if(decision[0] > 0) {
            return decision;
        } else if(best_hand[0] >= 2) {
            return decision;
        }

        decision = high_card(sorted_cards);

        return decision;
    }


    private int[] high_card(int[][] sorted_cards) {
        int[] decision = {0, 0, 0, 0, 0, 0};

        decision[0] = 1;
        decision[1] = sorted_cards[0][1];
        decision[2] = sorted_cards[1][1];
        decision[3] = sorted_cards[2][1];
        decision[4] = sorted_cards[3][1];
        decision[5] = sorted_cards[4][1];

        return decision;
    }


    private int[] pairs(int[][] sorted_cards) {
        int[] decision = {0, 0, 0, 0, 0, 0};
        int i, j, k, kicker = 0;

        for(i = 0; i <= 5; i++) {
            if(sorted_cards[i][1] == sorted_cards[i + 1][1]) {
                for(j = i + 2; j <= 5; j++) {
                    if(sorted_cards[j][1] == sorted_cards[j + 1][1]) {
                        for(k = 0; k <= 4; k++) {
                            if(sorted_cards[k][1] != sorted_cards[i][1] && sorted_cards[k][1] != sorted_cards[j][1]) {
                                decision[0] = 3;
                                decision[1] = sorted_cards[i][1];
                                decision[2] = sorted_cards[j][1];
                                decision[3] = sorted_cards[k][1];
                                return decision;
                            }
                        }
                    }
                }

                decision[0] = 2;
                decision[1] = sorted_cards[i][1];

                for(j = 0; j <= 4; j++) {
                    if(sorted_cards[j][1] != sorted_cards[i][1]) {
                        kicker++;
                        decision[kicker + 1] = sorted_cards[j][1];
                        if(kicker == 3) {
                            return decision;
                        }
                    }
                }
            }
        }

        return decision;
    }


    private int[] three_of_a_kind(int[][] sorted_cards) {
        int[] decision = {0, 0, 0, 0, 0, 0};
        int i, j, kicker = 0;

        for(i = 0; i <= 4; i++) {
            for(j = i + 1; j <= i + 2; j++) {
                if(sorted_cards[i][1] != sorted_cards[j][1]) {
                    break;
                }
            }

            if(j == i + 3) {
                decision[0] = 4;
                decision[1] = sorted_cards[i][1];

                for(j = 0; j <= 6; j++) {
                    if(sorted_cards[j][1] != sorted_cards[i][1]) {
                        kicker++;
                        decision[kicker + 1] = sorted_cards[j][1];
                        if(kicker == 2) {
                            return decision;
                        }
                    }
                }
            }
        }

        return decision;
    }


    private int[] straight(int[][] sorted_cards) {
        int[] decision = {0, 0, 0, 0, 0, 0};
        int i, j, k;

        for(i = 0; i <= 2; i++) {
            for(j = 1; j <= 4; j++ ) {
                for(k = i + 1; k <= 6; k++) {
                    if(sorted_cards[i][1] - j == sorted_cards[k][1]) {
                        break;
                    }
                }
                if(k == 7) {
                    break;
                }
            }
            if(j == 5) {
                decision[0] = 5;
                decision[1] = sorted_cards[i][1];
                return decision;
            }
        }

        if(sorted_cards[0][1] == 14) {
            for(j = 2; j <= 5; j++) {
                for(k = 1; k <= 6; k++) {
                    if(sorted_cards[k][1] == j) {
                        break;
                    }
                }
                if(k == 7) {
                    break;
                }
            }
            if(j == 6) {
                decision[0] = 5;
                decision[1] = 5;
                return decision;
            }
        }
        return decision;
    }


    private int[] flush(int[][] sorted_cards) {
        int[] decision = {0, 0, 0, 0, 0, 0};
        int i, j, count;

        for(i = 0; i <= 2; i++) {
            decision[1] = sorted_cards[i][1];
            count = 1;
            for(j = i + 1; j <= 6; j++) {
                if(sorted_cards[i][0] == sorted_cards[j][0]) {
                    count++;
                    decision[count] = sorted_cards[j][1];
                    if(count == 5) {
                        decision[0] = 6;
                        return decision;
                    }
                }
            }
        }

        return decision;
    }


    private int[] full_house(int[][] sorted_cards) {
        int[] decision = {0, 0, 0, 0, 0, 0};
        int i, j;

        for(i = 0; i <= 4; i++) {
            for(j = i + 1; j <= i + 2; j++) {
                if(sorted_cards[i][1] != sorted_cards[j][1]) {
                    break;
                }
            }

            if(j == i + 3) {
                for(j = 0; j <= 5; j++) {
                    if(sorted_cards[j][1] == sorted_cards[j + 1][1] && sorted_cards[j][1] != sorted_cards[i][1]) {
                        decision[0] = 7;
                        decision[1] = sorted_cards[i][1];
                        decision[2] = sorted_cards[j][1];
                        return decision;
                    }
                }
                return decision;
            }

        }

        return decision;
    }


    private int[] four_of_a_kind(int[][] sorted_cards) {
        int[] decision = {0, 0, 0, 0, 0, 0};
        int i, j;

        for(i = 0; i <= 3; i++) {
            for(j = i + 1; j <= i + 3; j++) {
                if(sorted_cards[i][1] != sorted_cards[j][1]) {
                    break;
                }
            }

            if(j == i + 4) {
                decision[0] = 8;
                decision[1] = sorted_cards[i][1];

                for(j = 0; j <= 4; j++) {
                    if(sorted_cards[j][1] != sorted_cards[i][1]) {
                        decision[2] = sorted_cards[j][1];
                        return decision;
                    }
                }
            }
        }

        return decision;
    }


    private int[] straight_flush(int[][] sorted_cards) {
        int[] decision = {0, 0, 0, 0, 0, 0};
        int i, j, k;

        for(i = 0; i <= 2; i++) {
            for(j = 1; j <= 4; j++ ) {
                for(k = i + 1; k <= 6; k++) {
                    if(sorted_cards[i][0] == sorted_cards[k][0] && sorted_cards[i][1] - j == sorted_cards[k][1]) {
                        break;
                    }
                }
                if(k == 7) {
                    break;
                }
            }
            if(j == 5) {
                decision[0] = 9;
                decision[1] = sorted_cards[i][1];
                return decision;
            }
        }

        for(i = 0; i <= 2; i++) {
            if(sorted_cards[i][1] == 14) {
                for(j = 2; j <= 5; j++) {
                    for(k = i + 1; k <= 6; k++) {
                        if(sorted_cards[k][0] == sorted_cards[i][0] && sorted_cards[k][1] == j) {
                            break;
                        }
                    }
                    if(k == 7) {
                        break;
                    }
                }
                if(j == 6) {
                    decision[0] = 9;
                    decision[1] = 5;
                    return decision;
                }
            }
        }
        return decision;
    }

    private int[] random_no_generator(int no_of_unknown, int no_of_known, int[] deck) {

        int i, temp;
        int[] random_numbers = new int[no_of_unknown];
        int[] deck2 = new int[52];

        for(i = 0; i < 52; i++) {
            deck2[i] = deck[i];
        }

        for(i = 0; i < no_of_unknown; i++){
            temp = myRandom.nextInt(52 - no_of_known - i);
            random_numbers[i] = deck2[temp];

            deck2[temp] = deck2[51 - no_of_known - i];
        }

        return random_numbers;
    }

    public int convert_selector_to_player(int ButtonId) {
        if (ButtonId == R.id.player11 || ButtonId == R.id.player12) {
            return 1;
        }
        else if (ButtonId == R.id.flop1 || ButtonId == R.id.flop2 || ButtonId == R.id.flop3 || ButtonId == R.id.turn || ButtonId == R.id.river ) {
            return 0;
        }
        else if (ButtonId == R.id.player21 || ButtonId == R.id.player22) {
            return 2;
        }
        else if (ButtonId == R.id.player31 || ButtonId == R.id.player32) {
            return 3;
        }
        else if (ButtonId == R.id.player41 || ButtonId == R.id.player42) {
            return 4;
        }
        else if (ButtonId == R.id.player51 || ButtonId == R.id.player52) {
            return 5;
        }
        else if (ButtonId == R.id.player61 || ButtonId == R.id.player62) {
            return 6;
        }
        else if (ButtonId == R.id.player71 || ButtonId == R.id.player72) {
            return 7;
        }
        else if (ButtonId == R.id.player81 || ButtonId == R.id.player82) {
            return 8;
        }
        else if (ButtonId == R.id.player91 || ButtonId == R.id.player92) {
            return 9;
        }
        else if (ButtonId == R.id.player101 || ButtonId == R.id.player102) {
            return 10;
        }

        return ButtonId;
    }

    public int convert_selector_to_position(int ButtonId) {
        if (
            ButtonId == R.id.player11 ||
            ButtonId == R.id.flop1 ||
            ButtonId == R.id.player21 ||
            ButtonId == R.id.player31 ||
            ButtonId == R.id.player41 ||
            ButtonId == R.id.player51 ||
            ButtonId == R.id.player61 ||
            ButtonId == R.id.player71 ||
            ButtonId == R.id.player81 ||
            ButtonId == R.id.player91 ||
            ButtonId == R.id.player101
        ) {
            return 0;
        }
        else if (
            ButtonId == R.id.player12 ||
            ButtonId == R.id.flop2 ||
            ButtonId == R.id.player22 ||
            ButtonId == R.id.player32 ||
            ButtonId == R.id.player42 ||
            ButtonId == R.id.player52 ||
            ButtonId == R.id.player62 ||
            ButtonId == R.id.player72 ||
            ButtonId == R.id.player82 ||
            ButtonId == R.id.player92 ||
            ButtonId == R.id.player102
        ) {
            return 1;
        }
        else if (ButtonId == R.id.flop3) {
            return 2;
        }
        else if (ButtonId == R.id.turn) {
            return 3;
        }
        else if (ButtonId == R.id.river) {
            return 4;
        }

        return ButtonId;
    }

    public int convert_remove_to_player(int ButtonId) {
        if (ButtonId == R.id.removeplayer1) {
            return 1;
        }
        else if (ButtonId == R.id.removeplayer2) {
            return 2;
        }
        else if (ButtonId == R.id.removeplayer3) {
            return 3;
        }
        else if (ButtonId == R.id.removeplayer4) {
            return 4;
        }
        else if (ButtonId == R.id.removeplayer5) {
            return 5;
        }
        else if (ButtonId == R.id.removeplayer6) {
            return 6;
        }
        else if (ButtonId == R.id.removeplayer7) {
            return 7;
        }
        else if (ButtonId == R.id.removeplayer8) {
            return 8;
        }
        else if (ButtonId == R.id.removeplayer9) {
            return 9;
        }
        else if (ButtonId == R.id.removeplayer10) {
            return 10;
        }

        return ButtonId;
    }

    public String convert_number_to_suit(int suit_no) {
        switch(suit_no) {
            case 0:
                return "unknown";
            case 1:
                return "d";
            case 2:
                return "c";
            case 3:
                return "h";
            case 4:
                return "s";
        }
        return null;
    }

    public String convert_number_to_rank(int rank_no) {
        switch(rank_no) {
            case 0:
                return "";
            case 2:
                return "2";
            case 3:
                return "3";
            case 4:
                return "4";
            case 5:
                return "5";
            case 6:
                return "6";
            case 7:
                return "7";
            case 8:
                return "8";
            case 9:
                return "9";
            case 10:
                return "10";
            case 11:
                return "11";
            case 12:
                return "12";
            case 13:
                return "13";
            case 14:
                return "14";
        }
        return null;
    }

    public String convert_id_to_text(int checkedRadioButtonId) {
        if (checkedRadioButtonId == R.id.radio_2) {
            return "2";
        }
        else if (checkedRadioButtonId == R.id.radio_3) {
            return "3";
        }
        else if (checkedRadioButtonId == R.id.radio_4) {
            return "4";
        }
        else if (checkedRadioButtonId == R.id.radio_5) {
            return "5";
        }
        else if (checkedRadioButtonId == R.id.radio_6) {
            return "6";
        }
        else if (checkedRadioButtonId == R.id.radio_7) {
            return "7";
        }
        else if (checkedRadioButtonId == R.id.radio_8) {
            return "8";
        }
        else if (checkedRadioButtonId == R.id.radio_9) {
            return "9";
        }
        else if (checkedRadioButtonId == R.id.radio_10) {
            return "10";
        }
        else if (checkedRadioButtonId == R.id.radio_11) {
            return "11";
        }
        else if (checkedRadioButtonId == R.id.radio_12) {
            return "12";
        }
        else if (checkedRadioButtonId == R.id.radio_13) {
            return "13";
        }
        else if (checkedRadioButtonId == R.id.radio_14) {
            return "14";
        }
        else if (checkedRadioButtonId == R.id.radio_diamond) {
            return "d";
        }
        else if (checkedRadioButtonId == R.id.radio_club) {
            return "c";
        }
        else if (checkedRadioButtonId == R.id.radio_heart) {
            return "h";
        }
        else if (checkedRadioButtonId == R.id.radio_spade) {
            return "s";
        }

        return null;
    }

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

}