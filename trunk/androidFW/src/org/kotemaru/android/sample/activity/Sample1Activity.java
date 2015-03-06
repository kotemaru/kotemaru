package org.kotemaru.android.sample.activity;

import org.kotemaru.android.fw.FwActivity;
import org.kotemaru.android.fw.R;
import org.kotemaru.android.fw.dialog.DialogHelper;
import org.kotemaru.android.fw.dialog.DialogHelper.OnDialogButtonListener;
import org.kotemaru.android.fw.dialog.DialogHelper.OnDialogButtonListenerBase;
import org.kotemaru.android.sample.Launcher;
import org.kotemaru.android.sample.MyApplication;
import org.kotemaru.android.sample.controller.Sample1Controller;
import org.kotemaru.android.sample.model.Sample1Model;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Sample1Activity extends Activity implements FwActivity {

	private DialogHelper mDialogHelper = new DialogHelper(this);
	private Sample1Model mModel;
	private Sample1Controller mController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample1_activity);
		MyApplication app = (MyApplication) getApplication();
		mModel = app.getModel().getSample1Model();
		mController = app.getController().getSample1Controller();

		this.findViewById(R.id.alert_dialog).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mController.mHandler.openAlertDialog();
			}
		});
		this.findViewById(R.id.confirm_dialog).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mController.mHandler.openConfirmDialog();
			}
		});
		this.findViewById(R.id.progress_dialog).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mController.mHandler.openProgress();
			}
		});

	}

	public void onClickSample2(View view) {
		Launcher.startSample2(this);
	}
	
	public void onClickSample3(View view) {
		Launcher.startSample3(this);
	}
	
	public void onClickSample4(View view) {
		Launcher.startSample4(this);
	}
	public void onClickSample5(View view) {
		Launcher.startSample5(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		update();
	}
	@Override
	public void onPause() {
		mDialogHelper.clear();
		super.onPause();
	}

	@Override
	public void update() {
		if (!mModel.tryReadLock()) return;
		try {
			mDialogHelper.doDialog(mModel.getDialogModel(), mOnDialogButtonListener);
		} finally {
			mModel.readUnlock();
		}
	}

	private OnDialogButtonListener mOnDialogButtonListener = new OnDialogButtonListenerBase() {

	};

}
