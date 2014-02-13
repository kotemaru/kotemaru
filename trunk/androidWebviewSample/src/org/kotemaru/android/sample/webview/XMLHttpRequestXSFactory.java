/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2014- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.android.sample.webview;

import java.io.IOException;
import java.net.URI;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * 拡張XMLHttpRequestのファクトリ。
 * - addJavascriptInterface()で登録するオブジェクト。
 * - XMLHttpRequstXS.jsと連携して動作する。
 * - 注意事項：API Level 17 以降のOSで使用しないとセキュリティホールになります。
 */
public class XMLHttpRequestXSFactory {
	 
	private WebView _webview;
	private AccessControlList _accessControlList;
	
	public XMLHttpRequestXSFactory() {
	}
	
	@JavascriptInterface
	public XMLHttpRequestXS getXMLHttpRequestXS() {
		return new XMLHttpRequestXS(this);
	}

	/**
	 * Activityが終了した時に呼ばれる。
	 */
	public void dispose(){
		_webview = null;
		_accessControlList = null;
	}
	
	/**
	 * WenViewでスクリプトを実行する。
	 * - UIスレッドから呼ばいないとエラーとなる。
	 * @param script "javascript:"で始まるjavascript
	 */
	public void runScript(String script) {
		if (_webview != null) {
			_webview.loadUrl(script);
		}
	}

	public void checkDomain(URI uri) throws IOException {
		if (_accessControlList == null) {
			throw new IOException("Disposed.");
		}
		_accessControlList.check(uri);
	}

	public WebView getWebView() {
		return _webview;
	}
	public void setWebView(WebView webview) {
		_webview = webview;
	}
	public AccessControlList getaccessControlList() {
		return _accessControlList;
	}
	public void setAccessControlList(AccessControlList accessControlList) {
		_accessControlList = accessControlList;
	}
}
