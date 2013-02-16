package org.kotemaru.android.asyncrotate;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

public class SlowAsyncTask extends AsyncTask<String, Void, String> {
	private ActivityManager activityManager; 
	private String activityId;
	
	public SlowAsyncTask(Activity activity) {
		activityManager = ((AsyncHelperApplication)activity.getApplication()).getActivityManager();
		// Activity の ID を取得して保存。
		activityId = activityManager.getActivityId(activity);
	}
	
	@Override
	protected String doInBackground(String... params) {
		// 時間のかかる非同期処理。
		try {Thread.sleep(3000);} catch (Exception e) { }
		return params[0];
	}

	@Override
	protected void onPostExecute(String result) {
		// Activityは保存しておいたIDから取得する。
		Activity activity = activityManager.getActivity(activityId);
		TextView message = (TextView) activity.findViewById(R.id.message);
		message.setText(result);
	}
}
