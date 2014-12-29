package org.kotemaru.android.async.http.body;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 平文 フォーマットのストリームを分割して書き込むためのクラス。
 * - 平文->平文 文のフィルターなので実質なにもしない。
 * @author kotemaru.org
 */
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
		if (mState == State.DONE) return -1;

		if (mBuffer.hasRemaining()) {
			return mChannel.write(mBuffer);
		}
		onNextBuffer();
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
