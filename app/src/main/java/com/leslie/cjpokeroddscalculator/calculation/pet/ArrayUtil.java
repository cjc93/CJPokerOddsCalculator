package com.leslie.cjpokeroddscalculator.calculation.pet;

import java.util.*;

/**
 * utilities for arrays.
 * Note: if you want subarrays, use Arrays.copyOfRange
 */
public class ArrayUtil {

	/**
	 * pick a value from a (max length 63) that hasn't been picked before
	 * according to picked[0] and update picked[0]
	 */
	public static String pick(Random r, String[] a, long[] picked) {
		while (true) {
			int i = r.nextInt(a.length);
			long m = 1L << i;
			if ((picked[0] & m) == 0) {
				picked[0] |= m;
				return a[i];
			}
		}
	}
}
