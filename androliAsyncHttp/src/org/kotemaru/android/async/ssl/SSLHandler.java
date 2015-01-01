package org.kotemaru.android.async.ssl;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class SSLHandler extends Handler {
	public static SSLHandler sInstance;

	public static SSLHandler getInstance()  {
		if (sInstance == null) {
			LooperThread th = new LooperThread(SSLHandler.class.getSimpleName());
			sInstance = new SSLHandler(th.getLooper());
		}
		return sInstance;
	}

	static class LooperThread extends Thread {
		private Looper mLooper;
		public LooperThread(String name) {
			super(name);
		}
		public synchronized Looper getLooper() {
			while (mLooper == null) {
				// @formatter:off
				try {wait();} catch (InterruptedException e) {}
				// @formatter:on
			}
			return mLooper;
		}
		public void run() {
			Looper.prepare();
			synchronized (this) {
				mLooper = Looper.myLooper();
				notifyAll();
			}
			Looper.loop();
		}
	}
	public SSLHandler(Looper looper) {
		super(looper);
	}

	public static void postHandling(SSLSelectorItem item, int opration) {
		Message msg = Message.obtain(null, opration, item);
		getInstance().sendMessage(msg);
	}
	public void handleMessage(Message msg) {
		SSLSelectorItem item = (SSLSelectorItem) msg.obj;
		switch (msg.what) {
		case SelectorItem.OP_READ:
			item.doReadable();
			break;
		case SelectorItem.OP_WRITE:
			item.doWritable();
			break;
		default:
			break;
		}
	}
}
