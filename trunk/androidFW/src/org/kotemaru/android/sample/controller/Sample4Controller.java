package org.kotemaru.android.sample.controller;

import org.kotemaru.android.delegatehandler.annotation.GenerateDelegateHandler;
import org.kotemaru.android.delegatehandler.annotation.Handle;
import org.kotemaru.android.fw.FwControllerBase;
import org.kotemaru.android.fw.thread.ThreadManager;
import org.kotemaru.android.sample.MyApplication;
import org.kotemaru.android.sample.model.Sample4Model;

@GenerateDelegateHandler
public class Sample4Controller extends FwControllerBase<MyApplication> {
	public final Sample4ControllerHandler mHandler;
	public final Sample4Model mModel;

	public Sample4Controller(MyApplication app) {
		super(app);
		mHandler = new Sample4ControllerHandler(this, app.getThreadManager());
		mModel = app.getModel().getSample4Model();
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
