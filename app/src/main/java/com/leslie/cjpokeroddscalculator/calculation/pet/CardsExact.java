package com.leslie.cjpokeroddscalculator.calculation.pet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * generate all possible remaining boards
 */
public class CardsExact extends Cards {

	/** number of combinations */
	private final int[] countArray;
	/** number of cards to pick */
	private final int[] kArray;
	/** combination number */
	private final int[] pArray;

	private final List<List<String>> remainingDecks;


	public CardsExact(String[] deck, String[] currentBoard, String[][] currentHoleCards) {
		super(deck, currentBoard, currentHoleCards);

		pArray = new int[currentHoleCards.length + 1];
		remainingDecks = new ArrayList<>(currentHoleCards.length + 1);
		kArray = new int[currentHoleCards.length + 1];
		countArray = new int[currentHoleCards.length + 1];

		List<String> remainingDeck = new ArrayList<>(Arrays.asList(deck));

		for(int i = 0; i < cards.length; i++) {
			remainingDecks.add(new ArrayList<>(remainingDeck));
			kArray[i] = cards[i].length - currentCards[i].length;
			countArray[i] = MathsUtil.binomialCoefficientFast(remainingDeck.size(), kArray[i]);

			MathsUtil.kCombination(kArray[i], pArray[i], remainingDeck.toArray(new String[0]), cards[i], currentCards[i].length);

			for (int j = currentCards[i].length; j < cards[i].length; j++) {
				remainingDeck.remove(cards[i][j]);
			}
		}
	}
	
	@Override
	public int count() {
		int totalCount = 1;

		for (int count : countArray) {
			totalCount = Math.multiplyExact(totalCount, count);
		}

		return totalCount;
	}

	@Override
	void next() {
		int row;
		for (row = pArray.length - 1; row > 0; row--) {
			if (pArray[row] >= countArray[row]) {
				pArray[row] = 0;
				pArray[row - 1]++;
			} else {
				break;
			}
		}

		if (row == pArray.length - 1) {
			MathsUtil.kCombination(kArray[row], pArray[row], remainingDecks.get(row).toArray(new String[0]), cards[row], currentCards[row].length);
		} else {
			List<String> remainingDeck = new ArrayList<>(remainingDecks.get(row));

			for (int i = row; i < cards.length; i++) {
				if (i > row) {
					remainingDecks.set(i, new ArrayList<>(remainingDeck));
				}

				MathsUtil.kCombination(kArray[i], pArray[i], remainingDeck.toArray(new String[0]), cards[i], currentCards[i].length);

				if (i < cards.length - 1) {
					for (int j = currentCards[i].length; j < cards[i].length; j++) {
						remainingDeck.remove(cards[i][j]);
					}
				}
			}
		}

		pArray[pArray.length - 1]++;
	}
}