package org.kotemaru.android.fw.util.image;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ResetableNetInputStream extends BufferedInputStream {
	private boolean mIsResetable = true;

	protected ResetableNetInputStream(InputStream in) throws FileNotFoundException {
		super(in, 1024);
	}
	@Override
	public boolean markSupported() {
		return false;
	}
	
	@Override
	public void reset() throws IOException {
		if (!mIsResetable) {
			throw new IOException("Reset is one chance.");
		}
		mIsResetable = false;
		super.reset();
	}

	@Override
	public int read() throws IOException {
		if ((pos + 1) >= buf.length) expandBuffer();
		return super.read();
	}
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if ((pos + len) >= buf.length) expandBuffer();
		return super.read(b, off, len);
	}

	private void expandBuffer() {
		if (!mIsResetable) return;
		byte[] newBuf = new byte[buf.length * 2];
		System.arraycopy(buf, 0, newBuf, 0, pos);
		super.buf = newBuf;
	}

}
