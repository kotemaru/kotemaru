package org.kotemaru.android.handlerhelper.rt;

import android.os.Handler;

public interface ThreadManager {
	public static final String UI = "UI";
	public static final String WORKER = "WORKER";
	public static final String NETWORK = "NETWORK";
	
	public Handler getHandler(String threadName);
	public boolean post(String threadName, Runner runner, int delay);
}
