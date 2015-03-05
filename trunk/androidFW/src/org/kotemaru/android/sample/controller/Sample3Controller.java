package org.kotemaru.android.sample.controller;

import org.kotemaru.android.delegatehandler.annotation.GenerateDelegateHandler;
import org.kotemaru.android.delegatehandler.annotation.Handle;
import org.kotemaru.android.fw.FwControllerBase;
import org.kotemaru.android.fw.thread.ThreadManager;
import org.kotemaru.android.sample.MyApplication;
import org.kotemaru.android.sample.model.Sample3Model;

@GenerateDelegateHandler
public class Sample3Controller extends FwControllerBase<MyApplication> {
	public final Sample3ControllerHandler mHandler;
	public final Sample3Model mModel;

	public Sample3Controller(MyApplication app) {
		super(app);
		mHandler = new Sample3ControllerHandler(this, app.getThreadManager());
		mModel = app.getModel().getSample3Model();
	}

	@Handle(thread = ThreadManager.WORKER)
	public void openAlertDialog() {
		mModel.getDialogModel().setAlert("Test tilte", "Test message");
		mApplication.updateCurrentActivity();
	}

	@Handle(thread = ThreadManager.WORKER)
	public void openConfirmDialog() {
		mModel.getDialogModel().setConfirm("Test tilte", "Test confirm message");
		mApplication.updateCurrentActivity();
	}

	@Handle(thread = ThreadManager.WORKER)
	public void openProgress() {
		mModel.getDialogModel().setProgress("Test message", true);
		mApplication.updateCurrentActivity();
	}

}
