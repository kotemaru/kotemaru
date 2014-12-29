package org.kotemaru.android.async.http.body;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.kotemaru.android.async.http.HttpUtil;

public class ChunkedPartWriter implements PartWriter {

	private final ByteBuffer mSizeLineBuffer = ByteBuffer.wrap(new byte[10]);
	private ByteBuffer mBuffer;
	private final ByteBuffer mCrlfBuffer = ByteBuffer.wrap(HttpUtil.CRLF);

	enum State {
		PREPARE, DATA, DONE
	}

	private State mState = State.PREPARE;
	private final SocketChannel mChannel;
	private final PartWriterListener mPartWriterListener;

	public ChunkedPartWriter(SocketChannel channel, PartWriterListener partWriterListener) {
		mChannel = channel;
		mPartWriterListener = partWriterListener;
	}

	public int doWrite() throws IOException {
		if (mState == State.PREPARE) onNextBuffer();
		if (mState == State.DONE) return -1;

		if (mSizeLineBuffer.hasRemaining()) {
			return mChannel.write(mSizeLineBuffer);
		} else if (mBuffer.hasRemaining()) {
			return mChannel.write(mBuffer);
		} else if (mCrlfBuffer.hasRemaining()) {
			return mChannel.write(mCrlfBuffer);
		}
		return doWrite();
	}

	private void onNextBuffer() throws IOException {
		mBuffer = mPartWriterListener.onNextBuffer();
		if (mBuffer == null) {
			mState = State.DONE;
			return;
		}
		mState = State.DATA;

		byte[] size = Integer.toHexString(mBuffer.remaining()).getBytes();
		mSizeLineBuffer.clear();
		mSizeLineBuffer.put(size).put(HttpUtil.CRLF);
		mCrlfBuffer.rewind();
	}
}
