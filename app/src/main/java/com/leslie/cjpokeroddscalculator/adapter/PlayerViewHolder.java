package com.leslie.cjpokeroddscalculator.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;

public abstract class PlayerViewHolder extends RecyclerView.ViewHolder {
    protected final PlayerRowInteractionListener listener;
    protected final int boardCardMaxHeight;
    protected final int cardMaxWidth;

    public PlayerViewHolder(@NonNull View itemView, PlayerRowInteractionListener listener, int boardCardMaxHeight, int cardMaxWidth) {
        super(itemView);
        this.listener = listener;
        this.boardCardMaxHeight = boardCardMaxHeight;
        this.cardMaxWidth = cardMaxWidth;
    }

    public abstract void bind(CardRow cardRow, int rowIdx, int[] selectedCard);
}