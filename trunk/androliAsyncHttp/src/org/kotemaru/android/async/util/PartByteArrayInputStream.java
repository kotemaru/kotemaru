package org.kotemaru.android.async.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * 分割式のByteArrayInputStream実装。
 * - レスポンス本文をInputStreamで扱うためのクラス。
 * - ByteArray{In/Out}putStreamが非効率なため代替用。
 * @author kotemaru.org
 */
public class PartByteArrayInputStream extends InputStream {
	protected static class Part {
		Part next;
		byte[] buffer;
	}
	
	protected Part mRoot;
	protected Part mBottom;
	protected Part mCurrent;
	protected int mOffset;
	protected int mLength;
	protected byte[] mSingleReadBuffer = new byte[1];

	public PartByteArrayInputStream() {
		clear();
	}
	
	public void clear() {
		mRoot = new Part();
		mBottom = mRoot;
		mCurrent = mRoot;
		mOffset = 0;
		mLength = 0;
	}
	public void addPart(byte[] buffer, int offset, int length) {
		Part part = new Part();
		part.buffer = new byte[length];
		System.arraycopy(buffer, offset, part.buffer, 0, length);

		mBottom.next = part;
		mBottom = part;
		mLength += part.buffer.length;
		if (mCurrent == mRoot) {
			mCurrent = mRoot.next;
		}
	}
	public int getLength() {
		return mLength;
	}
	
	public void rewind() {
		mCurrent = mRoot.next;
		mOffset = 0;
	}

	@Override
	public int read(byte[] buff, int off, int len) throws IOException {
		if (mCurrent == null) return -1;
		int length = Math.min(mCurrent.buffer.length - mOffset, len);
		System.arraycopy(mCurrent.buffer, mOffset, buff, off, length);
		mOffset += length;
		if (mOffset >= mCurrent.buffer.length) {
			mCurrent = mCurrent.next;
			mOffset = 0;
		}
		return length;
	}
	@Override
	public int read() throws IOException {
		int n = read(mSingleReadBuffer,0,1);
		if (n == -1) return -1;
		return (int)mSingleReadBuffer[0];
	}
	@Override
	public int available() {
		if (mCurrent == null) return -1;
		int avail = mCurrent.buffer.length - mOffset;
		return avail;
	}
}
