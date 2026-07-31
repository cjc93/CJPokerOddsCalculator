package com.leslie.cjpokeroddscalculator.fragment;

import static com.leslie.cjpokeroddscalculator.util.AndroidStatic.dpToPx;
import static com.leslie.cjpokeroddscalculator.util.AndroidStatic.navControllerNavigate;
import static com.leslie.cjpokeroddscalculator.util.GlobalStatic.rankStrings;
import static com.leslie.cjpokeroddscalculator.util.AndroidStatic.suitRankDrawableMap;
import static com.leslie.cjpokeroddscalculator.util.GlobalStatic.suitStrings;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.imageview.ShapeableImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.common.collect.HashBiMap;
import com.leslie.cjpokeroddscalculator.util.AndroidStatic;
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

    public List<ShapeableImageView> boardButtons = new ArrayList<>();
    public HashBiMap<ShapeableImageView, String> inputSuitRankMap;

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

        requireActivity().getOnBackPressedDispatcher().addCallback(
            getViewLifecycleOwner(),
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    requireActivity().finish();
                }
            }
        );

        ((MainActivity) requireActivity()).dataStore.writeToDataStore(PreferencesKeys.stringKey("start_fragment"), fragmentName);
    }

    protected abstract Class<? extends EquityCalculatorViewModel> getViewModelClass();

    public void observeLiveData() {
        viewModel.playerCardRows.observe(getViewLifecycleOwner(), cardRows -> equityCalculatorBinding.playersremaining.setText(getString(R.string.players_remaining, cardRows.size())));

        viewModel.selectedCard.observe(getViewLifecycleOwner(), selectedCard -> {
            if (selectedCard == null) {
                equityCalculatorBinding.inputCards.setVisibility(View.GONE);
                equityCalculatorBinding.buttonUnknownGroup.setVisibility(View.GONE);
            } else {
                equityCalculatorBinding.inputCards.setVisibility(View.VISIBLE);
                equityCalculatorBinding.buttonUnknownGroup.setVisibility(View.VISIBLE);
            }
        });

        viewModel.recyclerViewData.observe(getViewLifecycleOwner(), recyclerViewData -> playerAdapter.submitList(recyclerViewData));

        viewModel.boardData.observe(getViewLifecycleOwner(), boardData -> AndroidStatic.setCardRowImages(boardButtons, boardData));

        viewModel.inputCardsViewData.observe(getViewLifecycleOwner(), inputCardsViewData -> {
            for (ShapeableImageView inputButton : inputSuitRankMap.keySet()) {
                inputButton.setEnabled(true);
                inputButton.setImageAlpha(255);
            }

            for (CardRow cardRow : inputCardsViewData) {
                if (cardRow instanceof SpecificCardsRow specificCardRow) {
                    for (String cardStr : specificCardRow.cards) {
                        if (!cardStr.isEmpty()) {
                            ShapeableImageView inputButton = inputSuitRankMap.inverse().get(cardStr);
                            if (inputButton != null) {
                                inputButton.setEnabled(false);
                                inputButton.setImageAlpha(110);
                            }
                        }
                    }
                }
            }
        });

        viewModel.resDesc.observe(getViewLifecycleOwner(), stringId -> equityCalculatorBinding.resDesc.setText(stringId));
    }

    private void setButtonListeners () {
        equityCalculatorBinding.homeButton.setOnClickListener(v -> {
            viewModel.killThreads();
            viewModel.resDesc.setValue(R.string.space);
            navControllerNavigate(this, fragmentId, homeButtonActionId);
        });

        equityCalculatorBinding.addplayer.setOnClickListener(v -> {
            List<CardRow> newCardRows = viewModel.getPlayerCardRowsCopy();
            if (newCardRows.size() < this.maxPlayers) {
                newCardRows.add(new SpecificCardsRow(false, viewModel.cardsPerHand));
                viewModel.playerCardRows.setValue(newCardRows);
                calculateOdds();
            } else {
                Toast.makeText(requireActivity(), "Max number of players is " + this.maxPlayers, Toast.LENGTH_SHORT).show();
            }
        });

        equityCalculatorBinding.clear.setOnClickListener(v -> {
            SpecificCardsRow boardCardRow = Objects.requireNonNull(viewModel.boardCardRow.getValue()).copy();
            boardCardRow.clear();
            viewModel.boardCardRow.setValue(boardCardRow);

            List<CardRow> newCardRows = viewModel.getPlayerCardRowsCopy();
            for (CardRow cardRow : newCardRows) {
                cardRow.clear();
            }
            viewModel.playerCardRows.setValue(newCardRows);

            if (viewModel.getSelectedCardPosition() != null) {
                if (!newCardRows.isEmpty() && newCardRows.get(0) instanceof SpecificCardsRow) {
                    viewModel.setSelectedCardPosition(0, 0);
                } else {
                    viewModel.setSelectedCardPosition(-1, 0);
                }
            }

            calculateOdds();

            equityCalculatorBinding.playerList.scrollToPosition(0);
        });

        equityCalculatorBinding.buttonUnknown.setOnClickListener(v -> setValueToSelectedCard(""));

        equityCalculatorBinding.hideCardSelectorArea.setOnClickListener(v -> viewModel.setSelectedCardPosition(null, null));

        equityCalculatorBinding.playerList.addOnItemTouchListener(
            new RecyclerView.SimpleOnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                    if (e.getAction() == MotionEvent.ACTION_DOWN) {
                        View child = rv.findChildViewUnder(e.getX(), e.getY());

                        if (child == null) {
                            viewModel.setSelectedCardPosition(null, null);
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
        displayMetrics = AndroidStatic.getDisplayMetrics(requireActivity());
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

        AndroidStatic.setCardSize(boardButtons, boardCardMaxHeight, boardCardMaxWidth);
        for (int i = 0; i < boardButtons.size(); i++) {
            int cardIdx = i;
            boardButtons.get(i).setOnClickListener(v -> onSelectCard(-1, cardIdx));
        }

        playerAdapter = createPlayerAdapter();
        equityCalculatorBinding.playerList.setLayoutManager(new LinearLayoutManager(requireActivity()));
        equityCalculatorBinding.playerList.setItemAnimator(null);
        equityCalculatorBinding.playerList.setAdapter(playerAdapter);

        inputSuitRankMap = HashBiMap.create();
        for (String suit : suitStrings) {
            for (String rank : rankStrings) {
                ShapeableImageView b = new ShapeableImageView(requireActivity());
                b.setId(View.generateViewId());
                Integer id = suitRankDrawableMap.get(rank + suit);
                assert id != null;
                b.setImageResource(id);
                b.setScaleType(ShapeableImageView.ScaleType.FIT_XY);
                b.setPadding(1, 1, 1, 1);
                b.setShapeAppearanceModel(new ShapeAppearanceModel.Builder().setAllCornerSizes(dpToPx(b.getContext(), 5)).build());
                b.setStrokeColor(ColorStateList.valueOf(Color.WHITE));

                b.setOnClickListener(v -> {
                    ShapeableImageView cardInput = (ShapeableImageView) v;
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

                ShapeableImageView button = this.inputSuitRankMap.inverse().get(rankStrings[j] + suitStrings[i]);
                assert button != null;
                button.setLayoutParams(layoutParams);
            }
        }
    }

    public void setValueToSelectedCard(String cardStr) {
        int[] selectedCard = viewModel.getSelectedCardPosition();
        if (selectedCard == null) {
            return;
        }

        int selectedRowIdx = selectedCard[0];
        int selectedCardIdx = selectedCard[1];

        int newSelectedRowIdx = -2;
        int newSelectedCardIdx = -2;

        List<CardRow> newCardRows = viewModel.getPlayerCardRowsCopy();

        if (selectedRowIdx == -1) {
            SpecificCardsRow newCardRow = Objects.requireNonNull(viewModel.boardCardRow.getValue()).copy();
            newCardRow.cards[selectedCardIdx] = cardStr;
            viewModel.boardCardRow.setValue(newCardRow);
        } else {
            SpecificCardsRow cardRow = (SpecificCardsRow) newCardRows.get(selectedRowIdx);
            cardRow.cards[selectedCardIdx] = cardStr;
            viewModel.playerCardRows.setValue(newCardRows);
        }

        if ((selectedRowIdx == -1 && selectedCardIdx < 4) || (selectedRowIdx >= 0 && selectedCardIdx < (viewModel.cardsPerHand - 1))) {
            newSelectedRowIdx = selectedRowIdx;
            newSelectedCardIdx = selectedCardIdx + 1;
        } else if ((selectedRowIdx == 0 || selectedRowIdx == newCardRows.size() - 1) && selectedCardIdx == (viewModel.cardsPerHand - 1)) {
            newSelectedRowIdx = -1;
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
                newSelectedRowIdx = -1;
                newSelectedCardIdx = 0;
            }
        }

        viewModel.setSelectedCardPosition(newSelectedRowIdx, newSelectedCardIdx);

        calculateOdds();

        if (newSelectedRowIdx >= 0) {
            equityCalculatorBinding.playerList.scrollToPosition(newSelectedRowIdx);
        }
    }

    public void calculateOdds() {
        viewModel.stats.setValue(null);
        viewModel.resDesc.setValue(R.string.checking_random_subset);

        viewModel.calculateOdds();
    }

    @Override
    public void onRemovePlayer(int playerRemoveNumber) {
        int[] selectedCard = viewModel.selectedCard.getValue();

        List<CardRow> newCardRows = viewModel.getPlayerCardRowsCopy();
        int removedId = newCardRows.get(playerRemoveNumber).id;
        newCardRows.remove(playerRemoveNumber);
        viewModel.playerCardRows.setValue(newCardRows);

        if (selectedCard != null && selectedCard[0] == removedId) {
            for (int rowIdx = playerRemoveNumber; rowIdx >= -1; rowIdx--) {
                if (rowIdx == -1) {
                    viewModel.setSelectedCardPosition(-1, 0);
                    break;
                } else if (rowIdx < newCardRows.size() && newCardRows.get(rowIdx) instanceof SpecificCardsRow) {
                    viewModel.setSelectedCardPosition(rowIdx, selectedCard[1]);
                    break;
                }
            }
        }

        calculateOdds();
    }

    @Override
    public void onToggleStats(int rowIdx) {
        List<CardRow> newCardRows = viewModel.getPlayerCardRowsCopy();
        newCardRows.get(rowIdx).isStatsVisible = !newCardRows.get(rowIdx).isStatsVisible;
        viewModel.playerCardRows.setValue(newCardRows);
    }

    @Override
    public void onSelectCard(int rowIdx, int cardIdx) {
        viewModel.setSelectedCardPosition(rowIdx, cardIdx);
    }

    @Override
    public void onHideCardSelector() {
        viewModel.setSelectedCardPosition(null, null);
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