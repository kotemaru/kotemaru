/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2014- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.android.sample.webview;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;

/**
 * 拡張XMLHttpRequestの実装。
 * - クロスサイト制限の無いXMLHttpRequestのためのネイティブ側の実装。
 * - XMLHttpRequstXS.jsと連携して動作する。
 * - 注意事項：API Level 17 以降のOSで使用しないとセキュリティホールになります。
 */
public class XMLHttpRequestXS {
	private static final String TAG = "XMLHttpRequest";

	private static final String GET = "GET";
	private static final String POST = "POST";
	private static final String CONTENT_TYPE = "Content-type";
	private static final String TRIGGER_SCRIPT = "javascript:XMLHttpRequestXS._staticNativeCallback";

	private static final String MIME_TEXT = "text/plain";
	private static final String MIME_XML = "text/xml";

	private static final int UNSENT = 0; // open() がまだ呼び出されていない。
	private static final int OPENED = 1; // send() がまだ呼び出されていない。
	private static final int HEADERS_RECEIVED = 2; // send()
													// が呼び出され、ヘッダーとステータスが通った。
	private static final int LOADING = 3; // ダウンロード中；　responseText
											// は断片的なデータを保持している。
	private static final int DONE = 4; // 一連の動作が完了した。
	private static final int ERROR = 5; // 仕様に無いけど必要なので追加。

	private XMLHttpRequestXSFactory _factory;

	private HttpClient _client;
	private HttpRequestBase _request;
	private HttpResponse _response;
	private boolean _isAsync;
	private int _readyState;
	private String _triggerScript;

	private int _responseCode;
	private String _responseText = null;
	private Document _responseXML = null;
	private String _overrideMimeType = null;
	private String _responseType = null;
	private String _mimeType = null;
	private String _charset = null;

	public XMLHttpRequestXS(XMLHttpRequestXSFactory factory) {
		_factory = factory;
		_client = new DefaultHttpClient();
		_triggerScript = TRIGGER_SCRIPT + "(" + identityHashCode() + ")";
		setReadyState(UNSENT);
	}

	@JavascriptInterface
	public int identityHashCode() {
		return System.identityHashCode(this);
	}

	@JavascriptInterface
	public void open(String type, String url) throws Exception {
		open(type, url, false);
	}

	@JavascriptInterface
	public String open(String type, String url, boolean isAsync) throws Exception {
		Log.d(TAG,"open:"+type+" "+url);
		try {
			_isAsync = isAsync;
			if (GET.equalsIgnoreCase(type)) {
				_request = new HttpGet(url);
			} else {
				_request = new HttpPost(url);
			}
			_factory.checkDomain(_request.getURI());

			String cookie = CookieManager.getInstance().getCookie(url);
			_request.setHeader("Cookie", cookie);
			setReadyState(OPENED);
			return null;
		} catch (Throwable t) {
			Log.e(TAG, t.getMessage(), t);
			setReadyState(ERROR);
			return t.getMessage();
		}
	}

	@JavascriptInterface
	public void abort() throws Exception {
		if (_request != null) _request.abort();
	}

	@JavascriptInterface
	public void send(String body) throws Throwable {
		Log.d(TAG,"send:");
		if (_isAsync) {
			new RequestAsyncTask().execute(body);
		} else {
			doRequest(body);
			setReadyState(HEADERS_RECEIVED);
			setReadyState(LOADING);
			doResponse();
			setReadyState(DONE);
		}
	}

	private class RequestAsyncTask extends AsyncTask<String, Integer, Throwable> {
		@Override
		protected Throwable doInBackground(String... args) {
			try {
				doRequest(args[0]);
				publishProgress(HEADERS_RECEIVED);
				publishProgress(LOADING);
				doResponse();
				publishProgress(DONE);
			} catch (Throwable t) {
				return t;
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			setReadyState(values[0]);
			_factory.runScript(_triggerScript);
		}

		@Override
		protected void onPostExecute(Throwable error) {
			if (error != null) {
				setReadyState(ERROR);
				_factory.runScript(_triggerScript);
			}
		}
	}

	private void doRequest(String body) throws Throwable {
		try {
			if (POST.equals(_request.getMethod())) {
				HttpPost httpPost = (HttpPost) _request;
				Header header = httpPost.getFirstHeader(CONTENT_TYPE);
				String ctype = header != null ? header.getValue() : "plain/text; charset=" + HTTP.UTF_8;
				httpPost.setEntity(new StringEntity(body, ctype));
			}
			_response = _client.execute(_request);
			_responseCode = _response.getStatusLine().getStatusCode();

			CookieManager cookieManager = CookieManager.getInstance();
			Header[] cookies = _response.getHeaders("Set-Cookie");
			for (int i = 0; i < cookies.length; i++) {
				cookieManager.setCookie(_request.getURI().toString(), cookies[i].getValue());
			}

			if (Log.isLoggable(TAG, Log.DEBUG)) debugLog();
		} catch (Throwable t) {
			Log.e(TAG, t.getMessage(), t);
			_request.abort();
			throw t;
		}
	}

	private void doResponse() throws Throwable {
		try {
			HttpEntity entity = _response.getEntity();
			Header contentType = getOverrideContentType();
			if (contentType == null) {
				contentType = entity.getContentType();
				if (contentType == null) {
					contentType = new BasicHeader(CONTENT_TYPE, MIME_TEXT);
				}
			}
			HeaderElement[] elems = contentType.getElements();
			_mimeType = (elems.length > 0) ? elems[0].getName().toLowerCase(Locale.US) : "";
			_charset = HTTP.UTF_8; // default
			for (int i = 1; i < elems.length; i++) {
				if ("charset".equals(elems[i].getName().toLowerCase(Locale.US))) {
					_charset = elems[i].getValue();
				}
			}
			_responseText = getEntityString(entity, _charset);
		} catch (Throwable t) {
			Log.e(TAG, t.getMessage(), t);
			_responseCode = 500;
			throw t;
		} finally {
			_request.abort();
		}
	}

	private Header getOverrideContentType() {
		if (_overrideMimeType != null) {
			return new BasicHeader(CONTENT_TYPE, _overrideMimeType);
		}
		if ("document".equals(_responseType)) {
			return new BasicHeader(CONTENT_TYPE, MIME_XML);
		}
		return null;
	}

	private void setReadyState(int state) {
		_readyState = state;
	}

	@JavascriptInterface
	public String getResponseHeader(String name) {
		Header[] headers = _response.getHeaders(name);
		if (headers == null || headers.length == 0) return null;
		if (headers.length == 1) return headers[0].getValue();

		StringBuilder sbuf = new StringBuilder();
		for (int i = 0; i < headers.length; i++) {
			if (i > 0) sbuf.append(',');
			sbuf.append(headers[i].getValue());
		}
		return sbuf.toString();
	}

	@JavascriptInterface
	public String getAllResponseHeaders() {
		StringBuilder sbuf = new StringBuilder();
		Header[] headers = _response.getAllHeaders();
		for (int i = 0; i < headers.length; i++) {
			sbuf.append(headers[i].getName()).append(": ").append(headers[i].getValue());
		}
		return sbuf.toString();
	}

	private String getEntityString(HttpEntity entity, String charset) throws IllegalStateException, IOException {
		InputStream in = entity.getContent();
		try {
			Reader reader = new InputStreamReader(in, charset);
			StringBuilder sbuf = new StringBuilder();
			char[] buff = new char[1500];
			int n = 0;
			while ((n = reader.read(buff)) >= 0) {
				sbuf.append(buff, 0, n);
			}
			return sbuf.toString();
		} finally {
			in.close();
		}
	}

	@JavascriptInterface
	public int getReadyState() {
		return _readyState;
	}

	@JavascriptInterface
	public int getStatus() {
		return _responseCode;
	}

	@JavascriptInterface
	public String getStatusText() {
		if (_response == null) return null;
		return _response.getStatusLine().getReasonPhrase();
	}

	@JavascriptInterface
	public String getResponseText() throws IllegalStateException, IOException {
		return _responseText;
	}

	@JavascriptInterface
	public Document getResponseXML() throws IllegalStateException, SAXException, IOException, ParserConfigurationException {
		return _responseXML;
	}

	@JavascriptInterface
	public void setRequestHeader(String name, String value) {
		_request.setHeader(name, value);
	}

	@JavascriptInterface
	public void overrideMimeType(String mimeType) {
		this._overrideMimeType = mimeType;
	}

	@JavascriptInterface
	public String getResponseType() {
		return _responseType;
	}

	@JavascriptInterface
	public void setResponseType(String type) {
		this._responseType = type;
	}

	@JavascriptInterface
	public String getMimeType() {
		return _mimeType;
	}

	@JavascriptInterface
	public String getCharset() {
		return _charset;
	}

	private void debugLog() throws Exception {
		String msg = "XREQ request detail.\n";
		msg += ">> " + _request.getMethod() + " " + _request.getURI() + "\n";
		Header[] reqhs = _request.getAllHeaders();
		for (int i = 0; i < reqhs.length; i++) {
			msg += ">> " + reqhs[i] + "\n";
		}

		msg += "\n<< " + _response.getStatusLine() + "\n";
		Header[] reshs = _response.getAllHeaders();
		for (int i = 0; i < reshs.length; i++) {
			msg += "<< " + reshs[i] + "\n";
		}

		Log.d(TAG, msg);
	}

}
