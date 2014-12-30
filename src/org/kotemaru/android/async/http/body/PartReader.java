package org.kotemaru.android.async.http.body;

import java.nio.ByteBuffer;

/**
 * レスポンス本文を分割して読み込むためのインターフェース。
 * - Chenkedと平文の処理を共通化するためのもの。
 * @author kotemaru.org
 */
public interface PartReader {
	public interface PartReaderListener {
		/**
		 * 平文の状態のレスポンスの一部。
		 * @param buffer nullはEOF
		 */
		public void onPart(ByteBuffer buffer);
		
		/**
		 * 読み込み終了。
		 */
		public void onFinish();
	}

	/**
	 * 通信から受け取ったままのデータの一部。
	 * @param buffer nullはEOF
	 */
	public void postPart(ByteBuffer buffer);
}
