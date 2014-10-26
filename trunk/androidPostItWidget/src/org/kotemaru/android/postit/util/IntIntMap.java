package org.kotemaru.android.postit.util;

public class IntIntMap {
	private int[][] mTable;

	public IntIntMap(int[][] table) {
		mTable = table;
	}

	public int getFirst(int second) {
		for (int[] pair : mTable) {
			if (pair[1] == second) return pair[0];
		}
		return -1;
	}
	public int getFirst(int second, int third) {
		for (int[] pair : mTable) {
			if (pair[1] == second && pair[2] == third) return pair[0];
		}
		return -1;
	}
	public int getSecond(int first) {
		for (int[] pair : mTable) {
			if (pair[0] == first) return pair[1];
		}
		return -1;
	}
	public int getThird(int first) {
		for (int[] pair : mTable) {
			if (pair[0] == first) return pair[2];
		}
		return -1;
	}

}