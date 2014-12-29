package org.kotemaru.android.async.http;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.kotemaru.android.async.BufferTransferConsumer;

import android.util.Log;

public abstract class AsyncHttpListenerBase implements AsyncHttpListener {
	private static final String TAG = AsyncHttpListenerBase.class.getSimpleName();
	private static final boolean IS_DEBUG = BuildConfig.DEBUG;

	@Override
	public void onConnect(HttpRequest httpRequest) {
		if (IS_DEBUG) Log.v(TAG, "onConnect");
	}

	@Override
	public void onRequestHeader(HttpRequest httpRequest) {
		if (IS_DEBUG) Log.v(TAG, "onRequestHeader");
	}

	@Override
	public void onRequestBody(HttpRequest httpRequest) {
		if (IS_DEBUG) Log.v(TAG, "onRequestBody");
	}

	@Override
	public void onResponseHeader(HttpResponse httpResponse) {
		if (IS_DEBUG) Log.v(TAG, "onResponseHeader");
	}

	@Override
	public void onResponseBodyPart(BufferTransferConsumer consumer) {
		if (IS_DEBUG) Log.v(TAG, "onResponseBodyPart");
	}

	@Override
	public abstract void onResponseBody(HttpResponse httpResponse);

	@Override
	public void onError(String msg, Throwable t) {
		Log.e(TAG, "AsyncHttp failed:" + msg, t);
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
	public void onRequestBodyPart(BufferTransferConsumer consumer) {
	}
}
