package org.kotemaru.android.fw;

import android.app.Application;

public abstract class FwControllerBase<A extends Application> implements FwController {
	protected A mApplication;
	
	protected FwControllerBase(A app) {
		mApplication = app;
	}
	
	public A getApplication() {
		return mApplication;
	}
}
