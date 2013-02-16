package org.kotemaru.android.asyncrotate;

import android.app.Application;

public class AsyncHelperApplication extends Application {
	private ActivityManager activityManager = new ActivityManager();

	@Override
	public void onCreate() {
		// API14 からサポートのActivityのライフサイクルのコールバック設定。
		registerActivityLifecycleCallbacks(activityManager);
	}

	@Override
	public void onTerminate() {
	}

	public ActivityManager getActivityManager() {
		return activityManager;
	}
}
