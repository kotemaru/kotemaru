package org.kotemaru.android.fw.thread;


public interface ThreadManager {
	public static final String UI = "UI";
	public static final String WORKER = "WORKER";
	public static final String NETWORK = "NETWORK";
	
	public Executor registerThread(String threadName);
	public Executor registerThread(String threadName, int size, final int priority);
	public Executor getExecutor(String threadName);
	public boolean post(String threadName, Runnable runner, int delay);
}
