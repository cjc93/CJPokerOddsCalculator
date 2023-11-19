package com.leslie.cjpokeroddscalculator;

public abstract class CardRow {
    public abstract void clear(TexasHoldemFragment texasHoldemFragment, int row_idx);
    public abstract CardRow copy();
    public abstract void copyImageBelow(TexasHoldemFragment texasHoldemFragment, int row_idx);
    public abstract boolean isKnownPlayer();
    public abstract String convertPlayerCardsToStr();
}
