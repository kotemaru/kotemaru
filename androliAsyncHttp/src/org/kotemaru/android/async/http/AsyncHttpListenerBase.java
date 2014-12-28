package org.kotemaru.android.async.http;

import java.nio.ByteBuffer;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import android.util.Log;

public abstract class AsyncHttpListenerBase implements AsyncHttpListener {
	private static final String TAG = AsyncHttpListenerBase.class.getSimpleName();

	@Override
	public void onConnect(HttpRequest httpRequest) {
		Log.v(TAG, "onConnect");
	}

	@Override
	public void onRequestHeader(HttpRequest httpRequest) {
		Log.v(TAG, "onRequestHeader");
	}

	@Override
	public void onRequestBody(HttpRequest httpRequest) {
		Log.v(TAG, "onResponseBody");
	}

	@Override
	public void onResponseHeader(HttpResponse httpResponse) {
		Log.v(TAG, "onResponseHeader");
	}

	@Override
	public void onResponseBodyPart(byte[] buffer, int offset, int length) {
		Log.v(TAG, "onResponseBodyPart");
	}

	@Override
	public abstract void onResponseBody(HttpResponse httpResponse);

	@Override
	public void onError(Throwable t) {
		Log.e(TAG, "AsyncHttp failed:" + t.getMessage(), t);
	}

	@Override
	public void onAbort() {
		Log.v(TAG, "onAbort");
	}
	
	@Override
	public boolean isRequestBodyPart() {
		return false;
	}

	@Override
	public boolean isResponseBodyPart() {
		return false;
	}

	@Override
	public ByteBuffer onRequestBodyPart(ByteBuffer buffer) {
		return null;
	}
}
