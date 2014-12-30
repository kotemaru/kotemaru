package org.kotemaru.android.async.http.body;

import java.nio.ByteBuffer;

/**
 * 平文のストリームを分割して読み込むためのクラス。
 * - 平文 -> 平文のフィルターなので実質なにもしない。
 * - 終端チェックのみ。
 * @author kotemaru.org
 */
public class StreamPartReader implements PartReader {
	private final PartReaderListener mPartReaderListener;
	private final long mContentLength;
	private long readingCount = 0;

	public StreamPartReader(PartReaderListener chunkedListener, long contentLength) {
		mPartReaderListener = chunkedListener;
		mContentLength = contentLength;
	}

	@Override
	public void postPart(ByteBuffer buffer) {
		if (buffer != null) {
			mPartReaderListener.onPart(buffer);
			readingCount += buffer.remaining();
			if (mContentLength > 0 && readingCount >= mContentLength) {
				mPartReaderListener.onFinish();
			}
		} else {
			mPartReaderListener.onFinish();
		}
	}

}
