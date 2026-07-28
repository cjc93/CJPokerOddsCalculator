package com.leslie.cjpokeroddscalculator.adapter;

import android.view.View;
import android.widget.ImageButton;

import com.leslie.cjpokeroddscalculator.util.AndroidStatic;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.databinding.OmahaHighPlayerRowBinding;

import java.util.List;

public class OmahaHighPlayerViewHolder extends PlayerViewHolder {
    protected final OmahaHighPlayerRowBinding binding;
    protected final List<ImageButton> cardList;

    public OmahaHighPlayerViewHolder(OmahaHighPlayerRowBinding binding, PlayerRowInteractionListener listener, int boardCardMaxHeight, int cardMaxWidth, int cardsPerHand) {
        super(binding.getRoot(), listener, boardCardMaxHeight, cardMaxWidth);
        this.cardList = AndroidStatic.createOmahaCardButtons(binding, binding.getRoot(), binding.playerText, binding.statsButton, cardsPerHand);
        this.binding = binding;
        this.binding.getRoot().setOnClickListener(v -> listener.onHideCardSelector());
    }

    @Override
    public void bind(CardRow cardRow) {
        binding.playerText.setText(binding.getRoot().getContext().getString(R.string.player, getBindingAdapterPosition() + 1));

        binding.remove.setOnClickListener(v -> listener.onRemovePlayer(getBindingAdapterPosition() + 1));
        binding.statsButton.setOnClickListener(v -> listener.onToggleStats(getBindingAdapterPosition() + 1));

        binding.statsView.getRoot().setVisibility(cardRow.isStatsVisible ? View.VISIBLE : View.GONE);

        SpecificCardsRow specificCardRow = (SpecificCardsRow) cardRow;
        AndroidStatic.initialiseCardButtons(cardList, boardCardMaxHeight, cardMaxWidth, getBindingAdapterPosition() + 1, listener);
        AndroidStatic.setCardRowImages(cardList, specificCardRow);

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
    }
}