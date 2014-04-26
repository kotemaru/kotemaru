package org.kotemaru.android.nnalert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

/**
 * Push通知受け取りサービス。
 * 
 * @author @kotemaru.org
 */
public class GCMIntentService extends GCMBaseIntentService {
	private static final String TAG = MainActivity.class.getSimpleName();

	public static final String SENDER_ID = "864593998398";

	private NotificationManager notificationManager;

	public GCMIntentService() {
		super(SENDER_ID);
		Log.i(TAG, "GCMIntentService:");
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate:");
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "onRegistered: regId = " + registrationId);
		Util.transition(context, RegisterActivity.class);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		CharSequence messageType = intent.getCharSequenceExtra("messageType");
		Log.i(TAG, "onMessage: msg = " + messageType);
		if ("onRegistered".equals(messageType)) {
			CharSequence mail = intent.getCharSequenceExtra("mail");
			Notification notice = new Notification.Builder(this)
					.setContentTitle(context.getString(R.string.on_registered))
					.setContentText(String.format(context.getString(R.string.on_registered_sub), mail))
					.setSmallIcon(R.drawable.niconama_alert_trim)
					.setDefaults(Notification.DEFAULT_ALL)
					.build();
			notificationManager.notify(0, notice);
			
		} else if ("onLive".equals(messageType)) {
			CharSequence liveId = intent.getCharSequenceExtra("liveId");
			CharSequence title = intent.getCharSequenceExtra("title");

			Uri uri = Uri.parse("http://sp.live.nicovideo.jp/watch/lv" + liveId);
			Intent action = new Intent(Intent.ACTION_VIEW, uri);
			PendingIntent pi = PendingIntent.getActivity(this, 0, action, Intent.FLAG_ACTIVITY_NEW_TASK);

			Notification n = new Notification.Builder(this)
					.setContentTitle(intent.getCharSequenceExtra("community"))
					.setContentText(String.format(context.getString(R.string.on_live_sub), title))
					.setSmallIcon(R.drawable.niconama_alert_trim)
					.setDefaults(Notification.DEFAULT_ALL)
					.setContentIntent(pi)
					.build();
			notificationManager.notify(Integer.parseInt(liveId.toString()), n);
		} else {
			Log.w(TAG,"Unknown message "+messageType);
		}
	}

	

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "onUnregistered: regId = " + registrationId);
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "onDeletedMessages total=" + total);
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.e(TAG, "onError: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Log.i(TAG, "onRecoverableError: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

}