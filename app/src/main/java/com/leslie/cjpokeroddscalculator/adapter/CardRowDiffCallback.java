package com.leslie.cjpokeroddscalculator.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;

public class CardRowDiffCallback extends DiffUtil.ItemCallback<CardRow> {
    @Override
    public boolean areItemsTheSame(@NonNull CardRow oldItem, @NonNull CardRow newItem) {
        return oldItem.id == newItem.id;
    }

    @Override
    public boolean areContentsTheSame(@NonNull CardRow oldItem, @NonNull CardRow newItem) {
        return oldItem.equals(newItem);
    }
}