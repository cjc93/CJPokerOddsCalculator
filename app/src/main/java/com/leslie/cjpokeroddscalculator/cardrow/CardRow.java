package com.leslie.cjpokeroddscalculator.cardrow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class CardRow {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

    public int id;
    public List<Double> stats;
    public Boolean isStatsVisible;

    public CardRow(Boolean isStatsVisible) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.isStatsVisible = isStatsVisible;
    }

    public abstract void clear();
    public abstract boolean isKnownPlayer();
    public abstract String convertTexasHoldemPlayerCardsToStr();
    public abstract CardRow copy();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardRow cardRow = (CardRow) o;
        return Objects.equals(stats, cardRow.stats) && Objects.equals(isStatsVisible, cardRow.isStatsVisible);
    }

    protected List<Double> copyStats() {
        return stats == null ? null : new ArrayList<>(stats);
    }
}
