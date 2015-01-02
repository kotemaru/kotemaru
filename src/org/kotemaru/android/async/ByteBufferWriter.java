package org.kotemaru.android.async;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ByteBufferWriter {
	/**
	 * ブロックせずに書き込み可能なデータをかきこむ。
	 * @param buffer 書き込みデータ。nullは終了。
	 * @return 書き込んだサイズ。0=すぐに書き込めない。
	 * @throws IOException
	 */
	public int write(ByteBuffer buffer) throws IOException;
}
