package org.kotemaru.android.async.http;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicRequestLine;
import org.kotemaru.android.async.http.HttpUtil.MethodType;

public class AsyncHttpPost extends AsyncHttpRequest {
	private HttpEntity mHttpEntity;

	public AsyncHttpPost(URI uri) {
		super(uri);
	}
	public AsyncHttpPost(String uri) throws URISyntaxException {
		super(new URI(uri));
	}

	@Override
	public MethodType getMethodType() {
		return MethodType.POST;
	}
	@Override
	public RequestLine getRequestLine() {
		return new BasicRequestLine(MethodType.POST.name(), getURI().toASCIIString(), HttpUtil.PROTOCOL_VERSION);
	}
	
	@Override
	public HttpEntity getHttpEntity() {
		return mHttpEntity;
	}
	public void setHttpEntity(HttpEntity httpEntity) {
		mHttpEntity = httpEntity;
	}
	
}
