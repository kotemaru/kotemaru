package org.kotemaru.android.async.http.body;


public class PatternMatcher {
	
	private byte[] mPattern;
	private int mMatchPos;
	
	public PatternMatcher(byte[] pattern) {
		mPattern = pattern;
	}

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
}
