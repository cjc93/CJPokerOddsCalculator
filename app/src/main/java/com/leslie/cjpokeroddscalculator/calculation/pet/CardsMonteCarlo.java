package com.leslie.cjpokeroddscalculator.calculation.pet;

import java.util.Random;

/**
 * generate sample remaining boards
 */
public class CardsMonteCarlo extends Cards {
	private final long[] picked = new long[1];
	private final int count;
	private final Random r = new Random();
	
	public CardsMonteCarlo(String[] deck, String[] currentBoard, String[][] currentHoleCards, int count) {
		super(deck, currentBoard, currentHoleCards);
		this.count = count;
	}
	
	@Override
	int count() {
		return count;
	}

	@Override
	void next() {
		picked[0] = 0;

		for (int i = 0; i < currentCards.length; i++) {
			for (int j = currentCards[i].length; j < cards[i].length; j++) {
				cards[i][j] = ArrayUtil.pick(r, deck, picked);
			}
		}
	}
}