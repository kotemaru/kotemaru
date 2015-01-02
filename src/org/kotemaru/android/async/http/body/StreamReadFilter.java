package org.kotemaru.android.async.http.body;

import java.nio.ByteBuffer;

import org.kotemaru.android.async.helper.PartConsumer;

/**
 * 平文のストリームを分割して読み込むためのクラス。
 * - 平文 -> 平文のフィルターなので実質なにもしない。
 * - 終端チェックのみ。
 * @author kotemaru.org
 */
public class StreamReadFilter implements PartConsumer {
	private final PartConsumer mPartConsumer;
	private final long mContentLength;
	private long readingCount = 0;

	public StreamReadFilter(PartConsumer partConsumer, long contentLength) {
		mPartConsumer = partConsumer;
		mContentLength = contentLength;
	}

	@Override
	public void postPart(ByteBuffer buffer) {
		if (buffer != null) {
			mPartConsumer.postPart(buffer);
			readingCount += buffer.remaining();
			if (mContentLength > 0 && readingCount >= mContentLength) {
				mPartConsumer.postPart(null);
			}
		} else {
			mPartConsumer.postPart(null);
		}
	}

}
