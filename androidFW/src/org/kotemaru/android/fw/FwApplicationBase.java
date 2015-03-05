package org.kotemaru.android.fw;

import java.util.ArrayList;
import java.util.List;

import org.kotemaru.android.fw.thread.ThreadManager;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

public abstract class FwApplicationBase<M, V extends FwActivity, C extends FwController> 
	extends Application 
	implements FwApplicationContext 
{
	private static final String TAG = FwApplicationBase.class.getSimpleName();

	protected ThreadManager mThreadManager;
	protected M mModel;
	protected V mCurrentActivity;
	protected C mController;

	protected List<V> mActivityStack = new ArrayList<V>(10);
	protected Runnable mUpdateRunner = new Runnable() {
		@Override
		public void run() {
			if (mCurrentActivity != null) mCurrentActivity.update();
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mThreadManager = createThreadManager();
		mModel = createModel();
		mController = createController();
		registerActivityLifecycleCallbacks(mActivityMonitor);
	}
	public abstract ThreadManager createThreadManager();
	public abstract M createModel();
	public abstract C createController();
	
	public ThreadManager getThreadManager() {
		return mThreadManager;
	}
	
	public M getModel() {
		return mModel;
	}
	public C getController() {
		return mController;
	}
	public List<V> getActivityStack() {
		return mActivityStack;
	}
	public void goBackActivity(Class<?> activityClass) {
		for (int i=mActivityStack.size()-1; i>0; i--) {
			V activity = mActivityStack.get(i);
			if (!activity.isFinishing()) {
				if (activity.getClass().equals(activityClass)) break;
				activity.finish();
			}
		}
	}
	
	public void updateCurrentActivity() {
		if (mCurrentActivity != null) mThreadManager.post(ThreadManager.UI, mUpdateRunner, 0);
	}

	@SuppressWarnings("unchecked")
	private V toGenericsActivity(Activity activity) {
		try {
			return (V) activity;
		} catch (ClassCastException e) {
			Log.e(TAG, "Not management activity " + activity.getClass().getCanonicalName(), e);
			throw e;
		}
	}

	ActivityLifecycleCallbacks mActivityMonitor = new ActivityLifecycleCallbacks() {
		@Override
		public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
			mActivityStack.add(toGenericsActivity(activity));
		}
		@Override
		public void onActivityResumed(Activity activity) {
			mCurrentActivity = toGenericsActivity(activity);
			mActivityStack.remove(mCurrentActivity);
			mActivityStack.add(mCurrentActivity);
		}
		@Override
		public void onActivityPaused(Activity activity) {
			if (mCurrentActivity == activity) mCurrentActivity = null;
		}
		@Override
		public void onActivityDestroyed(Activity activity) {
			mActivityStack.remove(activity);
		}

		// @formatter:off
		@Override public void onActivityStarted(Activity activity) {}
		@Override public void onActivityStopped(Activity activity) {}
		@Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
		// @formatter:on
	};

}
