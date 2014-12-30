package org.kotemaru.android.async.http.body;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.kotemaru.android.async.BufferTransporter;

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
	private final BufferTransporter mBufferTransporter = new BufferTransporter() {
		@Override
		public ByteBuffer read() throws IOException {
			throw new UnsupportedOperationException();
		}
		@Override
		public void release(ByteBuffer buffer) {
			throw new UnsupportedOperationException();
		}
		@Override
		public int write(ByteBuffer buffer) throws IOException {
			return postBuffer(buffer);
		}
	};

	public StreamPartWriter(SocketChannel channel, PartWriterListener partWriterListener) throws IOException {
		mChannel = channel;
		mPartWriterListener = partWriterListener;
	}

	@Override
	public int onWritable() throws IOException {
		if (mState == State.PREPARE) {
			mPartWriterListener.onNextBuffer(mBufferTransporter);
			return 0;
		}
		if (mState == State.DONE) return -1;
		if (!mBuffer.hasRemaining()) {
			mPartWriterListener.onNextBuffer(mBufferTransporter);
			return 0;
		}
		int n = mChannel.write(mBuffer);
		return n;
	}
	
	private int postBuffer(ByteBuffer buffer) throws IOException {
		mBuffer = buffer;
		if (mBuffer == null) {
			mState = State.DONE;
			return -1;
		}
		mState = State.DATA;
		return onWritable();
	}

}
