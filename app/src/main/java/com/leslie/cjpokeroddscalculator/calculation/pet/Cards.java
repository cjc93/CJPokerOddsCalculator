package com.leslie.cjpokeroddscalculator.calculation.pet;


/**
 * methods for getting complete omaha boards, either
 * randomly or with combinatorial enumeration
 */
public abstract class Cards {

	final String[][] currentCards;

	/** remaining cards in deck, never changes */
	final String[] deck;

	String[][] cards;

	public Cards(String[] deck, String[] currentBoard, String[][] currentHoleCards) {
		this.deck = deck;

		this.currentCards = new String[currentHoleCards.length + 1][];

		this.currentCards[0] = currentBoard;

		System.arraycopy(currentHoleCards, 0, this.currentCards, 1, currentHoleCards.length);

		this.cards = new String[currentHoleCards.length + 1][];
		this.cards[0] = new String[5];
		for (int i = 0; i < currentHoleCards.length; i++) {
			this.cards[i + 1] = new String[4];
		}

		for (int i = 0; i < currentCards.length; i++) {
            System.arraycopy(currentCards[i], 0, this.cards[i], 0, currentCards[i].length);
		}
	}
	/** how many boards are there */
	abstract int count();
	/** create the next board */
	abstract void next();
}
	
