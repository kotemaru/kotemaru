package org.kotemaru.android.async.helper;

import java.io.IOException;

import org.kotemaru.android.async.ByteBufferWriter;

public interface PartProducer {
	/**
	 * 書き込みデータがなくなると呼び出される。
	 * @throws IOException 入力側のIOエラー
	 */
	public void requestNextPart(ByteBufferWriter transporter) throws IOException;
}
