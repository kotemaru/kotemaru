package org.kotemaru.android.postit.util;

/**
 * 小規模整数マッピング。
 * <li>整数から整数を引くマッピング。
 * <li>リソースIDと定数のマッピングを想定。
 * <li>直線的に検索を行うので10件程度が目安。
 * <li>３項目までサポート。
 * 
 * @author kotemaru.org
 */
public class IntIntMap {
	private int[][] mTable;

	public IntIntMap(int[][] table) {
		mTable = table;
	}

	/**
	 * 第２項目から第１項目を得る。
	 * @param second 検索キー
	 * @return 第一項目。見つからない時は-1。
	 */
	public int getFirst(int second) {
		for (int[] pair : mTable) {
			if (pair[1] == second) return pair[0];
		}
		return -1;
	}
	/**
	 * 第２項目、第３項目から第１項目を得る。
	 * @param second 検索キー
	 * @param third 検索キー
	 * @return 第一項目。見つからない時は-1。
	 */
	public int getFirst(int second, int third) {
		for (int[] pair : mTable) {
			if (pair[1] == second && pair[2] == third) return pair[0];
		}
		return -1;
	}
	/**
	 * 第１項目から第２項目を得る。
	 * @param first 検索キー
	 * @return 第２項目。見つからない時は-1。
	 */
	public int getSecond(int first) {
		for (int[] pair : mTable) {
			if (pair[0] == first) return pair[1];
		}
		return -1;
	}
	/**
	 * 第１項目から第３項目を得る。
	 * @param first 検索キー
	 * @return 第３項目。見つからない時は-1。
	 */
	public int getThird(int first) {
		for (int[] pair : mTable) {
			if (pair[0] == first) return pair[2];
		}
		return -1;
	}

}