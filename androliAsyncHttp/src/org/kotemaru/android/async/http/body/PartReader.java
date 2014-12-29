package org.kotemaru.android.async.http.body;

/**
 * レスポンス本文を分割して読み込むためのインターフェース。
 * - Chenkedと平文の処理を共通化するためのもの。
 * @author kotemaru.org
 */
public interface PartReader {
	public interface PartReaderListener {
		/**
		 * 平文の状態のレスポンスの一部。
		 * @param buffer
		 * @param offset
		 * @param length
		 */
		public void onPart(byte[] buffer, int offset, int length);
		
		/**
		 * 読み込み終了。
		 */
		public void onFinish();
	}

	/**
	 * 通信から受け取ったままのデータの一部。
	 * @param buffer
	 * @param offset
	 * @param length
	 */
	public void postPart(byte[] buffer, int offset, int length);
}
