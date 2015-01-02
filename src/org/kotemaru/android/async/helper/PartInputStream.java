package org.kotemaru.android.async.helper;

import java.io.InputStream;

import org.kotemaru.android.async.ByteBufferWriter;

public abstract class PartInputStream extends InputStream implements ByteBufferWriter {
	public abstract long getLength();
	public abstract void writeClose();
}
