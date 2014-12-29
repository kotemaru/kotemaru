package org.kotemaru.android.async.http.body;

import java.io.IOException;
import java.nio.ByteBuffer;
/**
 * リクエスト本文を分割して書き込むためのインターフェース。
 * - Chenkedと平文の処理を共通化するためのもの。
 * @author kotemaru.org
 */
public interface PartWriter {
	public interface PartWriterListener {
		/**
		 * 書き込みデータがなくなると呼び出される。
		 * @return 次の本文の一部。nullで終了。
		 * @throws IOException 入力側のIOエラー
		 */
		public ByteBuffer onNextBuffer() throws IOException;
	}
	
	/**
	 * 書き込みの実行。
	 * @return 書き込みサイズ。-1=終了。
	 * @throws IOException
	 */
	public int doWrite() throws IOException;
}
