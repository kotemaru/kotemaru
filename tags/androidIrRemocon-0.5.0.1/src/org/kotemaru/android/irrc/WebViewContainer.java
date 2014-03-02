package org.kotemaru.android.irrc;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;


public class WebViewContainer {
	private static final String TAG = "WebViewContainer";

	private RemoconActivity activity;
	private String url;
	private WebView webview;
	private IrrcUsbDriverForJs irrcUsbDriverForJs;

	public WebViewContainer(RemoconActivity activity) {
		this.activity = activity;
		
		IrrcUsbDriver irrcUsbDriver = ((RemoconApplication) activity.getApplication()).getIrrcUsbDriver(activity);
		irrcUsbDriverForJs = new IrrcUsbDriverForJs(this, irrcUsbDriver);
		webview = (WebView) activity.getLayoutInflater().inflate(R.layout.webview, null);
		// webview.setWebViewClient(new MyWebViewClient());
		webview.setWebChromeClient(new LoggingWebChromeClient());
	}
	
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	public void load(boolean isForce) {
		if (url.equals(webview.getUrl()) && !isForce) {
			return; // not reload.
		}
		
		// JS変数に NativeFactory に this を設定。
		webview.addJavascriptInterface(this, "NativeFactory");
		// 初期ページ読み込み。
		webview.loadUrl(url);
		// JavaScript有効化（デフォルトは無効）
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setAllowFileAccessFromFileURLs(true);

		// Chrome remode debug enable.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			WebView.setWebContentsDebuggingEnabled(true);
		}
	}
	
	public void onDestroy() {
		Log.i(TAG, "Fragment.onDestroy();" + this);
		webview.removeAllViews();
		webview.destroy();
	}

	public void onSelected() {
		activity.setTitle(webview.getTitle());
	}

	public WebView getWebview() {
		return webview;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * WebViewのログをLogcatに転送するクライアント。
	 */
	private class LoggingWebChromeClient extends WebChromeClient {
		public boolean onConsoleMessage(ConsoleMessage cm) {
			Log.i(TAG, cm.sourceId() + "(" + cm.lineNumber() + ") " + cm.message());
			return true;
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			if (WebViewContainer.this == activity.getCurrentWebViewFragment()) {
				onSelected();
			}
		}

	}

	// ---------------------------------------------------------
	// for JavaScript

	public void callbackJs(String callString) {
		webview.loadUrl("javascript:Native." + callString);
	}

	@JavascriptInterface
	public Options getOptions() {
		return activity.getOptions();
	}

	@JavascriptInterface
	public IrrcUsbDriverForJs getIrrcUsbDriver() {
		return irrcUsbDriverForJs;
	}

	@JavascriptInterface
	public IrDataDao getIrDataDao() {
		return activity.getIrDataDao();
	}

}