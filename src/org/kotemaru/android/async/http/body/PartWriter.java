package org.kotemaru.android.async.http.body;

import java.io.IOException;

import org.kotemaru.android.async.BufferTransporter;
/**
 * リクエスト本文を分割して書き込むためのインターフェース。
 * - Chenkedと平文の処理を共通化するためのもの。
 * @author kotemaru.org
 */
public interface PartWriter {
	public interface PartWriterListener {
		/**
		 * 書き込みデータがなくなると呼び出される。
		 * @throws IOException 入力側のIOエラー
		 */
		public void onNextBuffer(BufferTransporter transporter) throws IOException;
	}
	
	/**
	 * 書き込み可能の通知。
	 * @return 書き込みサイズ。-1=終了。
	 * @throws IOException
	 */
	public int onWritable() throws IOException;
}
