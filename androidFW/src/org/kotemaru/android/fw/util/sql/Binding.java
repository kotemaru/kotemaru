package org.kotemaru.android.fw.util.sql;


class Binding {
	int mIndexsSize = 0;
	int[] mIndexs = new int[2];

	public Binding(String name) {
	}
	public void addIndex(int idx) {
		if (mIndexs.length >= mIndexsSize) {
			int[] newIndexs = new int[mIndexs.length * 2];
			System.arraycopy(mIndexs, 0, newIndexs, 0, mIndexs.length);
			mIndexs = newIndexs;
		}
		mIndexs[mIndexsSize++] = idx;
	}
}
