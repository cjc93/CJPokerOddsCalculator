package com.leslie.cjpokeroddscalculator.cardrow;

import java.util.List;

public abstract class CardRow {
    public List<Double> stats;
    public Boolean isStatsVisible;

    public CardRow(List<Double> stats, Boolean isStatsVisible) {
        this.stats = stats;
        this.isStatsVisible = isStatsVisible;
    }

    public abstract void clear();
    public abstract boolean isKnownPlayer();
    public abstract String convertTexasHoldemPlayerCardsToStr();
}
