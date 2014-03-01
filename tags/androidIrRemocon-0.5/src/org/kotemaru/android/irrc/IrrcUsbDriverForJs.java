package org.kotemaru.android.irrc;

import org.kotemaru.android.irrc.IrrcUsbDriver.IrrcResponseListener;
import org.kotemaru.android.irrc.IrrcUsbDriver.RequestAsyncTask;

import android.webkit.JavascriptInterface;

/**
 * 「赤外線リモコンキット」のドライバ。
 * - 注：このクラスの AsyncTask は Activity のライフサイクルと同期しません。
 * Activity を持たないように。
 * 
 * @author kotemaru@kotemaru.org
 */
public class IrrcUsbDriverForJs {
	// private static final String TAG = "IrrcUsbDriverForJs";
	private IrrcUsbDriver irrcUsbDriver;
	private WebViewFragment fragment;
	private Bytes irData = new Bytes();

	public IrrcUsbDriverForJs(WebViewFragment fragment, IrrcUsbDriver irrcUsbDriver) {
		this.fragment = fragment;
		this.irrcUsbDriver = irrcUsbDriver;
	}

	/**
	 * @return true=デバイスの確認。
	 */
	@JavascriptInterface
	public boolean hasDevice() {
		return irrcUsbDriver.hasDevice();
	}

	/**
	 * @return true=デバイスの準備OK。
	 */
	@JavascriptInterface
	public boolean isReady() {
		return irrcUsbDriver.isReady();
	}

	/**
	 * リモコンの赤外線受信開始。
	 * 
	 * @param listener 応答リスナ。
	 */
	@JavascriptInterface
	public RequestAsyncTask startReceiveIr() {
		IrrcResponseListener listener = new IrrcResponseListener() {
			@Override
			public void onIrrcResponse(byte[] data) {
				fragment.callbackJs("onStartReceiveIr(" + (data != null) + ")");
			}
		};
		return irrcUsbDriver.startReceiveIr(listener);
	}

	/**
	 * リモコンの赤外線受信終了。
	 * 
	 * @param listener 応答リスナ。
	 */
	@JavascriptInterface
	public RequestAsyncTask endReceiveIr() {
		IrrcResponseListener listener = new IrrcResponseListener() {
			@Override
			public void onIrrcResponse(byte[] data) {
				fragment.callbackJs("onEndReceiveIr(" + (data != null) + ")");
			}
		};
		return irrcUsbDriver.endReceiveIr(listener);
	}

	/**
	 * リモコンの赤外線受信データ取得。
	 * - データが取れるまで戻らない。
	 * 
	 * @param listener 応答リスナ。
	 */
	@JavascriptInterface
	public RequestAsyncTask getReceiveIrData(long timeout) {
		IrrcResponseListener listener = new IrrcResponseListener() {
			@Override
			public void onIrrcResponse(byte[] data) {
				irData.setBytes(data);
				fragment.callbackJs("onGetReceiveIrData(" + (data != null) + ")");
			}
		};
		return irrcUsbDriver.getReceiveIrData(listener, timeout);
	}

	@JavascriptInterface
	public Bytes getIrData() {
		if (irData.getBytes() == null) return null;
		return irData;
	}

	/**
	 * 赤外線データ送信。
	 * 
	 * @param buff データ
	 */
	@JavascriptInterface
	public RequestAsyncTask sendData(Bytes data) {
		byte[] buff = data.getBytes();
		if (buff == null) return null;
		return irrcUsbDriver.sendData(buff);
	}

}