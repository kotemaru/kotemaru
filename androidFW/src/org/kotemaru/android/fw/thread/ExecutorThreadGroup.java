package org.kotemaru.android.fw.thread;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ExecutorThreadGroup implements Executor {
	private final String mName;
	private final ThreadFactory mThreadFactory;
	private final ScheduledThreadPoolExecutor mThreadPool;

	public ExecutorThreadGroup(String name, int size, final int priority) {
		mName = name;
		mThreadFactory = new ThreadFactory() {
			private int mCount = 0;

			@Override
			public Thread newThread(Runnable r) {
				Thread th = new Thread(r, mName + "." + (mCount++));
				th.setPriority(priority);
				return th;
			}
		};
		mThreadPool = new ScheduledThreadPoolExecutor(size, mThreadFactory);
	}

	public String getName() {
		return mName;
	}
	//public void setPoolSize(int size) {
	//	mThreadPool.setMaximumPoolSize(size);
	//}

	public boolean post(Runnable runner, int delay) {
		try {
			if (delay == 0) {
				mThreadPool.execute(runner);
			} else {
				mThreadPool.schedule(runner, delay, TimeUnit.MILLISECONDS);
			}
			return true;
		} catch (RejectedExecutionException e) {
			return false;
		}
	}

}
