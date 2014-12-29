package org.kotemaru.android.async.http.body;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class StreamPartWriter implements PartWriter {
	private ByteBuffer mBuffer;

	enum State {
		PREPARE, DATA, DONE
	}

	private State mState = State.PREPARE;
	private final SocketChannel mChannel;
	private final PartWriterListener mPartWriterListener;

	public StreamPartWriter(SocketChannel channel, PartWriterListener partWriterListener) {
		mChannel = channel;
		mPartWriterListener = partWriterListener;
	}

	public int doWrite() throws IOException {
		if (mState == State.PREPARE) onNextBuffer();
		if (mState == State.DATA) return -1;

		if (mBuffer.hasRemaining()) {
			return mChannel.write(mBuffer);
		}
		return doWrite();
	}
	private void onNextBuffer() throws IOException {
		mBuffer = mPartWriterListener.onNextBuffer();
		if (mBuffer != null) {
			mState = State.DATA;
		} else {
			mState = State.DONE;
		}
	}

}
