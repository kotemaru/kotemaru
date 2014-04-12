package org.kotemaru.android.irrc;

public class Bytes {
	private byte[] bytes;

	public Bytes() {
		this(null);
	}

	public Bytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}
