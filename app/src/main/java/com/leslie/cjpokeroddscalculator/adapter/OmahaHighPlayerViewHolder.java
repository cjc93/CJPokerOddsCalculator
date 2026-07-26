package com.leslie.cjpokeroddscalculator.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.button.MaterialButton;
import com.leslie.cjpokeroddscalculator.GlobalStatic;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.databinding.OmahaHighPlayerRowBinding;

import java.util.ArrayList;
import java.util.List;

public class OmahaHighPlayerViewHolder extends PlayerViewHolder {
    protected final OmahaHighPlayerRowBinding binding;
    protected final List<ImageButton> cardList;
    protected final int cardsPerHand;

    public OmahaHighPlayerViewHolder(OmahaHighPlayerRowBinding binding, PlayerRowInteractionListener listener, int boardCardMaxHeight, int cardMaxWidth, int cardsPerHand) {
        super(binding.getRoot(), listener, boardCardMaxHeight, cardMaxWidth);
        this.binding = binding;
        this.cardsPerHand = cardsPerHand;
        this.cardList = createCardButtons(binding.getRoot(), binding.playerText, binding.statsButton);
    }

    @Override
    public void bind(CardRow cardRow, int rowIdx, int[] selectedCard) {
        binding.playerText.setText(binding.getRoot().getContext().getString(R.string.player, rowIdx));

        binding.remove.setOnClickListener(v -> listener.onRemovePlayer(rowIdx));
        binding.statsButton.setOnClickListener(v -> listener.onToggleStats(rowIdx));

        binding.statsView.getRoot().setVisibility(cardRow.isStatsVisible ? View.VISIBLE : View.GONE);
        binding.getRoot().setOnClickListener(v -> listener.onHideCardSelector());

        if (cardRow.stats != null) {
            binding.equity.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(0) * 100));
            binding.win.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(1) * 100));
            binding.tie.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(2) * 100));

            binding.statsView.highCard.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(3) * 100));
            binding.statsView.onePair.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(4) * 100));
            binding.statsView.twoPair.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(5) * 100));
            binding.statsView.threeOfAKind.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(6) * 100));
            binding.statsView.straight.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(7) * 100));
            binding.statsView.flush.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(8) * 100));
            binding.statsView.fullHouse.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(9) * 100));
            binding.statsView.fourOfAKind.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(10) * 100));
            binding.statsView.straightFlush.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(11) * 100));
        } else {
            binding.equity.setText("");
            binding.win.setText("");
            binding.tie.setText("");
            binding.statsView.highCard.setText("");
            binding.statsView.onePair.setText("");
            binding.statsView.twoPair.setText("");
            binding.statsView.threeOfAKind.setText("");
            binding.statsView.straight.setText("");
            binding.statsView.flush.setText("");
            binding.statsView.fullHouse.setText("");
            binding.statsView.fourOfAKind.setText("");
            binding.statsView.straightFlush.setText("");
        }

        for (int i = 0; i < cardList.size(); i++) {
            cardList.get(i).setMaxHeight(boardCardMaxHeight);
            cardList.get(i).setMaxWidth(cardMaxWidth);

            int cardIdx = i;
            cardList.get(i).setOnClickListener(v -> listener.onSelectCard(rowIdx, cardIdx));
        }

        SpecificCardsRow specificCardRow = (SpecificCardsRow) cardRow;
        for (int i = 0; i < specificCardRow.cards.length; i++) {
            String cardStr = specificCardRow.cards[i];
            GlobalStatic.setCardImage(cardList.get(i), cardStr);

            if (selectedCard != null && selectedCard[0] == rowIdx && selectedCard[1] == i) {
                cardList.get(i).setBackgroundResource(R.drawable.selected_border);
            } else {
                cardList.get(i).setBackgroundResource(0);
            }
        }
    }

    protected List<ImageButton> createCardButtons(ConstraintLayout playerRow, TextView playerText, MaterialButton statsButton) {
        List<ImageButton> cardList = new ArrayList<>();

        for (int i = 0; i < cardsPerHand; i++) {
            ImageButton card = new ImageButton(binding.getRoot().getContext(), null, 0, R.style.SelectCardButton);
            card.setId(View.generateViewId());
            cardList.add(card);
        }

        for (int i = 0; i < cardsPerHand; i++) {
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );

            if (i == 0) {
                layoutParams.topToBottom = playerText.getId();
                layoutParams.leftToLeft = ConstraintSet.PARENT_ID;
                layoutParams.rightToLeft = cardList.get(i + 1).getId();
            } else if (i == cardsPerHand - 1) {
                layoutParams.topToBottom = playerText.getId();
                layoutParams.leftToRight = cardList.get(i - 1).getId();
                layoutParams.rightToRight = playerText.getId();
            } else {
                layoutParams.topToBottom = playerText.getId();
                layoutParams.leftToRight = cardList.get(i - 1).getId();
                layoutParams.rightToLeft = cardList.get(i + 1).getId();
            }

            cardList.get(i).setLayoutParams(layoutParams);

            playerRow.addView(cardList.get(i));
        }

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) statsButton.getLayoutParams();
        layoutParams.bottomToBottom = cardList.get(0).getId();
        statsButton.setLayoutParams(layoutParams);

        return cardList;
    }
}