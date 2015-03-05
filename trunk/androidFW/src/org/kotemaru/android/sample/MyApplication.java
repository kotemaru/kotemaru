package org.kotemaru.android.sample;

import org.kotemaru.android.fw.FwActivity;
import org.kotemaru.android.fw.FwApplicationBase;
import org.kotemaru.android.fw.thread.DefaultThreadManager;
import org.kotemaru.android.fw.thread.ThreadManager;

public class MyApplication extends FwApplicationBase<RootModel, FwActivity, RootController> {

	@Override
	public ThreadManager createThreadManager() {
		ThreadManager tm = DefaultThreadManager.getInstance();
		tm.registerThread(ThreadManager.WORKER);
		tm.registerThread(ThreadManager.NETWORK, 2, Thread.MIN_PRIORITY);
		return tm;
	}

	@Override
	public RootModel createModel() {
		return new RootModel();
	}

	@Override
	public RootController createController() {
		return new RootController(this);
	}

}
