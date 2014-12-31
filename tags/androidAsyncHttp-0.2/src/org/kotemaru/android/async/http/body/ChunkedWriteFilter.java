package org.kotemaru.android.async.http.body;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.kotemaru.android.async.ByteBufferWriter;
import org.kotemaru.android.async.helper.PartProducer;
import org.kotemaru.android.async.helper.WritableListener;
import org.kotemaru.android.async.http.HttpUtil;

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
	private final SocketChannel mChannel;
	private final PartProducer mPartProducer;

	public ChunkedWriteFilter(SocketChannel channel, PartProducer partProducer) throws IOException {
		mChannel = channel;
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
			return mChannel.write(mSizeLineBuffer);
		} else if (mBuffer.hasRemaining()) {
			return mChannel.write(mBuffer);
		} else if (mCrlfBuffer.hasRemaining()) {
			return mChannel.write(mCrlfBuffer);
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
