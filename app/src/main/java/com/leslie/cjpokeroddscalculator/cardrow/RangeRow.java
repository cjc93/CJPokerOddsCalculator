package com.leslie.cjpokeroddscalculator.cardrow;

import com.leslie.cjpokeroddscalculator.GlobalStatic;
import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.fragment.TexasHoldemFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class RangeRow extends CardRow {
    public List<List<Set<String>>> matrix;

    public RangeRow() {
        this.matrix = new ArrayList<>(13);
        for (int rowIdx = 0; rowIdx < 13; rowIdx++) {
            List<Set<String>> row = new ArrayList<>(13);
            for (int colIdx = 0; colIdx < 13; colIdx++) {
                row.add(new HashSet<>());
            }
            this.matrix.add(row);
        }
    }

    @Override
    public void clear(EquityCalculatorFragment equityCalculatorFragment, int rowIdx) {
        TexasHoldemFragment texasHoldemFragment = (TexasHoldemFragment) equityCalculatorFragment;
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                this.matrix.get(i).get(j).clear();
            }
        }

        texasHoldemFragment.rangeButtonList.get(rowIdx - 1).setImageBitmap(texasHoldemFragment.emptyRangeBitmap);
    }

    @Override
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

    @Override
    public String convertTexasHoldemPlayerCardsToStr() {
        StringJoiner sj = new StringJoiner(",");
        String firstRank, secondRank;

        for (int rowIdx = 0; rowIdx < 13; rowIdx++) {
            for (int colIdx = 0; colIdx < 13; colIdx++) {
                if (rowIdx == colIdx) {
                    firstRank = GlobalStatic.rankStrings[rowIdx];
                    secondRank = GlobalStatic.rankStrings[rowIdx];
                } else if (colIdx > rowIdx) {
                    firstRank = GlobalStatic.rankStrings[rowIdx];
                    secondRank = GlobalStatic.rankStrings[colIdx];
                } else {
                    firstRank = GlobalStatic.rankStrings[colIdx];
                    secondRank = GlobalStatic.rankStrings[rowIdx];
                }

                for (String s : this.matrix.get(rowIdx).get(colIdx)) {
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
