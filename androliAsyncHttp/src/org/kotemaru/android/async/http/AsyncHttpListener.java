package org.kotemaru.android.async.http;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.kotemaru.android.async.BufferTranspoter;

/**
 * 非同期HTTP通信のリスナ。
 * - 各種タイミングでコールバックされる。
 * - 実行スレッドは全て SelectorThread となる。
 * -- 長時間実行すると他の通信処理が止まるので注意。
 * @author kotemaru.org
 */
public interface AsyncHttpListener {
	/**
	 * 接続完了後。
	 */
	public void onConnect(HttpRequest httpRequest);
	/**
	 * リクエストヘッダが送信された。
	 */
	public void onRequestHeader(HttpRequest httpRequest);
	/**
	 * リクエスト本文が送信された。（ボディがある場合のみ）
	 */
	public void onRequestBody(HttpRequest httpRequest);

	/**
	 * レスポンスヘッダを受信した。
	 * @param httpResponse レスポンス。ヘッダ情報のみ。
	 */
	public void onResponseHeader(HttpResponse httpResponse);
	/**
	 * レスポンス本文を受信した。
	 * @param httpResponse レスポンス。
	 */
	public void onResponseBody(HttpResponse httpResponse);
	/**
	 * 通信中にエラーが発生した。
	 * - 以降の通信は行われない。
	 * @param msg エラーメッセージ
	 * @param t 原因の例外。null もある。
	 */
	public void onError(String msg, Throwable t);

	/**
	 * リクエスト本文を分割処理する場合、true を返す。
	 * - HttpEntity.getContent()の替りに onRequestBodyPart() が呼ばれるようになる。
	 * - 但し、HttpEntity 自体は必要。Content が不要なだけ。
	 * @return true=分割モード
	 */
	public boolean isRequestBodyPart();
	/**
	 * レスポンス本文を分割処理する場合、true を返す。
	 * - onResponseBodyPart() が呼ばれるようになる。
	 * - HttpEntity.getContent() は null が戻るようになる。
	 * @return true=分割モード
	 */
	public boolean isResponseBodyPart();

	/**
	 * 分割されたリクエスト本文の一部を返す。
	 * - isRequestBodyPart()がtrueの場合のみ呼ばれる。
	 * - HttpEntity.getContentLength()が設定されている場合は合計の長さが合っていること。
	 * - HttpEntity.getContentLength()が設定されていない場合は Chunked で送信される。
	 * @param transpoter データの書き込み先
	 */
	public void onRequestBodyPart(BufferTranspoter transpoter);

	/**
	 * 分割されたレスポンス本文の一部を読み込み可能となったことの通知。
	 * - 分割位置は予測不能。極端な場合、1byteつづのこともある。
	 * @param transpoter データの読み込み先
	 */
	public void onResponseBodyPart(BufferTranspoter transpoter);

}
