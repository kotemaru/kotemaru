package org.kotemaru.android.async.helper;

import java.nio.ByteBuffer;

public interface PartConsumer {
	/**
	 * 平文の状態のレスポンスの一部。
	 * @param buffer nullはEOF
	 */
	public void postPart(ByteBuffer buffer);
}
