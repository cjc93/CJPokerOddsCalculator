package com.leslie.cjpokeroddscalculator.cardrow;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class CardRow implements Serializable {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

    public int id;
    public double[] stats;
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
        return Arrays.equals(stats, cardRow.stats) && Objects.equals(isStatsVisible, cardRow.isStatsVisible);
    }

    protected double[] copyStats() {
        return stats == null ? null : stats.clone();
    }
}
