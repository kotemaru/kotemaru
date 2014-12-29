package org.kotemaru.android.async.http;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicRequestLine;

/**
 * GETリクエスト。
 * - 再利用可能。
 * @author kotemaru.org
 */
public class AsyncHttpPost extends AsyncHttpRequest {
	private HttpEntity mHttpEntity;

	public AsyncHttpPost() {
	}
	public AsyncHttpPost(URI uri) {
		super(uri);
	}
	public AsyncHttpPost(String uri) throws URISyntaxException {
		super(new URI(uri));
	}
	/**
	 * リクエスト本文を設定。
	 * - 分割を使い場合は content はnullで良い。
	 * @see AsyncHttpListener#isRequestBodyPart()
	 * @param httpEntity リクエスト本文
	 */
	public void setHttpEntity(HttpEntity httpEntity) {
		mHttpEntity = httpEntity;
	}

	@Override
	public HttpEntity getHttpEntity() {
		return mHttpEntity;
	}

	@Override
	public MethodType getMethodType() {
		return MethodType.POST;
	}
	@Override
	public RequestLine getRequestLine() {
		return new BasicRequestLine(MethodType.POST.name(), getURI().toASCIIString(), HttpUtil.PROTOCOL_VERSION);
	}

}
