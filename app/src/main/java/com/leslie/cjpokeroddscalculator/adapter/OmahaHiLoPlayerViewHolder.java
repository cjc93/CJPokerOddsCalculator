package com.leslie.cjpokeroddscalculator.adapter;

import android.view.View;
import com.google.android.material.imageview.ShapeableImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.leslie.cjpokeroddscalculator.util.AndroidStatic;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.databinding.OmahaHiloPlayerRowBinding;

import java.util.List;

public class OmahaHiLoPlayerViewHolder extends PlayerViewHolder {
    private final OmahaHiloPlayerRowBinding binding;
    private final List<ShapeableImageView> cardList;
    private final TextView lowPercent;

    public OmahaHiLoPlayerViewHolder(OmahaHiloPlayerRowBinding binding, PlayerRowInteractionListener listener, int boardCardMaxHeight, int cardMaxWidth, int cardsPerHand) {
        super(binding.getRoot(), listener, boardCardMaxHeight, cardMaxWidth);
        this.cardList = AndroidStatic.createOmahaCardButtons(binding, binding.getRoot(), binding.playerText, binding.statsButton, cardsPerHand);
        this.binding = binding;
        this.binding.getRoot().setOnClickListener(v -> listener.onHideCardSelector());

        TextView lowText = new TextView(binding.getRoot().getContext(), null, 0, R.style.StatsText);
        lowText.setId(View.generateViewId());
        lowText.setText(R.string.low);

        this.lowPercent = new TextView(binding.getRoot().getContext(), null, 0, R.style.StatsText);
        this.lowPercent.setId(View.generateViewId());

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.topToBottom = binding.statsView.straightFlushText.getId();
        layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;
        layoutParams.leftToRight = binding.statsView.guideline3.getId();
        layoutParams.rightToLeft = lowPercent.getId();
        layoutParams.horizontalChainStyle = ConstraintLayout.LayoutParams.CHAIN_SPREAD_INSIDE;

        lowText.setLayoutParams(layoutParams);

        binding.statsView.getRoot().addView(lowText);

        layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.topToBottom = binding.statsView.straightFlush.getId();
        layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;
        layoutParams.leftToRight = lowText.getId();
        layoutParams.rightToLeft = binding.statsView.guideline4.getId();
        layoutParams.horizontalChainStyle = ConstraintLayout.LayoutParams.CHAIN_SPREAD_INSIDE;

        this.lowPercent.setLayoutParams(layoutParams);

        binding.statsView.getRoot().addView(this.lowPercent);
    }

    @Override
    public void bind(CardRow cardRow) {
        binding.playerText.setText(binding.getRoot().getContext().getString(R.string.player, getBindingAdapterPosition() + 1));

        binding.remove.setOnClickListener(v -> listener.onRemovePlayer(getBindingAdapterPosition() + 1));
        binding.statsButton.setOnClickListener(v -> listener.onToggleStats(getBindingAdapterPosition() + 1));

        binding.statsView.getRoot().setVisibility(cardRow.isStatsVisible ? View.VISIBLE : View.GONE);

        AndroidStatic.initialiseCardButtons(cardList, boardCardMaxHeight, cardMaxWidth, getBindingAdapterPosition() + 1, listener);

        SpecificCardsRow specificCardRow = (SpecificCardsRow) cardRow;
        AndroidStatic.setCardRowImages(cardList, specificCardRow);

        if (cardRow.stats != null && cardRow.stats.size() >= 15) {
            binding.equity.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(0) * 100));
            binding.win.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(1) * 100));
            binding.winLow.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(2) * 100));
            binding.tie.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(3) * 100));
            binding.tieLow.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(4) * 100));

            binding.statsView.highCard.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(5) * 100));
            binding.statsView.onePair.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(6) * 100));
            binding.statsView.twoPair.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(7) * 100));
            binding.statsView.threeOfAKind.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(8) * 100));
            binding.statsView.straight.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(9) * 100));
            binding.statsView.flush.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(10) * 100));
            binding.statsView.fullHouse.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(11) * 100));
            binding.statsView.fourOfAKind.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(12) * 100));
            binding.statsView.straightFlush.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(13) * 100));
            lowPercent.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(14) * 100));
        } else {
            binding.equity.setText("");
            binding.win.setText("");
            binding.winLow.setText("");
            binding.tie.setText("");
            binding.tieLow.setText("");
            binding.statsView.highCard.setText("");
            binding.statsView.onePair.setText("");
            binding.statsView.twoPair.setText("");
            binding.statsView.threeOfAKind.setText("");
            binding.statsView.straight.setText("");
            binding.statsView.flush.setText("");
            binding.statsView.fullHouse.setText("");
            binding.statsView.fourOfAKind.setText("");
            binding.statsView.straightFlush.setText("");
            lowPercent.setText("");
        }
    }
}