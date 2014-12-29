package org.kotemaru.android.async.http.body;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface PartWriter {
	public interface PartWriterListener {
		public ByteBuffer onNextBuffer() throws IOException;
	}
	
	public int doWrite() throws IOException;
}
