package org.kotemaru.android.async.http.body;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.kotemaru.android.async.ByteBufferWriter;
import org.kotemaru.android.async.helper.PartProducer;
import org.kotemaru.android.async.helper.WritableListener;
import org.kotemaru.android.async.ssl.SelectorItem;

/**
 * 平文 フォーマットのストリームを分割して書き込むためのクラス。
 * - 平文->平文 文のフィルターなので実質なにもしない。
 * @author kotemaru.org
 */
public class StreamWriteFilter implements WritableListener, ByteBufferWriter {
	private ByteBuffer mBuffer;

	enum State {
		PREPARE, DATA, DONE
	}

	private State mState = State.PREPARE;
	private final SelectorItem mSelectorItem;
	private final PartProducer mPartProducer;

	public StreamWriteFilter(SelectorItem channel, PartProducer partProducer) throws IOException {
		mSelectorItem = channel;
		mPartProducer = partProducer;
	}

	@Override
	public int onWritable() throws IOException {
		if (mState == State.PREPARE) {
			mPartProducer.requestNextPart(this);
			return 0;
		}
		if (mState == State.DONE) return -1;
		if (!mBuffer.hasRemaining()) {
			mPartProducer.requestNextPart(this);
			return 0;
		}
		int n = mSelectorItem.write(mBuffer);
		return n;
	}

	@Override
	public int write(ByteBuffer buffer) throws IOException {
		mBuffer = buffer;
		if (mBuffer == null) {
			mState = State.DONE;
			return -1;
		}
		mState = State.DATA;
		return onWritable();
	}

}
