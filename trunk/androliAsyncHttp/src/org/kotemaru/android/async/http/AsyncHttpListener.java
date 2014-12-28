package org.kotemaru.android.async.http;

import java.nio.ByteBuffer;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

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
	 * リクエストボディーが送信された。（ボディがある場合のみ）
	 */
	public void onRequestBody(HttpRequest httpRequest);

	
	public void onResponseHeader(HttpResponse httpResponse);
	public void onResponseBody(HttpResponse httpResponse);
	public void onError(Throwable t);
	public void onAbort();

	public boolean isRequestBodyPart();
	public boolean isResponseBodyPart();
	public ByteBuffer onRequestBodyPart(ByteBuffer buffer);
	public void onResponseBodyPart(byte[] buffer, int offset, int length);
}
