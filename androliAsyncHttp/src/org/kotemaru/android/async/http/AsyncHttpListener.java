package org.kotemaru.android.async.http;

import java.nio.ByteBuffer;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpResponse;

public interface AsyncHttpListener {
	public void onConnect();
	public void onRequest();
	public void onResponseWait();
	public void onResponseHeader(BasicHttpResponse httpResponse);
	public void onResponseBody(HttpResponse httpResponse);
	public void onError(Throwable t);
	public void onAbort();

	public boolean isRequestBodyPart();
	public boolean isResponseBodyPart();
	public ByteBuffer onRequestBodyPart(ByteBuffer buffer);
	public void onResponseBodyPart(byte[] buffer, int offset, int length);
}
