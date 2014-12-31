package org.kotemaru.android.async.helper;

import java.io.IOException;

public interface WritableListener {
	/**
	 * 書き込み可能の通知。
	 * @return 書き込みサイズ。-1=終了。
	 * @throws IOException
	 */
	public int onWritable() throws IOException;
}
