package org.kotemaru.android.logicasync;

public class RetryException extends RuntimeException {
	private int retryCount = 3;
	private int delay = 0;

	public RetryException(int retryCount, int delay, String msg, Throwable t) {
		super(msg, t);
		this.setRetryCount(retryCount);
		this.setDelay(delay);
	}
	
	
	public RetryException(int retryCount, String msg) {
		this(retryCount, 0, msg, null);
	}
	public RetryException(int retryCount, Throwable t) {
		this(retryCount, 0, null, t);
	}
	public RetryException(int retryCount, int delay, String msg) {
		this(retryCount, delay, msg, null);
	}
	public RetryException(int retryCount, int delay, Throwable t) {
		this(retryCount, delay, null, t);
	}
	
	public int getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
}
