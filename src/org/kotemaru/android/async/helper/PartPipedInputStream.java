package org.kotemaru.android.async.helper;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PartPipedInputStream extends PartInputStream {

	private ByteBuffer mBuffer;
	private boolean mIsEof = false;
	private boolean mIsWriteClose = false;
	protected byte[] mSingleReadBuffer = new byte[1];
	private PartProducer mPartProducer;

	public PartPipedInputStream(PartProducer listener) {
		mPartProducer = listener;
	}

	@Override
	public synchronized int write(ByteBuffer buffer) {
		mBuffer = buffer;
		if (mBuffer == null) {
			mIsEof = true;
		}
		notifyAll();
		return 0;
	}

	@Override
	public synchronized int read(byte[] buff, int offset, int length) throws IOException {
		while (!mIsEof && !mIsWriteClose && (mBuffer == null || !mBuffer.hasRemaining())) {
			mPartProducer.requestNextPart(this);
			// @formatter:off
			try {wait();} catch (InterruptedException e) { }
			// @formatter:on
		}
		if (mIsEof) return -1;
		if (mIsWriteClose) {
			throw new IOException("Broken pipe.");
		}

		int len = Math.min(length, mBuffer.remaining());
		mBuffer.get(buff, offset, len);
		return len;
	}
	@Override
	public int read() throws IOException {
		int n = read(mSingleReadBuffer, 0, 1);
		if (n == -1) return -1;
		return (int) mSingleReadBuffer[0];
	}
	@Override
	public synchronized int available() {
		if (mIsEof) return -1;
		if (mBuffer == null || !mBuffer.hasRemaining()) {
			return 0;
		}
		return mBuffer.remaining();
	}
	@Override
	public long getLength() {
		return -1;
	}

	@Override
	public synchronized void writeClose() {
		mIsWriteClose = true;
		notifyAll();
	}

}
