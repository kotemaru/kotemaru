package org.kotemaru.android.async.helper;

import java.io.IOException;

public interface ReadableListener {
	public int onReadable() throws IOException;
}
