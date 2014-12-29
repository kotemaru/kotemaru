package org.kotemaru.android.async.http.body;


public class StreamPartReader implements PartReader {
	private final PartReaderListener mPartReaderListener;
	private final long mContentLength;
	private long readingCount = 0;

	public StreamPartReader(PartReaderListener chunkedListener, long contentLength) {
		mPartReaderListener = chunkedListener;
		mContentLength = contentLength;
	}

	public void postPart(byte[] buffer, int offset, int length) {
		if (length >= 0) {
			mPartReaderListener.onPart(buffer, offset, length);
			readingCount += length;
			if (mContentLength > 0 && readingCount >= mContentLength) {
				mPartReaderListener.onFinish();
			}
		} else {
			mPartReaderListener.onFinish();
		}
	}

}
