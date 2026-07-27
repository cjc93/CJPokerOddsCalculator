package com.leslie.cjpokeroddscalculator.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;

public abstract class PlayerAdapter extends ListAdapter<CardRow, PlayerViewHolder> {
    protected final PlayerRowInteractionListener listener;

    public PlayerAdapter(PlayerRowInteractionListener listener) {
        super(new CardRowDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return createPlayerViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        holder.bind(getItem(position), position + 1);
    }

    public abstract PlayerViewHolder createPlayerViewHolder(ViewGroup parent);
}