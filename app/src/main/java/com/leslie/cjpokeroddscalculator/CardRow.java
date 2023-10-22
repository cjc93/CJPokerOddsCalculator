package com.leslie.cjpokeroddscalculator;

public abstract class CardRow {
    public abstract void clear(MainActivity mainActivity, int row_idx);
    public abstract CardRow copy();
    public abstract void copyImageBelow(MainActivity mainActivity, int row_idx);
    public abstract boolean isKnownPlayer();
    public abstract String convertPlayerCardsToStr();
}
