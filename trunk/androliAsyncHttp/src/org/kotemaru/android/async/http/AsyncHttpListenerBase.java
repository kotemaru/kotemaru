package org.kotemaru.android.async.http;

import java.nio.ByteBuffer;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpResponse;

import android.util.Log;

public abstract class AsyncHttpListenerBase implements AsyncHttpListener {
	private static final String TAG = AsyncHttpListenerBase.class.getSimpleName();

	@Override
	public void onConnect() {
		Log.v(TAG, "onConnect");
	}

	@Override
	public void onRequest() {
		Log.v(TAG, "onRequest");
	}

	@Override
	public void onResponseWait() {
		Log.v(TAG, "onResponseWait");
	}

	@Override
	public void onResponseHeader(BasicHttpResponse httpResponse) {
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
