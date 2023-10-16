package com.leslie.cjpokeroddscalculator;

import java.util.Arrays;

public class CardRow {
    public int[][] cards;
    public String rowType = "specific";
    public boolean[][] matrix = new boolean[13][13];

    public CardRow(int numOfCards) {
        cards = new int[numOfCards][];

        for (int i = 0; i < numOfCards; i++) {
            cards[i] = new int[]{0, 0};
        }

        for (int row_idx = 0; row_idx < 13; row_idx++) {
            for (int col_idx = 0; col_idx < 13; col_idx++) {
                this.matrix[row_idx][col_idx] = false;
            }
        }
    }

    public CardRow(CardRow other)
    {
        this.rowType = other.rowType ;

        this.cards = new int[other.cards.length][];
        for (int i = 0; i < other.cards.length; i++) {
            this.cards[i] = Arrays.copyOf(other.cards[i], other.cards[i].length);
        }

        this.matrix = new boolean[13][];
        for (int i = 0; i < 13; i++) {
            this.matrix[i] = Arrays.copyOf(other.matrix[i], 13);
        }
    }
}
