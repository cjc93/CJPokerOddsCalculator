package com.leslie.cjpokeroddscalculator;

import java.util.Arrays;

public class CardRow {
    public int[][] cards;
    public String rowType = "specific";

    public CardRow(int numOfCards) {
        cards = new int[numOfCards][];

        for (int i = 0; i < numOfCards; i++) {
            cards[i] = new int[]{0, 0};
        }
    }

    public CardRow(CardRow other)
    {
        this.rowType = other.rowType ;

        this.cards = new int[other.cards.length][];
        for (int i = 0; i < other.cards.length; i++) {
            this.cards[i] = Arrays.copyOf(other.cards[i], other.cards[i].length);
        }
    }
}
