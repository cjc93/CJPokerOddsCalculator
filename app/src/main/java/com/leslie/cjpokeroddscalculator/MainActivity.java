package com.leslie.cjpokeroddscalculator;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;
import com.leslie.cjpokeroddscalculator.calculation.ExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.MonteCarloCalc;
import com.leslie.cjpokeroddscalculator.databinding.ActivityMainBinding;
import com.leslie.cjpokeroddscalculator.databinding.PlayerRowBinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.HashBiMap;

public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding;

    private long startClickTime;

    private ImageButton selected_card_button = null;
    private final int[] selected_card_position = new int[2];

    private ImageButton selectedRangeButton = null;
    private int selectedRangePosition;

    public Thread monte_carlo_thread = null;
    public Thread exact_calc_thread = null;

    public int players_remaining_no = 2;

    private final LinearLayout[] player_row_array = new LinearLayout[10];
    public TextView[] win_array = new TextView[10];
    public HashBiMap<Integer, ImageButton> rangePositionBiMap = HashBiMap.create();
    public LinearLayout[] twoCardsLayouts = new LinearLayout[10];

    HashBiMap<List<Integer>, ImageButton> cardPositionBiMap = HashBiMap.create();
    HashMap<Button, Integer> remove_row_map = new HashMap<>();
    HashMap<Integer, HashMap<Integer, Integer>> suit_rank_drawable_map = new HashMap<>();
    HashBiMap<ImageButton, List<Integer>> input_suit_rank_map = HashBiMap.create();

    CardRow[] cardRows = new CardRow[11];

    HashBiMap<Button, List<Integer>> inputMatrixMap = HashBiMap.create();
    boolean[][] matrixInput = new boolean[13][13];
    public Bitmap emptyRangeBitmap;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    int cardHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.rangeSelector.getVisibility() == View.VISIBLE) {
                    binding.rangeSelector.setVisibility(View.GONE);
                    binding.mainUi.setVisibility(View.VISIBLE);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
        
        initialise_variables();

        generate_main_layout();

        generateRangeSelector();

        binding.playersremaining.setText(getString(R.string.players_remaining, players_remaining_no));
        win_array[0].setText(getString(R.string.win_perc_populated, 50.0));
        win_array[1].setText(getString(R.string.win_perc_populated, 50.0));

        for (ImageButton b : cardPositionBiMap.values()) {
            b.setOnClickListener(selector_listener);
        }

        for (ImageButton r : rangePositionBiMap.values()) {
            r.setOnClickListener(rangeSelectorListener);
        }

        binding.addplayer.setOnClickListener(v -> {
            if(players_remaining_no < 10){
                players_remaining_no++;
                binding.playersremaining.setText(getString(R.string.players_remaining, players_remaining_no));
                Objects.requireNonNull(rangePositionBiMap.get(players_remaining_no)).setVisibility(View.GONE);
                cardRows[players_remaining_no] = new SpecificCardsRow(2);
                setCardImage(players_remaining_no, 0, 0, 0);
                setCardImage(players_remaining_no, 1, 0, 0);
                twoCardsLayouts[players_remaining_no - 1].setVisibility(View.VISIBLE);
                player_row_array[players_remaining_no - 1].setVisibility(View.VISIBLE);
                calculate_odds();
            }
            else{
                Toast.makeText(MainActivity.this, "Max number of players is 10", Toast.LENGTH_SHORT).show();
            }
        });

        binding.addrange.setOnClickListener(v -> {
            if(players_remaining_no < 10){
                players_remaining_no++;
                binding.playersremaining.setText(getString(R.string.players_remaining, players_remaining_no));
                twoCardsLayouts[players_remaining_no - 1].setVisibility(View.GONE);
                cardRows[players_remaining_no] = new RangeRow();
                ImageButton b = this.rangePositionBiMap.get(players_remaining_no);
                assert b != null;
                b.setImageBitmap(this.emptyRangeBitmap);
                b.setVisibility(View.VISIBLE);
                player_row_array[players_remaining_no - 1].setVisibility(View.VISIBLE);
                calculate_odds();
            }
            else{
                Toast.makeText(MainActivity.this, "Max number of players is 10", Toast.LENGTH_SHORT).show();
            }
        });

        binding.clear.setOnClickListener(v -> {
            for (int i = 0; i < 11; i++) {
                if (cardRows[i] instanceof SpecificCardsRow) {
                    SpecificCardsRow cardRow = (SpecificCardsRow) cardRows[i];
                    for (int j = 0; j < cardRow.cards.length; j++) {
                        setInputCardVisible(i, j);
                    }
                }

                cardRows[i].clear(this, i);
            }

            binding.scrollView.post(() -> binding.scrollView.smoothScrollTo(0, 0));

            calculate_odds();
        });

        binding.buttonUnknown.setOnClickListener(v -> set_value_to_selected_card(0, 0));

        binding.rangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                float finalValue = 0;
                for (java.util.Map.Entry<Integer, List<Integer>> entry : GlobalStatic.bestHandsMap.entrySet()) {
                    List<Integer> matrixPosition = entry.getValue();
                    int cumulativeHands = entry.getKey();
                    if (cumulativeHands <= value) {
                        Objects.requireNonNull(inputMatrixMap.inverse().get(matrixPosition)).setBackgroundColor(Color.YELLOW);
                        finalValue = cumulativeHands;
                    } else {
                        Objects.requireNonNull(inputMatrixMap.inverse().get(matrixPosition)).setBackgroundColor(Color.LTGRAY);
                    }
                }
                slider.setValue(finalValue);
            }
            binding.handsPerc.setText(getString(R.string.hands_perc, slider.getValue() / 1326.0 * 100));
        });

        binding.rangeSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) { }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                float selectedValue = slider.getValue();
                for (java.util.Map.Entry<Integer, List<Integer>> entry : GlobalStatic.bestHandsMap.entrySet()) {
                    List<Integer> matrixPosition = entry.getValue();
                    matrixInput[matrixPosition.get(0)][matrixPosition.get(1)] = entry.getKey() <= selectedValue;
                }
            }
        });

        binding.done.setOnClickListener(v -> {
            RangeRow rangeRow = (RangeRow) this.cardRows[selectedRangePosition];
            rangeRow.matrix = new boolean[13][];
            for (int i = 0; i < 13; i++) {
                rangeRow.matrix[i] = Arrays.copyOf(this.matrixInput[i], 13);
            }

            Bitmap matrixBitmap = Bitmap.createBitmap(13, 13, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < 13; i++)  {
                for (int j = 0; j < 13; j++)  {
                    if (rangeRow.matrix[i][j]) {
                        matrixBitmap.setPixel(j, i, Color.YELLOW);
                    } else {
                        matrixBitmap.setPixel(j, i, Color.LTGRAY);
                    }
                }
            }

            selectedRangeButton.setImageBitmap(Bitmap.createScaledBitmap(matrixBitmap, cardHeight, cardHeight, false));
            matrixBitmap.recycle();

            binding.rangeSelector.setVisibility(View.GONE);
            binding.mainUi.setVisibility(View.VISIBLE);

            calculate_odds();
        });
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

    private void initialise_variables() {
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        cardHeight = displayMetrics.heightPixels / 9;

        cardRows[0] = new SpecificCardsRow(5);

        for (int i = 1; i <= 10; i++) {
            cardRows[i] = new SpecificCardsRow(2);
        }

        cardPositionBiMap.put(Arrays.asList(0, 0), binding.flop1);
        cardPositionBiMap.put(Arrays.asList(0, 1), binding.flop2);
        cardPositionBiMap.put(Arrays.asList(0, 2), binding.flop3);
        cardPositionBiMap.put(Arrays.asList(0, 3), binding.turn);
        cardPositionBiMap.put(Arrays.asList(0, 4), binding.river);

        for (int row_idx = 0; row_idx < 13; row_idx++) {
            for (int col_idx = 0; col_idx < 13; col_idx++) {
                this.matrixInput[row_idx][col_idx] = false;
            }
        }

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

    private void generate_main_layout() {
        for (int i = 0; i < 10; i++) {
            PlayerRowBinding binding_player_row = PlayerRowBinding.inflate(LayoutInflater.from(MainActivity.this), binding.playerRows, true);
            player_row_array[i] = binding_player_row.getRoot();
            win_array[i] = binding_player_row.win;
            rangePositionBiMap.put(i + 1, binding_player_row.range);
            twoCardsLayouts[i] = binding_player_row.twoCards;
            cardPositionBiMap.put(Arrays.asList(i + 1, 0), binding_player_row.card1);
            cardPositionBiMap.put(Arrays.asList(i + 1, 1), binding_player_row.card2);
            remove_row_map.put(binding_player_row.remove, i + 1);

            binding_player_row.playerText.setText(getString(R.string.player, i + 1));
            binding_player_row.remove.setOnClickListener(remove_player_listener);
        }

        for (ImageButton card : cardPositionBiMap.values()) {
            card.setMaxHeight(cardHeight);
        }

        this.emptyRangeBitmap = Bitmap.createBitmap(cardHeight, cardHeight, Bitmap.Config.ARGB_8888);
        this.emptyRangeBitmap.eraseColor(Color.LTGRAY);

        for (ImageButton r : rangePositionBiMap.values()) {
            r.setImageBitmap(emptyRangeBitmap);
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

    private void generateRangeSelector() {
        LinearLayout.LayoutParams rowParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams buttonParam = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        buttonParam.setMargins(1, 1,1,1);

        int squareHeight = displayMetrics.widthPixels / 13;

        for (int row_idx = 0; row_idx < 13; row_idx++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(rowParam);

            for (int col_idx = 0; col_idx < 13; col_idx++) {
                Button b = new Button(this);
                b.setLayoutParams(buttonParam);
                b.setPadding(1, 1, 1, 1);
                b.setHeight(squareHeight);
                b.setMinimumHeight(squareHeight);
                b.setBackgroundColor(Color.LTGRAY);
                b.setTextColor(Color.BLACK);
                b.setAllCaps(false);
                b.setMaxLines(1);
                b.setAutoSizeTextTypeUniformWithConfiguration(10, 11, 1, TypedValue.COMPLEX_UNIT_SP);
                b.setOnClickListener(matrixListener);

                if (row_idx == col_idx) {
                    b.setText(getString(R.string.matrix_str, GlobalStatic.matrixStrings[row_idx], GlobalStatic.matrixStrings[row_idx], ""));
                } else if (col_idx > row_idx) {
                    b.setText(getString(R.string.matrix_str, GlobalStatic.matrixStrings[row_idx], GlobalStatic.matrixStrings[col_idx], "s"));
                } else {
                    b.setText(getString(R.string.matrix_str, GlobalStatic.matrixStrings[col_idx], GlobalStatic.matrixStrings[row_idx], "o"));
                }

                row.addView(b);
                this.inputMatrixMap.put(b, Arrays.asList(row_idx, col_idx));
            }
            binding.rangeMatrix.addView(row);
        }
    }

    private final View.OnClickListener remove_player_listener = v -> {
        final Button remove_input = (Button) v;
        int player_remove_number = remove_row_map.get(remove_input);

        players_remaining_no--;
        binding.playersremaining.setText(getString(R.string.players_remaining, players_remaining_no));

        if (cardRows[player_remove_number] instanceof SpecificCardsRow) {
            for (int i = 0; i < 2; i++) {
                setInputCardVisible(player_remove_number, i);
            }
        }

        for (int i = player_remove_number; i <= players_remaining_no; i++) {
            cardRows[i] = cardRows[i + 1].copy();
            cardRows[i].copyImageBelow(this, i);
        }

        player_row_array[players_remaining_no].setVisibility(View.GONE);

        if (selected_card_position[0] > player_remove_number || selected_card_position[0] == players_remaining_no + 1) {
            for (int i = selected_card_position[0] - 1; i >= 0; i--) {
                if (cardRows[i] instanceof SpecificCardsRow) {
                    set_selected_card(i, selected_card_position[1]);
                    break;
                }
            }
        }

        calculate_odds();
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

    private final View.OnClickListener rangeSelectorListener = v -> {
        ImageButton rangeSelectorInput = (ImageButton) v;
        selectedRangeButton = rangeSelectorInput;
        selectedRangePosition = rangePositionBiMap.inverse().get(rangeSelectorInput);

        RangeRow rangeRow = (RangeRow) this.cardRows[selectedRangePosition];
        this.matrixInput = new boolean[13][];
        for (int i = 0; i < 13; i++) {
            this.matrixInput[i] = Arrays.copyOf(rangeRow.matrix[i], 13);
        }

        int handCount = 0;

        for (int row_idx = 0; row_idx < 13; row_idx++) {
            for (int col_idx = 0; col_idx < 13; col_idx++) {
                if (this.matrixInput[row_idx][col_idx]) {
                    Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(row_idx, col_idx))).setBackgroundColor(Color.YELLOW);

                    if (row_idx == col_idx) {
                        handCount += 6;
                    } else if (col_idx > row_idx) {
                        handCount += 4;
                    } else {
                        handCount += 12;
                    }
                } else {
                    Objects.requireNonNull(this.inputMatrixMap.inverse().get(Arrays.asList(row_idx, col_idx))).setBackgroundColor(Color.LTGRAY);
                }
            }
        }

        binding.rangeSlider.setValue(handCount);

        binding.mainUi.setVisibility(View.GONE);
        binding.rangeSelector.setVisibility(View.VISIBLE);
    };

    private final View.OnClickListener matrixListener = v -> {
        Button matrixButton = (Button) v;

        List<Integer> matrixPosition = inputMatrixMap.get(matrixButton);
        assert matrixPosition != null;

        if (matrixInput[matrixPosition.get(0)][matrixPosition.get(1)]) {
            matrixInput[matrixPosition.get(0)][matrixPosition.get(1)] = false;
            matrixButton.setBackgroundColor(Color.LTGRAY);

            if (Objects.equals(matrixPosition.get(0), matrixPosition.get(1))) {
                binding.rangeSlider.setValue(binding.rangeSlider.getValue() - 6);
            } else if (matrixPosition.get(1) > matrixPosition.get(0)) {
                binding.rangeSlider.setValue(binding.rangeSlider.getValue() - 4);
            } else {
                binding.rangeSlider.setValue(binding.rangeSlider.getValue() - 12);
            }
        } else {
            matrixInput[matrixPosition.get(0)][matrixPosition.get(1)] = true;
            matrixButton.setBackgroundColor(Color.YELLOW);

            if (Objects.equals(matrixPosition.get(0), matrixPosition.get(1))) {
                binding.rangeSlider.setValue(binding.rangeSlider.getValue() + 6);
            } else if (matrixPosition.get(1) > matrixPosition.get(0)) {
                binding.rangeSlider.setValue(binding.rangeSlider.getValue() + 4);
            } else {
                binding.rangeSlider.setValue(binding.rangeSlider.getValue() + 12);
            }
        }
    };

    private void set_next_selected_card() {
        if ((selected_card_position[0] == 0 && selected_card_position[1] < 4) || selected_card_position[1] == 0) {
            set_selected_card(selected_card_position[0], selected_card_position[1] + 1);
        } else if ((selected_card_position[0] == 1 || selected_card_position[0] == players_remaining_no) && selected_card_position[1] == 1) {
            set_selected_card(0, 0);
        } else {
            boolean foundNext = false;
            for (int i = selected_card_position[0] + 1; i < players_remaining_no + 1; i++) {
                if (cardRows[i] instanceof SpecificCardsRow) {
                    set_selected_card(i, 0);
                    foundNext = true;
                    break;
                }
            }

            if (!foundNext) {
                set_selected_card(0, 0);
            }
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
        SpecificCardsRow cardRow = (SpecificCardsRow) cardRows[row_idx];
        cardRow.cards[card_idx][0] = suit;
        cardRow.cards[card_idx][1] = rank;

        setCardImage(row_idx, card_idx, suit, rank);
    }

    public void set_value_to_selected_card(int suit, int rank) {
        setInputCardVisible(selected_card_position[0], selected_card_position[1]);

        set_card_value(selected_card_position[0], selected_card_position[1], suit, rank);
        set_next_selected_card();
        calculate_odds();
    }

    public void setInputCardVisible(int row_idx, int card_idx) {
        int[] suit_rank_array = ((SpecificCardsRow) cardRows[row_idx]).cards[card_idx];

        if (suit_rank_array[0] != 0) {
            ImageButton card = input_suit_rank_map.inverse().get(Arrays.asList(suit_rank_array[0], suit_rank_array[1]));
            assert card != null;
            card.setVisibility(View.VISIBLE);
        }
    }

    public void setCardImage(int row_idx, int card_idx, int suit, int rank) {
        ImageButton card_button = cardPositionBiMap.get(Arrays.asList(row_idx, card_idx));
        assert card_button != null;
        card_button.setImageResource(suit_rank_drawable_map.get(suit).get(rank));
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