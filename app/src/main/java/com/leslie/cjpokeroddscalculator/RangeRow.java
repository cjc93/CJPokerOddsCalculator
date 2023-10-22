package com.leslie.cjpokeroddscalculator;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

public class RangeRow extends CardRow{
    public boolean[][] matrix = new boolean[13][13];

    public RangeRow() {
        for (int row_idx = 0; row_idx < 13; row_idx++) {
            for (int col_idx = 0; col_idx < 13; col_idx++) {
                this.matrix[row_idx][col_idx] = false;
            }
        }
    }

    public RangeRow(RangeRow other) {
        this.matrix = new boolean[13][];
        for (int i = 0; i < 13; i++) {
            this.matrix[i] = Arrays.copyOf(other.matrix[i], 13);
        }
    }

    public void clear(MainActivity mainActivity, int row_idx) {
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                this.matrix[i][j] = false;
            }
        }

        Objects.requireNonNull(mainActivity.rangePositionBiMap.get(row_idx)).setImageBitmap(mainActivity.emptyRangeBitmap);
    }

    public CardRow copy() {
        return new RangeRow(this);
    }

    public void copyImageBelow(MainActivity mainActivity, int row_idx) {
        mainActivity.twoCardsLayouts[row_idx - 1].setVisibility(View.GONE);
        Drawable d = Objects.requireNonNull(mainActivity.rangePositionBiMap.get(row_idx + 1)).getDrawable();
        Objects.requireNonNull(mainActivity.rangePositionBiMap.get(row_idx)).setImageDrawable(d);
        Objects.requireNonNull(mainActivity.rangePositionBiMap.get(row_idx)).setVisibility(View.VISIBLE);
    }

    public boolean isKnownPlayer() {
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                if (this.matrix[i][j] != this.matrix[0][0]) {
                    return true;
                }
            }
        }

        return false;
    }

    public String convertPlayerCardsToStr() {
        StringJoiner sj = new StringJoiner(",");

        for (int row_idx = 0; row_idx < 13; row_idx++) {
            for (int col_idx = 0; col_idx < 13; col_idx++) {
                if (this.matrix[row_idx][col_idx]) {
                    if (row_idx == col_idx) {
                        sj.add(GlobalStatic.matrixStrings[row_idx] + GlobalStatic.matrixStrings[row_idx]);
                    } else if (col_idx > row_idx) {
                        sj.add(GlobalStatic.matrixStrings[row_idx] + GlobalStatic.matrixStrings[col_idx] + "s");
                    } else {
                        sj.add(GlobalStatic.matrixStrings[col_idx] + GlobalStatic.matrixStrings[row_idx] + "o");
                    }
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
