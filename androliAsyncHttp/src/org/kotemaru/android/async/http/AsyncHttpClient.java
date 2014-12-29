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

/**
 * 非同期　HttpClient　実装。prg.apacheとの互換性は無い。
 * - クッキーはサポート。
 * @author kotemaru.org
 */
public class AsyncHttpClient {
	private static final String TAG = AsyncHttpRequest.class.getSimpleName();

	private CookieSpec mCookieSpec = new BrowserCompatSpec();
	private CookieStore mCookieStore = new BasicCookieStore();

	/**
	 * HTTPリスエストの実行。
	 * - 処理はすぐに終わる。
	 * - 通信は発生しないのでUIスレッドで実行可能。
	 * - 結果は listener に返される。
	 * @param request    HTTPリクエスト。GET or POST。
	 * @param listener   非同期リスナ。
	 */
	public void execute(AsyncHttpRequest request, AsyncHttpListener listener) {
		try {
			request.execute(this, listener);
		} catch (IOException e) {
			listener.onError("execute fail", e);
		}
	}

	protected List<Cookie> getCookies(URI uri) {
		return HttpUtil.getCookies(mCookieSpec, mCookieStore, uri);
	}
	protected void setCookies(HttpMessage response, URI uri) {
		Header[] headers = response.getHeaders(HttpUtil.SET_COOKIE);
		for (Header header : headers) {
			try {
				HttpUtil.setCookie(mCookieSpec, mCookieStore, header, uri);
			} catch (MalformedCookieException e) {
				Log.w(TAG,"Ignore Set-Cookie:"+e);
			}
		}
	}

	
	//---------------------------------------------------------------------------
	// Setter/Getter
	public CookieSpec getCookieSpec() {
		return mCookieSpec;
	}
	public void setCookieSpec(CookieSpec cookieSpec) {
		mCookieSpec = cookieSpec;
	}
	public CookieStore getCookieStore() {
		return mCookieStore;
	}
	public void setCookieStore(CookieStore cookieStore) {
		mCookieStore = cookieStore;
	}

}
