package org.kotemaru.android.handlerhelper.rt;

public abstract class Runner implements Runnable {
	private int mRetryCount = 0;
	private int mInterval = 0;
	private float mIntervalRate = 0;

	public Runner() {
	}

	public void setRetryInfo(int retryCount, int interval, float intervalRate) {
		this.mRetryCount = retryCount;
		this.mInterval = interval;
		this.mIntervalRate = intervalRate;
	}
	public boolean doRetry() {
		mInterval = (int)(mInterval * mIntervalRate);
		return --mRetryCount >= 0;
	}
	public int getInterval() {
		return mInterval;
	}


	public String toTraceString(Runner caller, String threadName, String method, Object... arguments) {
		StringBuilder sbuf = new StringBuilder(200);
		String curThName = Thread.currentThread().getName();
		if ("main".equals(curThName)) curThName = ThreadManager.UI;
		sbuf.append(Integer.toHexString(System.identityHashCode(caller)))
				.append(':').append(curThName)
				.append("->")
				.append(Integer.toHexString(System.identityHashCode(this)))
				.append(':').append(threadName)
				.append(':').append(method).append('(');
		if (arguments != null) {
			for (Object arg : arguments) {
				if (arg != arguments[0]) sbuf.append(',');
				String str = String.valueOf(arg);
				if (str.length()>50) {
					sbuf.append(String.valueOf(arg).substring(0,50)).append("...");
				} else {
					sbuf.append(String.valueOf(arg));
				}
			}
		}
		sbuf.append(')');
		return sbuf.toString();
	}
}
