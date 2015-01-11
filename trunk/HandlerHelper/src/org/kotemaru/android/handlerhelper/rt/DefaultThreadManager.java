package org.kotemaru.android.handlerhelper.rt;

import android.os.Handler;
import android.os.Looper;

public class DefaultThreadManager implements ThreadManager {
	
	protected Handler mUiHandler = new Handler(Looper.getMainLooper());
	protected Handler mWorkerHandler = new Handler(getLooper(WORKER));
	protected Handler mNetworkHandler = new Handler(getLooper(NETWORK));

	protected static ThreadManager sInstance = null;
	// Not MT-safe
	public static ThreadManager getInstance() {
		if (sInstance == null) {
			sInstance = new DefaultThreadManager();
		}
		return sInstance;
	}
	
	
	@Override
	public Handler getHandler(String threadName) {
		if (UI.equals(threadName)) return mUiHandler;
		if (WORKER.equals(threadName)) return mWorkerHandler;
		if (NETWORK.equals(threadName)) return mNetworkHandler;
		return null;
	}
	@Override
	public void post(String threadName, Runner runner, int delay) {
		final Handler handler = getHandler(threadName);
		if (delay == 0) {
			handler.post(runner);
		} else {
			handler.postDelayed(runner, delay);
		}
	}

	protected static Looper getLooper(String name) {
		return new LooperThread(name).startLooper();
	}
	
	protected static class LooperThread extends Thread {
		private Looper mLooper = null;
		
		public LooperThread(String name) {
			super(name);
		}
		
		public synchronized Looper startLooper() {
			this.start();
			while (mLooper == null) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return mLooper;
		}
		
		@Override
		public void run() {
			synchronized (this) {
				Looper.prepare();
				mLooper = Looper.myLooper();
				this.notifyAll();
			}
			Looper.loop();
		}
	}

}
