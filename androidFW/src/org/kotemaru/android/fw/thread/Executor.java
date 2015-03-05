package org.kotemaru.android.fw.thread;


public interface Executor {
	public boolean post(Runnable runner, int delay);
}
