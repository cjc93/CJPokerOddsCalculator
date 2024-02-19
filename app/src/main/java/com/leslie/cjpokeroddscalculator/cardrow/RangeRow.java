package com.leslie.cjpokeroddscalculator.cardrow;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.leslie.cjpokeroddscalculator.GlobalStatic;
import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.fragment.TexasHoldemFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

public class RangeRow extends CardRow {
    public List<List<Set<String>>> matrix;

    public RangeRow() {
        this.matrix = new ArrayList<>(13);
        for (int row_idx = 0; row_idx < 13; row_idx++) {
            List<Set<String>> row = new ArrayList<>(13);
            for (int col_idx = 0; col_idx < 13; col_idx++) {
                row.add(new HashSet<>());
            }
            this.matrix.add(row);
        }
    }

    public RangeRow(RangeRow other) {
        this.matrix = GlobalStatic.copyMatrix(other.matrix);
    }

    public void clear(EquityCalculatorFragment equityCalculatorFragment, int row_idx) {
        TexasHoldemFragment texasHoldemFragment = (TexasHoldemFragment) equityCalculatorFragment;
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                this.matrix.get(i).get(j).clear();
            }
        }

        Objects.requireNonNull(texasHoldemFragment.rangePositionBiMap.get(row_idx)).setImageBitmap(texasHoldemFragment.emptyRangeBitmap);
    }

    public CardRow copy() {
        return new RangeRow(this);
    }

    public void copyImageBelow(EquityCalculatorFragment equityCalculatorFragment, int row_idx) {
        TexasHoldemFragment texasHoldemFragment = (TexasHoldemFragment) equityCalculatorFragment;
        texasHoldemFragment.twoCardsLayouts[row_idx - 1].setVisibility(View.GONE);
        Drawable d = Objects.requireNonNull(texasHoldemFragment.rangePositionBiMap.get(row_idx + 1)).getDrawable();
        Objects.requireNonNull(texasHoldemFragment.rangePositionBiMap.get(row_idx)).setImageDrawable(d);
        Objects.requireNonNull(texasHoldemFragment.rangePositionBiMap.get(row_idx)).setVisibility(View.VISIBLE);
    }

    public boolean isKnownPlayer() {
        boolean isAllSuitsFirstElement = GlobalStatic.isAllSuits(this.matrix.get(0).get(0), 0, 0);

        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                boolean isAllSuitsCurrentElement = GlobalStatic.isAllSuits(this.matrix.get(i).get(j), i, j);

                if (!isAllSuitsCurrentElement && !this.matrix.get(i).get(j).isEmpty()) {
                    return true;
                }

                if (isAllSuitsCurrentElement != isAllSuitsFirstElement) {
                    return true;
                }
            }
        }

        return false;
    }

    public String convertTexasHoldemPlayerCardsToStr() {
        StringJoiner sj = new StringJoiner(",");
        String firstRank, secondRank;

        for (int row_idx = 0; row_idx < 13; row_idx++) {
            for (int col_idx = 0; col_idx < 13; col_idx++) {
                if (row_idx == col_idx) {
                    firstRank = GlobalStatic.rankStrings[row_idx];
                    secondRank = GlobalStatic.rankStrings[row_idx];
                } else if (col_idx > row_idx) {
                    firstRank = GlobalStatic.rankStrings[row_idx];
                    secondRank = GlobalStatic.rankStrings[col_idx];
                } else {
                    firstRank = GlobalStatic.rankStrings[col_idx];
                    secondRank = GlobalStatic.rankStrings[row_idx];
                }

                for (String s : this.matrix.get(row_idx).get(col_idx)) {
                    sj.add(firstRank + s.charAt(0) + secondRank + s.charAt(1));
                }
            }
        }

        if (sj.toString().equals("")) {
            return "random";
        } else {
            return sj.toString();
        }
    }
}
