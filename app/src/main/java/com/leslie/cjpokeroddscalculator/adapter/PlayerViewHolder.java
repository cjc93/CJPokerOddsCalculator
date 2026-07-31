package com.leslie.cjpokeroddscalculator.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.util.AndroidStatic;

import java.util.List;

public abstract class PlayerViewHolder extends RecyclerView.ViewHolder {
    public PlayerRowInteractionListener listener;

    public TextView playerText;
    public List<ShapeableImageView> cardList;
    public MaterialButton removeButton;
    public MaterialButton statsButton;
    public ConstraintLayout statsView;
    public List<TextView> statsTextViewList;

    public PlayerViewHolder(@NonNull View itemView, PlayerRowInteractionListener listener) {
        super(itemView);
        this.listener = listener;
    }

    public void initialiseViews(int boardCardMaxHeight, int cardMaxWidth) {
        AndroidStatic.setCardSize(cardList, boardCardMaxHeight, cardMaxWidth);

        for (int i = 0; i < cardList.size(); i++) {
            int cardIdx = i;
            cardList.get(i).setOnClickListener(v -> listener.onSelectCard(getBindingAdapterPosition(), cardIdx));
        }

        removeButton.setOnClickListener(v -> listener.onRemovePlayer(getBindingAdapterPosition()));
        statsButton.setOnClickListener(v -> listener.onToggleStats(getBindingAdapterPosition()));
    }

    public void bind(CardRow cardRow) {
        // TODO: this doesn't get updated sometimes
        playerText.setText(playerText.getContext().getString(R.string.player, getBindingAdapterPosition() + 1));

        statsView.setVisibility(cardRow.isStatsVisible ? View.VISIBLE : View.GONE);

        for (int statsIdx = 0; statsIdx < statsTextViewList.size(); statsIdx++) {
            if (cardRow.stats == null) {
                statsTextViewList.get(statsIdx).setText("");
            } else {
                statsTextViewList.get(statsIdx).setText(statsTextViewList.get(statsIdx).getContext().getString(R.string.two_decimal_perc, cardRow.stats[statsIdx] * 100));
            }
        }
    }
}