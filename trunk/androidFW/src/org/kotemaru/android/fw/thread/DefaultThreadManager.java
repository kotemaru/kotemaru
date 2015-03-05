package org.kotemaru.android.fw.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

import android.os.Looper;

public class DefaultThreadManager implements ThreadManager {

	protected Executor mUiExecutor = new ExecutorHandler(Looper.getMainLooper());
	protected Map<String, Executor> mExecutorMap = new HashMap<String, Executor>();

	protected static DefaultThreadManager sInstance = null;

	// Not MT-safe
	public static DefaultThreadManager getInstance() {
		if (sInstance == null) {
			sInstance = new DefaultThreadManager();
		}
		return sInstance;
	}
	
	public DefaultThreadManager standardInitialize() {
		registerThread(WORKER, 1, Thread.NORM_PRIORITY);
		registerThread(NETWORK, 4, Thread.MIN_PRIORITY);
		return this;
	}

	@Override
	public synchronized Executor registerThread(String threadName) {
		return registerThread(threadName, 1, Thread.NORM_PRIORITY);
	}
	@Override
	public synchronized Executor registerThread(String threadName, int size, final int priority) {
		Executor executor = mExecutorMap.get(threadName);
		if (executor == null) {
			executor = new ExecutorThreadGroup(threadName, size, priority);
			mExecutorMap.put(threadName, executor);
		}
		return executor;
	}

	@Override
	public Executor getExecutor(String threadName) {
		if (UI.equals(threadName)) return mUiExecutor;
		synchronized (this) {
			return mExecutorMap.get(threadName);
		}
	}
	@Override
	public boolean post(String threadName, Runnable runner, int delay) {
		final Executor executor = getExecutor(threadName);
		if (executor == null) {
			throw new RejectedExecutionException("Unknown thread "+threadName);
		}
		return executor.post(runner, delay);
	}

}
