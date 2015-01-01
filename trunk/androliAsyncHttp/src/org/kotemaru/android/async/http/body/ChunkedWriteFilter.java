package org.kotemaru.android.async.http.body;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.kotemaru.android.async.ByteBufferWriter;
import org.kotemaru.android.async.helper.PartProducer;
import org.kotemaru.android.async.helper.WritableListener;
import org.kotemaru.android.async.http.HttpUtil;
import org.kotemaru.android.async.ssl.SelectorItem;

/**
 * Chunked フォーマットのストリームを分割して書き込むためのクラス。
 * - 平文->Chunked 文のフィルター。
 * @author kotemaru.org
 */
public class ChunkedWriteFilter implements WritableListener, ByteBufferWriter {

	private final ByteBuffer mSizeLineBuffer = ByteBuffer.wrap(new byte[10]);
	private ByteBuffer mBuffer;
	private final ByteBuffer mCrlfBuffer = ByteBuffer.wrap(HttpUtil.CRLF);

	enum State {
		PREPARE, DATA, DONE
	}

	private State mState = State.PREPARE;
	private final SelectorItem mSelectorItem;
	private final PartProducer mPartProducer;

	public ChunkedWriteFilter(SelectorItem channel, PartProducer partProducer) throws IOException {
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

		if (mSizeLineBuffer.hasRemaining()) {
			return mSelectorItem.write(mSizeLineBuffer);
		} else if (mBuffer.hasRemaining()) {
			return mSelectorItem.write(mBuffer);
		} else if (mCrlfBuffer.hasRemaining()) {
			return mSelectorItem.write(mCrlfBuffer);
		}
		mPartProducer.requestNextPart(this);
		return 0;
	}

	public int write(ByteBuffer buffer) throws IOException {
		mBuffer = buffer;
		if (mBuffer == null) {
			mState = State.DONE;
			return -1;
		}
		mState = State.DATA;

		byte[] size = Integer.toHexString(mBuffer.remaining()).getBytes();
		mSizeLineBuffer.clear();
		mSizeLineBuffer.put(size).put(HttpUtil.CRLF);
		mCrlfBuffer.rewind();
		return onWritable();
	}

}
