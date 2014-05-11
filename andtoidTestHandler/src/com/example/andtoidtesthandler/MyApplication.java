package com.example.andtoidtesthandler;

import java.io.Serializable;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class MyApplication extends Application {
	
	private static final int WHAT_MODEL = 0;
	private Handler uiHandler;
	private HandlerThread asyncHandlerThread;
	private Handler asyncHandler;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("Test","onCreate");
		uiHandler = new Handler();
		asyncHandlerThread = new HandlerThread("async");
		asyncHandlerThread.start();
		asyncHandler = new Handler(asyncHandlerThread.getLooper());
	}
	
	@Override
	public void onConfigurationChanged (Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.e("Test","onConfigurationChanged");
	}
	@Override
	public void onTerminate ()  {
		super.onTerminate();
		Log.e("Test","onTerminate");
	}

	public void sendUI(Serializable model) {
		uiHandler.sendMessage(uiHandler.obtainMessage(WHAT_MODEL, 0,0,model));
	}
	public void sendAsync(Serializable model) {
		asyncHandler.sendMessage(asyncHandler.obtainMessage(WHAT_MODEL, 0,0,model));
	}
	
	public void postUi(Runnable run) {
		uiHandler.post(run);
	}
	public void postAsync(Runnable run) {
		asyncHandler.post(run);
	}
	
	
}
