package com.leslie.cjpokeroddscalculator.calculation.pet;

import java.util.*;

/**
 * Poker hand valuation.
 */
public abstract class Poker {

	/*
	 * poker hand values are represented by 7 x 4 bit values (28 bits total):
	 * 0x87654321
	 * 8 = 0
	 * 7 = hand type (high, a-5 low, 2-7 low, badugi)
	 * 6 = rank
	 * 5 = most significant card (if any)
	 * ...
	 * 1 = least significant card
	 */

	/** rank mask (allowing 20 bits for hand value, i.e. 4 bits per card) */
	protected static final int RANK = 0x00f00000;

	/** high card bit mask (always zero) */
	protected static final int H_RANK = 0;
	/** pair rank bit mask */
	protected static final int P_RANK = 0x100000;
	/** two pair rank bit mask */
	protected static final int TP_RANK = 0x200000;
	/** three of a kind rank bit mask */
	protected static final int TK_RANK = 0x300000;
	/** straight bit mask */
	public static final int ST_RANK = 0x400000;
	/** flush bit mask */
	protected static final int FL_RANK = 0x500000;
	/** full house bit mask */
	protected static final int FH_RANK = 0x600000;
	/** four of a kind bit mask */
	protected static final int FK_RANK = 0x700000;
	/** straight flush rank mask */
	protected static final int SF_RANK = 0x800000;

	/** impossible rank higher than straight flush for low hand value purposes */
	protected static final int MAX_RANK = 0x900000;

	/** complete deck in face then suit order, lowest first */
	private static final String[] deckArr = { 
		"2h", "2s", "2c", "2d",
		"3h", "3s", "3c", "3d", "4h", "4s", "4c", "4d", "5h", "5s", "5c",
		"5d", "6h", "6s", "6c", "6d", "7h", "7s", "7c", "7d", "8h", "8s",
		"8c", "8d", "9h", "9s", "9c", "9d", "Th", "Ts", "Tc", "Td", "Jh",
		"Js", "Jc", "Jd", "Qh", "Qs", "Qc", "Qd", "Kh", "Ks", "Kc", "Kd",
		"Ah", "As", "Ac", "Ad" 
	};
	
	public static final String[] deckArrS;
	
	public static final List<String> deck = Collections.unmodifiableList(Arrays.asList(deckArr));

	static {
		deckArrS = deckArr.clone();
		Arrays.sort(deckArrS);
	}

	/**
	 * count low cards
	 */
	protected static int lowCount(String[] hand) {
		int count = 0;
		for (String s : hand) {
            if (faceValue(s, false) <= 8) {
                count++;
            }
        }
		return count;
	}

	/**
	 * get 8 or better qualified ace to five low value of hand.
	 * returns 0 if no low.
	 */
	static int afLow8Value(String[] hand) {
		if (lowCount(hand) == 5) {
			int p = isPair(hand, false);
			if (p < P_RANK) {
				// no pairs
				// invert value
                return MAX_RANK - p;
			}
		}
		return 0;
	}

	/**
	 * Get value of 5 card hand
	 */
	public static int value(String[] hand) {
		int p = isPair(hand, true);
		if (p < P_RANK) {
			boolean f = isFlush(hand);
			int s = isStraight(hand);
			if (f) {
				if (s > 0) {
					p = SF_RANK | s;
				} else {
					p = FL_RANK | p;
				}
			} else if (s > 0) {
				p = ST_RANK | s;
			}
		}

		return p;
	}
	
	/**
	 * return true if flush
	 */
	private static boolean isFlush(String[] hand) {
		char s = suit(hand[0]);
		for (int n = 1; n < 5; n++) {
			if (suit(hand[n]) != s) {
				return false;
			}
		}
		return true;
	}
	
	/** 
	 * return value of high card of straight (5-14) or 0 
	 */
	private static int isStraight(String[] hand) {
		int x = 0;
		// straight value
		int str = 5;
		for (String s : hand) {
            // sub 1 so bottom bit equals ace low
            int v = faceValueAH(s) - 1;
            x |= (1 << v);
            if (v == 13) {
                // add ace low as well as ace high
                x |= 1;
            }
        }
		// [11111000000001]
		while (x >= 31) {
			if ((x & 31) == 31) {
				return str;
			}
			x >>= 1;
			str++;
		}
		return 0;
	}
	
	/**
	 * Return pair value or high cards without type mask.
	 * Does not require sorted hand
	 */
	private static int isPair(String[] hand, boolean acehigh) {
		// count card face frequencies (3 bits each) -- 0, 1, 2, 3, 4
		long v = 0;
        for (String s : hand) {
            v += (1L << ((14 - faceValue(s, acehigh)) * 3));
        }
		// get the card faces for each frequency
		int fk = 0, tk = 0, pa = 0, hc = 0;
		for (int f = 14; v != 0; v >>= 3, f--) {
			int i = (int) (v & 7);
            if (i == 1) {
				hc = (hc << 4) | f;
			} else if (i == 2) {
				pa = (pa << 4) | f;
			} else if (i == 3) {
				tk = f;
			} else if (i == 4) {
				fk = f;
			}
		}
		
		if (fk != 0) {
			return FK_RANK | (fk << 4) | hc;
		} else if (tk != 0) {
			if (pa != 0) {
				return FH_RANK | (tk << 4) | pa;
			} else {
				return TK_RANK | (tk << 8) | hc;
			}
		} else if (pa >= 16) {
			return TP_RANK | (pa << 4) | hc;
		} else if (pa != 0) {
			return P_RANK | (pa << 12) | hc;
		} else {
			return H_RANK | hc;
		}
	}

	/**
	 * Return integer value of card face, ace high or low (from A = 14 to 2 = 2 or K = 13 to A = 1)
	 */
	static int faceValue(String card, boolean acehigh) {
		if (acehigh) {
			return faceValueAH(card);
		} else {
			return faceValueAL(card);
		}
	}

	/** face value, ace low (A = 1, K = 13) */
	static int faceValueAL (String card) {
		int i = "A23456789TJQK".indexOf(face(card));
		return i + 1;
	}

	/** face value, ace high (2 = 2, A = 14) */
	static int faceValueAH (String card) {
		int i = "23456789TJQKA".indexOf(face(card));
		return i + 2;
	}
	
	/**
	 * Returns lower case character representing suit, i.e. s, d, h or c
	 */
	public static char suit(String card) {
		return card.charAt(1);
	}

	public static char face(String card) {
		return card.charAt(0);
	}

	/**
	 * return the remaining cards in the deck.
	 * always returns new array
	 */
	public static String[] remdeck(String[][] aa, String[]... a) {
		ArrayList<String> list = new ArrayList<>(deck);
		if (aa != null) {
			for (String[] x : aa) {
				rem1(list, x);
			}
		}
		if (a != null) {
			for (String[] x : a) {
				rem1(list, x);
			}
		}
		return list.toArray(new String[0]);
	}
	
	private static void rem1(List<String> list, String[] a) {
		if (a != null) {
			for (String s : a) {
				if (s != null) {
					list.remove(s);
				}
			}
		}
	}
}
