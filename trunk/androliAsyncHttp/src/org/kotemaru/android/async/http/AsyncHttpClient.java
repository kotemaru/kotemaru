package org.kotemaru.android.async.http;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BrowserCompatSpec;

import android.util.Log;

public class AsyncHttpClient {
	private static final String TAG = AsyncHttpRequest.class.getSimpleName();

	private CookieSpec mCookieSpec = new BrowserCompatSpec();
	private CookieStore mCookieStore = new BasicCookieStore();

	public void execute(AsyncHttpRequest request, AsyncHttpListener listener) {
		request.setAsyncHttpListener(listener);
		try {
			request.execute(this);
		} catch (IOException e) {
			listener.onError(e);
		}
	}

	public List<Cookie> getCookies(URI uri) {
		return HttpUtil.getCookies(mCookieSpec, mCookieStore, uri);
	}
	public void setCookies(HttpMessage response, URI uri) {
		Header[] headers = response.getHeaders(HttpUtil.SET_COOKIE);
		for (Header header : headers) {
			try {
				HttpUtil.setCookie(mCookieSpec, mCookieStore, header, uri);
			} catch (MalformedCookieException e) {
				Log.w(TAG,"Ignore Set-Cookie:"+e);
			}
		}
	}

}
