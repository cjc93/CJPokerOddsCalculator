package com.leslie.cjpokeroddscalculator;

public class CardRow {
    public int[][] cards;

    public CardRow(int numOfCards) {
        cards = new int[numOfCards][];

        for (int i = 0; i < numOfCards; i++) {
            cards[i] = new int[]{0, 0};
        }
    }
}
