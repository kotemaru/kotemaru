package org.kotemaru.android.fw.thread;

import android.os.Handler;
import android.os.Looper;

public class ExecutorHandler implements Executor {
	private final Handler mHandler;

	public ExecutorHandler(Looper looper) {
		mHandler = new Handler(looper);
	}

	public boolean post(Runnable runner, int delay) {
		if (delay == 0) {
			return mHandler.post(runner);
		} else {
			return mHandler.postDelayed(runner, delay);
		}
	}

}
