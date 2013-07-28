package org.kotemaru.sample.gcm.client;

import static org.kotemaru.sample.gcm.client.MainActivity.*;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * Push通知受け取りサービス。
 * @author @kotemaru.org
 */
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

	private Handler toaster;

	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		toaster = new Handler();
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "onRegistered: regId = " + registrationId);
		// GCMから発行された端末IDをアプリサーバに登録する。
		String uri = SERVER_URL + "?action=register"
				+ "&userId=" + USER_ID
				+ "&regId=" + registrationId;
		Util.doGet(uri);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		// アプリサーバから送信されたPushメッセージの受信。
		// Message.data が Intent.extra になるらしい。
		CharSequence msg = intent.getCharSequenceExtra("msg");
		Log.i(TAG, "onMessage: msg = " + msg);
		toast("Push message: " + msg);
	}

	
	
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "onUnregistered: regId = " + registrationId);
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			String uri = SERVER_URL + "?action=unregister"
					+ "&userId=" + USER_ID;
			Util.doGet(uri);
		} else {
			Log.i(TAG, "onUnregistered: ignore");
		}
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "onDeletedMessages total="+total);
		toast("onDeletedMessages: " + total);
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "onError: " + errorId);
		toast("onError: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Log.i(TAG, "onRecoverableError: " + errorId);
		toast("onRecoverableError: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	
	private void toast(final String msg) {
		toaster.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(GCMIntentService.this, msg, Toast.LENGTH_LONG).show();
			}
		});
	}

}
