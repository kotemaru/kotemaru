package org.kotemaru.android.async.http.body;


public interface PartReader {
	public interface PartReaderListener {
		public void onPart(byte[] buffer, int offset, int length);
		public void onFinish();
	}
	public void postPart(byte[] buffer, int offset, int length);
}
