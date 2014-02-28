package org.kotemaru.android.irrc;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class WebViewFragment extends Fragment {
	private static final String TAG = "WebViewFragment";

	private String url;
	private WebView webview;
	private IrrcUsbDriverForJs irrcUsbDriverForJs;

	public WebViewFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Log.i(TAG, "Fragment.onCreate();" + this);
		this.setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		//Log.i(TAG, "Fragment.onCreateView();" + this+":"+webview);
		IrrcUsbDriver irrcUsbDriver = ((RemoconApplication) getActivity().getApplication()).getIrrcUsbDriver(getActivity());
		irrcUsbDriverForJs = new IrrcUsbDriverForJs(this, irrcUsbDriver);

		webview = (WebView) inflater.inflate(R.layout.webview, null);
		// webview.setWebViewClient(new MyWebViewClient());
		webview.setWebChromeClient(new LoggingWebChromeClient());
		return webview;
	}

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	@Override
	public void onResume() {
		super.onResume();
		//Log.i(TAG, "Fragment.onResume();" + this);

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

	public void onSelected() {
		getActivity().setTitle(webview.getTitle());
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

	/*
	 * private class MyWebViewClient extends WebViewClient {
	 * 
	 * @Override
	 * public void onPageStarted (WebView webview, String url, Bitmap favicon) {
	 * }
	 * 
	 * @Override
	 * public void onReceivedError (WebView view, int errorCode, String description, String failingUrl) {
	 * ((RemoconActivity) getActivity()).errorDialog(description);
	 * }
	 * }
	 */

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
			if (WebViewFragment.this == ((RemoconActivity) getActivity()).getCurrentWebViewFragment()) {
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
		return ((RemoconActivity) getActivity()).getOptions();
	}

	@JavascriptInterface
	public IrrcUsbDriverForJs getIrrcUsbDriver() {
		return irrcUsbDriverForJs;
	}

	@JavascriptInterface
	public IrDataDao getIrDataDao() {
		return ((RemoconActivity) getActivity()).getIrDataDao();
	}

}