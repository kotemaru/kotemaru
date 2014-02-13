/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2014- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.android.sample.webview;

import java.net.URI;
import java.net.URISyntaxException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * WebViewに拡張XMLHttpRequestを設定してクロスサイトするためのサンプル。
 * @author kotemaru@kotemaru.org
 */
public class SampleActivity extends Activity {
	public static final String TAG = "WebviewSample";

	public static final String ACTIVITY_ID = "ACTIVITY_ID";
	/**  Activityの継続性を維持するためのID */
	private String _activityId = null;
	private AccessControlList _accessControlList = new AccessControlList();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Activityの継続性を維持するためのID取得。@see #onSaveInstanceState()
		String aid = (savedInstanceState != null) ? savedInstanceState.getString(ACTIVITY_ID) : null;
		_activityId = (aid != null) ? aid : ("@" + this.hashCode());

		initAccessControlList(savedInstanceState);
		initWebView(savedInstanceState);
	}

	/**
	 * サンプルで使うホストのみアクセス許可の設定。
	 * @param savedInstanceState
	 */
	private void initAccessControlList(Bundle savedInstanceState) {
		try {
			_accessControlList.addAllowAccess("http://www.google.co.jp");
			_accessControlList.addAllowAccess("https://www.google.co.jp");
			_accessControlList.addAllowAccess("http://blog.kotemaru.org");
			_accessControlList.addAllowAccess("http://www.redmine.org");
			_accessControlList.addAllowAccess("http://192.168.0.*");
		} catch (URISyntaxException e) {
			Log.e(TAG,e.getMessage(),e);
		}
	}
	
	/**
	 * WebViewの各種設定。
	 * - 拡張XMLHttpRequestのファクトリをJavaScriptから使えるようにする。
	 * @param savedInstanceState
	 */
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	private void initWebView(Bundle savedInstanceState) {
		WebView webview = (WebView) findViewById(R.id.webview);


		// WebViewの基本リスナを設定。
		webview.setWebViewClient(new SampleWebViewClient());
		webview.setWebChromeClient(new LoggingWebChromeClient());

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//WebView.setWebContentsDebuggingEnabled(true);
		}

		// 初期ページ読み込み。
		webview.loadUrl("file:///android_asset/test.html");
		// JavaScript有効化（デフォルトは無効）
		webview.getSettings().setJavaScriptEnabled(true);
	}
	
	/**
	 * 拡張XMLHttpRequestのファクトリの取得。
	 * - 拡張XMLHttpRequestは非同期処理があるのでActivityのライフサイクルに従わない。
	 * - 従って、Applicationに保存して再利用する。
	 * @return 拡張XMLHttpRequestのファクトリ
	 */
	private XMLHttpRequestXSFactory getXMLHttpRequestXSFactory() {
		SampleApplication application = (SampleApplication) getApplication();
		String factorykey = "XMLHttpRequestXSFactory" + _activityId;
		XMLHttpRequestXSFactory factory = (XMLHttpRequestXSFactory) application.getObject(factorykey);
		Log.d(TAG, "XMLHttpRequestXSFactory="+factory);
		if (factory == null) {
			factory = new XMLHttpRequestXSFactory();
			application.putObject(factorykey, factory);
		}
		return factory;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Activityの再構築に備えて自身にIDを保存して置く。
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(ACTIVITY_ID, _activityId);
	}
	
	/**
	 * 拡張XMLHttpRequestのファクトリにWebViewが無効になっていることを通知する。
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		getXMLHttpRequestXSFactory().dispose();
	}

	/**
	 * WebViewのリスナー。
	 * - JavascriptInterfaceを登録する。
	 * - WebViewにアクセス制限をかける。
	 */
	private class SampleWebViewClient extends WebViewClient {
		@Override
		public void onPageStarted (WebView webview, String url, Bitmap favicon) {
			// 拡張XMLHttpRequestファクトリの初期化。
			XMLHttpRequestXSFactory factory = getXMLHttpRequestXSFactory();
			factory.setAccessControlList(_accessControlList);
			factory.setWebView(webview);
			webview.addJavascriptInterface(factory, "XMLHttpRequestXSFactory");
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			try {
				Log.d(TAG, url);
				URI uri = new URI(url);
				if (!_accessControlList.isAllow(uri)) {
					Log.w(TAG, "Access denied " + url);
					return false;
				}
			} catch (URISyntaxException e) {
				Log.w(TAG, e.getMessage());
				return false;
			}
			return true;
		}
	}
	/**
	 * WebViewのログをLogcatに転送するクライアント。
	 */
	private class LoggingWebChromeClient extends WebChromeClient {
		public boolean onConsoleMessage(ConsoleMessage cm) {
			Log.i(TAG, cm.sourceId() + "(" + cm.lineNumber() + ") " + cm.message());
			return true;
		}
	};
}
