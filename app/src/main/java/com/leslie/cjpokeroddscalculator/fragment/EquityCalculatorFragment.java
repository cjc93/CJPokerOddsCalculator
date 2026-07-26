package com.leslie.cjpokeroddscalculator.fragment;

import static com.leslie.cjpokeroddscalculator.GlobalStatic.navControllerNavigate;
import static com.leslie.cjpokeroddscalculator.GlobalStatic.rankStrings;
import static com.leslie.cjpokeroddscalculator.GlobalStatic.suitRankDrawableMap;
import static com.leslie.cjpokeroddscalculator.GlobalStatic.suitStrings;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowMetrics;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.collect.HashBiMap;
import com.leslie.cjpokeroddscalculator.GlobalStatic;
import com.leslie.cjpokeroddscalculator.adapter.PlayerAdapter;
import com.leslie.cjpokeroddscalculator.adapter.PlayerRowInteractionListener;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.MainActivity;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.databinding.FragmentEquityCalculatorBinding;
import com.leslie.cjpokeroddscalculator.viewmodel.EquityCalculatorViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public abstract class EquityCalculatorFragment extends Fragment implements PlayerRowInteractionListener {

    public FragmentEquityCalculatorBinding equityCalculatorBinding;
    public EquityCalculatorViewModel viewModel;

    public List<ImageButton> boardButtons = new ArrayList<>();
    public HashBiMap<ImageButton, String> inputSuitRankMap;

    public DisplayMetrics displayMetrics = new DisplayMetrics();
    public int boardCardMaxHeight;
    public int boardCardMaxWidth;

    public String fragmentName;
    public int fragmentId;
    public int homeButtonActionId;

    public int maxPlayers;
    public int titleTextId;

    public PlayerAdapter playerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        equityCalculatorBinding = FragmentEquityCalculatorBinding.inflate(inflater, container, false);
        return equityCalculatorBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(getViewModelClass());

        initialiseVariables();

        generateMainLayout();

        observeLiveData();

        setButtonListeners();

        ((MainActivity) requireActivity()).dataStore.writeToDataStore(PreferencesKeys.stringKey("start_fragment"), fragmentName);
    }

    protected abstract Class<? extends EquityCalculatorViewModel> getViewModelClass();

    public void observeLiveData() {
        viewModel.cardRows.observe(getViewLifecycleOwner(), cardRows -> {
            for (ImageButton inputButton : inputSuitRankMap.keySet()) {
                inputButton.setVisibility(View.VISIBLE);
            }

            for (CardRow cardRow : cardRows) {
                if (cardRow instanceof SpecificCardsRow specificCardRow) {
                    for (String cardStr : specificCardRow.cards) {
                        if (!cardStr.isEmpty()) {
                            ImageButton cardButton = inputSuitRankMap.inverse().get(cardStr);
                            if (cardButton != null) {
                                cardButton.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
            }

            SpecificCardsRow boardCards = (SpecificCardsRow) cardRows.get(0);
            GlobalStatic.setCardRowImages(boardButtons, boardCards);

            equityCalculatorBinding.playersremaining.setText(getString(R.string.players_remaining, cardRows.size() - 1));

            playerAdapter.submitList(cardRows.subList(1, cardRows.size()));
        });

        viewModel.selectedCard.observe(getViewLifecycleOwner(), selectedCard -> {
            GlobalStatic.setSelectedCardBorder(boardButtons, 0, selectedCard);

            playerAdapter.setSelectedCard(selectedCard);
            playerAdapter.notifyItemRangeChanged(0, playerAdapter.getItemCount());

            if (selectedCard == null) {
                equityCalculatorBinding.inputCards.setVisibility(View.GONE);
                equityCalculatorBinding.buttonUnknown.setVisibility(View.GONE);
            } else {
                equityCalculatorBinding.inputCards.setVisibility(View.VISIBLE);
                equityCalculatorBinding.buttonUnknown.setVisibility(View.VISIBLE);
            }
        });

        viewModel.resDesc.observe(getViewLifecycleOwner(), stringId -> equityCalculatorBinding.resDesc.setText(stringId));
    }

    private void setButtonListeners () {
        equityCalculatorBinding.homeButton.setOnClickListener(v -> navControllerNavigate(this, fragmentId, homeButtonActionId));

        equityCalculatorBinding.addplayer.setOnClickListener(v -> {
            List<CardRow> cardRows = viewModel.cardRows.getValue();
            assert cardRows != null;
            if (cardRows.size() - 1 < this.maxPlayers) {
                List<CardRow> newCardRows = new ArrayList<>();
                for (CardRow cardRow : cardRows) {
                    newCardRows.add(cardRow.copy());
                }
                newCardRows.add(new SpecificCardsRow(null, false, viewModel.cardsPerHand));
                viewModel.cardRows.setValue(newCardRows);

                calculateOdds();
            } else {
                Toast.makeText(requireActivity(), "Max number of players is " + this.maxPlayers, Toast.LENGTH_SHORT).show();
            }
        });

        equityCalculatorBinding.clear.setOnClickListener(v -> {
            List<CardRow> cardRows = viewModel.cardRows.getValue();
            assert cardRows != null;
            List<CardRow> newCardRows = new ArrayList<>();
            for (CardRow cardRow : cardRows) {
                CardRow copy = cardRow.copy();
                copy.clear();
                newCardRows.add(copy);
            }
            viewModel.cardRows.setValue(newCardRows);

            if (viewModel.selectedCard.getValue() != null) {
                if (newCardRows.size() > 1 && newCardRows.get(1) instanceof SpecificCardsRow) {
                    viewModel.selectedCard.setValue(new int[]{1, 0});
                } else {
                    viewModel.selectedCard.setValue(new int[]{0, 0});
                }
            }

            equityCalculatorBinding.playerList.scrollToPosition(0);

            calculateOdds();
        });

        equityCalculatorBinding.buttonUnknown.setOnClickListener(v -> setValueToSelectedCard(""));

        equityCalculatorBinding.hideCardSelectorArea.setOnClickListener(v -> viewModel.selectedCard.setValue(null));

        equityCalculatorBinding.playerList.addOnItemTouchListener(
            new RecyclerView.SimpleOnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                    if (e.getAction() == MotionEvent.ACTION_DOWN) {
                        View child = rv.findChildViewUnder(e.getX(), e.getY());

                        if (child == null) {
                            viewModel.selectedCard.setValue(null);
                        }
                    }

                    return false;
                }
            }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        equityCalculatorBinding = null;
    }

    public void initialiseVariables() {
        WindowMetrics windowMetrics = requireActivity().getWindowManager().getCurrentWindowMetrics();
        Rect bounds = windowMetrics.getBounds();
        displayMetrics.widthPixels = bounds.width();
        displayMetrics.heightPixels = bounds.height();
        boardCardMaxHeight = (int) (displayMetrics.heightPixels * 0.12);
        boardCardMaxWidth = (int) (displayMetrics.widthPixels * 0.2);
    }

    public void generateMainLayout() {
        equityCalculatorBinding.title.setText(this.titleTextId);

        boardButtons = Arrays.asList(
            equityCalculatorBinding.flop1,
            equityCalculatorBinding.flop2,
            equityCalculatorBinding.flop3,
            equityCalculatorBinding.turn,
            equityCalculatorBinding.river
        );

        GlobalStatic.initialiseCardButtons(boardButtons, boardCardMaxHeight, boardCardMaxWidth, 0, this);

        playerAdapter = createPlayerAdapter();
        equityCalculatorBinding.playerList.setLayoutManager(new LinearLayoutManager(requireActivity()));
        equityCalculatorBinding.playerList.setItemAnimator(null);
        equityCalculatorBinding.playerList.setAdapter(playerAdapter);

        inputSuitRankMap = HashBiMap.create();
        for (String suit : suitStrings) {
            for (String rank : rankStrings) {
                ImageButton b = new ImageButton(requireActivity());
                b.setId(View.generateViewId());
                b.setBackgroundResource(0);
                Integer id = suitRankDrawableMap.get(rank + suit);
                assert id != null;
                b.setImageResource(id);
                b.setScaleType(ImageButton.ScaleType.FIT_XY);
                b.setPadding(1, 1, 1, 1);

                b.setOnClickListener(v -> {
                    ImageButton cardInput = (ImageButton) v;
                    cardInput.setVisibility(View.INVISIBLE);
                    String cardStr = inputSuitRankMap.get(cardInput);
                    setValueToSelectedCard(cardStr);
                });

                inputSuitRankMap.put(b, rank + suit);

                equityCalculatorBinding.inputCards.addView(b);
            }
        }

        for (int i = 0; i < suitStrings.length; i++) {
            for (int j = 0; j < rankStrings.length; j++) {
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
                );

                if (i == 0) {
                    layoutParams.topToTop = ConstraintSet.PARENT_ID;
                    layoutParams.bottomToTop = Objects.requireNonNull(this.inputSuitRankMap.inverse().get(rankStrings[j] + suitStrings[1])).getId();
                } else if (i == 3) {
                    layoutParams.topToBottom = Objects.requireNonNull(this.inputSuitRankMap.inverse().get(rankStrings[j] + suitStrings[2])).getId();
                    layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;
                } else {
                    layoutParams.topToBottom = Objects.requireNonNull(this.inputSuitRankMap.inverse().get(rankStrings[j] + suitStrings[i - 1])).getId();
                    layoutParams.bottomToTop = Objects.requireNonNull(this.inputSuitRankMap.inverse().get(rankStrings[j] + suitStrings[i + 1])).getId();
                }

                if (j == 0) {
                    layoutParams.leftToLeft = ConstraintSet.PARENT_ID;
                    layoutParams.rightToLeft = Objects.requireNonNull(this.inputSuitRankMap.inverse().get(rankStrings[1] + suitStrings[i])).getId();
                } else if (j == 12) {
                    layoutParams.leftToRight = Objects.requireNonNull(this.inputSuitRankMap.inverse().get(rankStrings[11] + suitStrings[i])).getId();
                    layoutParams.rightToRight = ConstraintSet.PARENT_ID;
                } else {
                    layoutParams.leftToRight = Objects.requireNonNull(this.inputSuitRankMap.inverse().get(rankStrings[j - 1] + suitStrings[i])).getId();
                    layoutParams.rightToLeft = Objects.requireNonNull(this.inputSuitRankMap.inverse().get(rankStrings[j + 1] + suitStrings[i])).getId();
                }

                ImageButton button = this.inputSuitRankMap.inverse().get(rankStrings[j] + suitStrings[i]);
                assert button != null;
                button.setLayoutParams(layoutParams);
            }
        }
    }

    public void setValueToSelectedCard(String cardStr) {
        int[] selectedCard = viewModel.selectedCard.getValue();
        if (selectedCard == null) {
            return;
        }

        int selectedRowIdx = selectedCard[0];
        int selectedCardIdx = selectedCard[1];

        int newSelectedRowIdx = -1;
        int newSelectedCardIdx = -1;

        List<CardRow> cardRows = viewModel.cardRows.getValue();
        assert cardRows != null;

        List<CardRow> newCardRows = new ArrayList<>();
        for (CardRow cardRow : cardRows) {
            newCardRows.add(cardRow.copy());
        }

        SpecificCardsRow cardRow = (SpecificCardsRow) newCardRows.get(selectedRowIdx);

        cardRow.cards[selectedCardIdx] = cardStr;

        viewModel.cardRows.setValue(newCardRows);

        if ((selectedRowIdx == 0 && selectedCardIdx < 4) || (selectedRowIdx > 0 && selectedCardIdx < (viewModel.cardsPerHand - 1))) {
            newSelectedRowIdx = selectedRowIdx;
            newSelectedCardIdx = selectedCardIdx + 1;
        } else if ((selectedRowIdx == 1 || selectedRowIdx == newCardRows.size() - 1) && selectedCardIdx == (viewModel.cardsPerHand - 1)) {
            newSelectedRowIdx = 0;
            newSelectedCardIdx = 0;
        } else {
            boolean foundNext = false;
            for (int rowIdx = selectedRowIdx + 1; rowIdx < newCardRows.size(); rowIdx++) {
                if (newCardRows.get(rowIdx) instanceof SpecificCardsRow) {
                    newSelectedRowIdx = rowIdx;
                    newSelectedCardIdx = 0;
                    foundNext = true;
                    break;
                }
            }

            if (!foundNext) {
                newSelectedRowIdx = 0;
                newSelectedCardIdx = 0;
            }
        }

        viewModel.selectedCard.setValue(new int[]{newSelectedRowIdx, newSelectedCardIdx});

        if (newSelectedRowIdx > 0) {
            equityCalculatorBinding.playerList.scrollToPosition(newSelectedRowIdx - 1);
        }

        calculateOdds();
    }

    public void calculateOdds() {
        List<CardRow> cardRows = viewModel.cardRows.getValue();
        assert cardRows != null;
        List<CardRow> newCardRows = new ArrayList<>();
        for (CardRow cardRow : cardRows) {
            CardRow copy = cardRow.copy();
            copy.stats = null;
            newCardRows.add(copy);
        }
        viewModel.cardRows.setValue(newCardRows);

        viewModel.resDesc.setValue(R.string.checking_random_subset);
        viewModel.calculateOdds();
    }

    @Override
    public void onRemovePlayer(int playerRemoveNumber) {
        List<CardRow> cardRows = viewModel.cardRows.getValue();
        if (cardRows != null && playerRemoveNumber < cardRows.size()) {
            List<CardRow> newCardRows = new ArrayList<>();
            for (CardRow row : cardRows) {
                newCardRows.add(row.copy());
            }
            newCardRows.remove(playerRemoveNumber);
            viewModel.cardRows.setValue(newCardRows);
            int[] selectedCard = viewModel.selectedCard.getValue();

            if (selectedCard != null && selectedCard[0] >= playerRemoveNumber) {
                for (int rowIdx = selectedCard[0]; rowIdx >= 0; rowIdx--) {
                    if (rowIdx == 0) {
                        viewModel.selectedCard.setValue(new int[]{0, 0});
                        break;
                    } else if (rowIdx < newCardRows.size() && newCardRows.get(rowIdx) instanceof SpecificCardsRow) {
                        viewModel.selectedCard.setValue(new int[]{rowIdx, selectedCard[1]});
                        break;
                    }
                }
            }

            calculateOdds();
        }
    }

    @Override
    public void onToggleStats(int rowIdx) {
        List<CardRow> cardRows = viewModel.cardRows.getValue();
        if (cardRows != null && rowIdx < cardRows.size()) {
            List<CardRow> newCardRows = new ArrayList<>();
            for (CardRow row : cardRows) {
                newCardRows.add(row.copy());
            }
            newCardRows.get(rowIdx).isStatsVisible = !newCardRows.get(rowIdx).isStatsVisible;
            viewModel.cardRows.setValue(newCardRows);
        }
    }

    @Override
    public void onSelectCard(int rowIdx, int cardIdx) {
        viewModel.selectedCard.setValue(new int[]{rowIdx, cardIdx});
    }

    @Override
    public void onHideCardSelector() {
        viewModel.selectedCard.setValue(null);
    }

    @Override
    public void onToggleRangeHand(int rowIdx) {
        // Default implementation does nothing, overridden in TexasHoldemFragment
    }

    @Override
    public void onShowRangeSelector(int rowIdx) {
        // Default implementation does nothing, overridden in TexasHoldemFragment
    }


    public abstract PlayerAdapter createPlayerAdapter();
}