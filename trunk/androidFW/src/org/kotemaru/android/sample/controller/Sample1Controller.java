package org.kotemaru.android.sample.controller;

import org.kotemaru.android.delegatehandler.annotation.GenerateDelegateHandler;
import org.kotemaru.android.delegatehandler.annotation.Handle;
import org.kotemaru.android.fw.FwControllerBase;
import org.kotemaru.android.fw.thread.ThreadManager;
import org.kotemaru.android.sample.MyApplication;
import org.kotemaru.android.sample.model.Sample1Model;

@GenerateDelegateHandler
public class Sample1Controller extends FwControllerBase<MyApplication> {
	public final Sample1ControllerHandler mHandler;
	public final Sample1Model mModel;

	public Sample1Controller(MyApplication app) {
		super(app);
		mHandler = new Sample1ControllerHandler(this, app.getThreadManager());
		mModel = app.getModel().getSample1Model();
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
