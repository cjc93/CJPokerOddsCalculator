package com.leslie.cjpokeroddscalculator.cardrow;

import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;

public abstract class CardRow {
    public abstract void clear(EquityCalculatorFragment equityCalculatorFragment, int row_idx);
    public abstract boolean isKnownPlayer();
    public abstract String convertTexasHoldemPlayerCardsToStr();
}
