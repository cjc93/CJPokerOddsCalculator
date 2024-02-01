package com.leslie.cjpokeroddscalculator;

import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;

public abstract class CardRow {
    public abstract void clear(EquityCalculatorFragment equityCalculatorFragment, int row_idx);
    public abstract CardRow copy();
    public abstract void copyImageBelow(EquityCalculatorFragment equityCalculatorFragment, int row_idx);
    public abstract boolean isKnownPlayer();
    public abstract String convertTexasHoldemPlayerCardsToStr();
}
