package org.kotemaru.android.asyncrotate;

import java.util.HashMap;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;


/**
 * Activityの管理クラス。
 * <li>Activityが destroy/create されても同一IDで継続的にアクセスできる。
 * <li>インスタンスをApplication.registerActivityLifecycleCallbacks()に設定する事。
 * <li>Bundle のキー "___ACTIVITY_ID___" を汚染する。
 * <li>使用例：<xmp>
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
	}

	@Override
	protected void onPostExecute(String result) {
		// Activityは保存しておいたIDから取得する。
		Activity activity = activityManager.getActivity(activityId);
		TextView message = (TextView) activity.findViewById(R.id.message);
		message.setText(result);
	}
}
</xmp>

 * @author info@kotemaru.org
 */
public class ActivityManager implements ActivityLifecycleCallbacks {
	public final String ACTIVITY_ID = "___ACTIVITY_ID___";
	
	/** Application内で一意のActivityのIDカウンタ */
	private Integer nextActivityId = 0;
	
	// マップ
	private HashMap<String,Activity> aid2activity = new HashMap<String,Activity>();
	private HashMap<Activity,String> activity2aid = new HashMap<Activity,String>();

	
	/**
	 * ActivityからIDの取得。
	 * <li>すでにIDを持っている場合はそれを返す。
	 * <li>IDを持っていない場合はマップに新規登録して返す。
	 * @param activity
	 * @return Application内で一意のID
	 */
	public synchronized String getActivityId(Activity activity) {
		String aid = activity2aid.get(activity);
		if (aid == null) {
			aid = (nextActivityId++).toString();
			aid2activity.put(aid, activity);
			activity2aid.put(activity, aid);
		}
		return aid;
	}
	/**
	 * IDからActivityの取得。
	 * <li>登録されているIDからActivityを引いて返す。
	 * <li>Activity が destroy/create されていてると更新されている。
	 * @param aid ActivityのID
	 * @return Activity。未登録の場合はnull。
	 */
	public synchronized Activity getActivity(String aid) {
		return aid2activity.get(aid);
	}
	
	
	/**
	 * Activity.onCreate()のハンドリング。
	 * <li>Bundleに ___ACTIVITY_ID___ を持っていればマップを更新。
	 */
	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		if (savedInstanceState == null) return; // First
		String aid = savedInstanceState.getString(ACTIVITY_ID);
		if (aid == null) return; // Not managed.
		
		synchronized (this) {
			aid2activity.put(aid, activity);
			activity2aid.put(activity, aid);
		}
	}
	
	/**
	 * Activity.onSaveInstanceState()のハンドリング。
	 * <li>___ACTIVITY_ID___ にActivityのIDを保存。
	 */
	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
		String aid = activity2aid.get(activity);
		outState.putString(ACTIVITY_ID, aid);
	}

	/**
	 * Activity.onDestroy()のハンドリング
	 * <li>マップからActivityを削除。
	 * <li>Activityインスタンス開放の為、必須。
	 */
	@Override
	public synchronized void onActivityDestroyed(Activity activity) {
		String aid = activity2aid.get(activity);
		if (aid == null) return; // Not managed activity.
		aid2activity.put(aid, null);
		activity2aid.remove(activity);
	}
	
	 
	@Override
	public void onActivityStarted(Activity activity) {
	}
	@Override
	public void onActivityResumed(Activity activity) {
	}
	@Override
	public void onActivityStopped(Activity activity) {
	}
	@Override
	public void onActivityPaused(Activity activity) {
	}

}
