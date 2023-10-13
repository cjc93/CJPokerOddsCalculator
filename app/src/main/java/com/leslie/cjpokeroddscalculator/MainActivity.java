package com.leslie.cjpokeroddscalculator;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.leslie.cjpokeroddscalculator.calculation.ExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.MonteCarloCalc;
import com.leslie.cjpokeroddscalculator.databinding.ActivityMainBinding;
import com.leslie.cjpokeroddscalculator.databinding.PlayerRowBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.HashBiMap;

public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding;
    private long startClickTime;

    private ImageButton selected_card_button = null;
    private final int[] selected_card_position = new int[2];
    public Thread monte_carlo_thread = null;
    public Thread exact_calc_thread = null;

    public int players_remaining_no = 2;

    private final LinearLayout[] player_row_array = new LinearLayout[10];
    public TextView[] win_array = new TextView[10];

    HashBiMap<List<Integer>, ImageButton> cardPositionBiMap = HashBiMap.create();
    HashMap<Button, Integer> remove_row_map = new HashMap<>();
    HashMap<Integer, HashMap<Integer, Integer>> suit_rank_drawable_map = new HashMap<>();
    HashBiMap<ImageButton, List<Integer>> input_suit_rank_map = HashBiMap.create();

    CardRow[] cardRows = new CardRow[11];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialise_variables();

        generate_main_layout();

        binding.playersremaining.setText(getString(R.string.players_remaining, players_remaining_no));
        win_array[0].setText(getString(R.string.win_perc_populated, 50.0));
        win_array[1].setText(getString(R.string.win_perc_populated, 50.0));

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
            for (List<Integer> position : cardPositionBiMap.keySet()) {
                set_card_value(position.get(0), position.get(1), 0, 0);
            }

            binding.scrollView.post(() -> binding.scrollView.smoothScrollTo(0, 0));

            calculate_odds();
        });

        binding.buttonUnknown.setOnClickListener(v -> set_value_to_selected_card(0, 0));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startClickTime = System.currentTimeMillis();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
                Rect outRect = new Rect();
                boolean tapOnButton = false;

                binding.bottomBar.getGlobalVisibleRect(outRect);
                if (outRect.top < (int) event.getRawY()) {
                    tapOnButton = true;
                }

                if (!tapOnButton) {
                    for (ImageButton card : cardPositionBiMap.values()) {
                        card.getGlobalVisibleRect(outRect);
                        if (outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            tapOnButton = true;
                            break;
                        }
                    }
                }

                if (!tapOnButton) {
                    for (ImageButton card : cardPositionBiMap.values()) {
                        card.getGlobalVisibleRect(outRect);
                        if (outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            tapOnButton = true;
                            break;
                        }
                    }
                }

                if (!tapOnButton) {
                    for (Button b : remove_row_map.keySet()) {
                        b.getGlobalVisibleRect(outRect);
                        if (outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            tapOnButton = true;
                            break;
                        }
                    }
                }

                if (!tapOnButton) {
                    binding.inputCards.setVisibility(View.GONE);
                    binding.buttonUnknown.setVisibility(View.GONE);
                    selected_card_button.setBackgroundResource(0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    private void generate_main_layout() {
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

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int cardHeight = displayMetrics.heightPixels / 9;
        for (ImageButton card : cardPositionBiMap.values()) {
            card.setMaxHeight(cardHeight);
        }

        for (int i = 2; i < 10; i++) {
            player_row_array[i].setVisibility(View.GONE);
        }

        set_selected_card(1, 0);

        LinearLayout.LayoutParams rowParam = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1.0f
        );

        LinearLayout.LayoutParams buttonParam = new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1.0f
        );

        for (int suit = 1; suit <= 4; suit++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(rowParam);

            for (int rank = 2; rank <= 14; rank++) {
                ImageButton b = new ImageButton(this);
                b.setLayoutParams(buttonParam);
                b.setBackgroundResource(0);
                b.setImageResource(suit_rank_drawable_map.get(suit).get(rank));
                b.setScaleType(ImageButton.ScaleType.FIT_XY);
                b.setPadding(1, 1, 1, 1);
                b.setOnClickListener(input_card_listener);
                row.addView(b);
                input_suit_rank_map.put(b, Arrays.asList(suit, rank));
            }
            binding.inputCards.addView(row);
        }
    }

    private void initialise_variables() {
        cardRows[0] = new CardRow(5);

        for (int i = 1; i <= 10; i++) {
            cardRows[i] = new CardRow(2);
        }

        cardPositionBiMap.put(Arrays.asList(0, 0), binding.flop1);
        cardPositionBiMap.put(Arrays.asList(0, 1), binding.flop2);
        cardPositionBiMap.put(Arrays.asList(0, 2), binding.flop3);
        cardPositionBiMap.put(Arrays.asList(0, 3), binding.turn);
        cardPositionBiMap.put(Arrays.asList(0, 4), binding.river);

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

            players_remaining_no--;
            binding.playersremaining.setText(getString(R.string.players_remaining, players_remaining_no));
            player_row_array[players_remaining_no].setVisibility(View.GONE);

            for (int i = player_remove_number; i <= players_remaining_no; i++) {
                set_card_value(i, 0, cardRows[i + 1].cards[0][0], cardRows[i + 1].cards[0][1]);
                set_card_value(i, 1, cardRows[i + 1].cards[1][0], cardRows[i + 1].cards[1][1]);
            }

            set_card_value(players_remaining_no + 1, 0, 0, 0);
            set_card_value(players_remaining_no + 1, 1, 0, 0);

            if (selected_card_position[0] > player_remove_number || selected_card_position[0] == players_remaining_no + 1) {
                set_selected_card(selected_card_position[0] - 1, selected_card_position[1]);
            }

            calculate_odds();
        }
    };

    private final View.OnClickListener selector_listener = v -> {
        List<Integer> position = cardPositionBiMap.inverse().get((ImageButton) v);
        assert position != null;
        set_selected_card(position.get(0), position.get(1));
        binding.inputCards.setVisibility(View.VISIBLE);
        binding.buttonUnknown.setVisibility(View.VISIBLE);
    };

    private final View.OnClickListener input_card_listener = v -> {
        ImageButton card_input = (ImageButton) v;
        card_input.setVisibility(View.INVISIBLE);

        List<Integer> suit_rank_list = input_suit_rank_map.get(card_input);
        assert suit_rank_list != null;
        set_value_to_selected_card(suit_rank_list.get(0), suit_rank_list.get(1));
    };

    private void set_next_selected_card() {
        if ((selected_card_position[0] == 0 && selected_card_position[1] < 4) || selected_card_position[1] == 0) {
            set_selected_card(selected_card_position[0], selected_card_position[1] + 1);
        } else if ((selected_card_position[0] == 1 || selected_card_position[0] == players_remaining_no) && selected_card_position[1] == 1) {
            set_selected_card(0, 0);
        } else {
            set_selected_card(selected_card_position[0] + 1, 0);
        }

        Rect rect = new Rect();
        if(!selected_card_button.getGlobalVisibleRect(rect) || selected_card_button.getHeight() != rect.height() ) {
            binding.scrollView.post(
                () -> binding.scrollView.smoothScrollTo(
                    0,
                    ((LinearLayout) selected_card_button.getParent().getParent().getParent().getParent().getParent()).getBottom() - binding.scrollView.getHeight()
                )
            );
        }
    }

    private void set_selected_card(int row_idx, int card_idx) {
        if (selected_card_button != null) {
            selected_card_button.setBackgroundResource(0);
        }

        selected_card_position[0] = row_idx;
        selected_card_position[1] = card_idx;

        selected_card_button = cardPositionBiMap.get(Arrays.asList(row_idx, card_idx));
        assert selected_card_button != null;
        selected_card_button.setBackgroundResource(R.drawable.border_selector);
    }

    public void set_card_value(int row_idx, int card_idx, int suit, int rank) {
        int[] suit_rank_array = cardRows[row_idx].cards[card_idx];

        if (suit_rank_array[0] != 0) {
            ImageButton prev_card = input_suit_rank_map.inverse().get(Arrays.asList(suit_rank_array[0], suit_rank_array[1]));
            assert prev_card != null;
            prev_card.setVisibility(View.VISIBLE);
        }

        cardRows[row_idx].cards[card_idx][0] = suit;
        cardRows[row_idx].cards[card_idx][1] = rank;

        ImageButton card_button = cardPositionBiMap.get(Arrays.asList(row_idx, card_idx));
        assert card_button != null;
        card_button.setImageResource(suit_rank_drawable_map.get(suit).get(rank));
    }

    public void set_value_to_selected_card(int suit, int rank) {
        set_card_value(selected_card_position[0], selected_card_position[1], suit, rank);
        set_next_selected_card();
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

        binding.resDesc.setText(R.string.checking_random_subset);

        monte_carlo_thread = new Thread(null, monte_carlo_proc);
        exact_calc_thread = new Thread(null, exact_calc_proc);

        monte_carlo_thread.start();
        exact_calc_thread.start();
    }

    private final Runnable monte_carlo_proc = () -> {
        try {
            MonteCarloCalc calc_obj = new MonteCarloCalc();
            calc_obj.monteCarloCalc(cardRows, players_remaining_no, new LiveUpdate(this));
        } catch (InterruptedException ignored) { }
    };

    private final Runnable exact_calc_proc = () -> {
        try {
            ExactCalc calc_obj = new ExactCalc();
            calc_obj.exactCalc(cardRows, players_remaining_no, new FinalUpdate(this));
        } catch (InterruptedException ignored) { }
    };
}