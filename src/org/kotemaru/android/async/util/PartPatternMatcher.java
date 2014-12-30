package org.kotemaru.android.async.util;


/**
 * 分割ストリームからパターンを検出するユーテリティ。
 * @author kotemaru.org
 */
public class PartPatternMatcher {
	
	private byte[] mPattern;
	private int mMatchPos;
	
	/**
	 * @param pattern 固定パターン。
	 */
	public PartPatternMatcher(byte[] pattern) {
		mPattern = pattern;
	}

	/**
	 * パターン検出。
	 * - 前回の呼び出しを跨ぐパターンも検出する。
	 * @param buff
	 * @param off
	 * @param len
	 * @return 0以上=検出位置。offから相対。パターンの最後。-1=未検出。
	 */
	public int find(byte[] buff, int off, int len) {
		for (int i=0; i<len; i++) {
			if (buff[off+i] == mPattern[mMatchPos]) {
				if (++mMatchPos >= mPattern.length) {
					mMatchPos = 0;
					return i;
				}
			} else {
				mMatchPos = 0;
			}
		}
		return -1;
	}
	
	public void reset() {
		mMatchPos = 0;
	}
	
}
