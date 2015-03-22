package org.kotemaru.android.sample.activity;

import java.io.File;

import org.kotemaru.android.fw.FwActivity;
import org.kotemaru.android.fw.R;
import org.kotemaru.android.fw.dialog.DialogHelper;
import org.kotemaru.android.fw.dialog.DialogHelper.OnDialogButtonListener;
import org.kotemaru.android.fw.dialog.DialogHelper.OnDialogButtonListenerBase;
import org.kotemaru.android.sample.MyApplication;
import org.kotemaru.android.sample.controller.Sample3Controller;
import org.kotemaru.android.sample.model.Sample3Model;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.MediaController;
import android.widget.VideoView;

public class Sample3Activity extends Activity implements FwActivity {

	private DialogHelper mDialogHelper = new DialogHelper(this);
	private Sample3Model mModel;
	private Sample3Controller mController;
	private VideoView mVideoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample3_activity);
		MyApplication app = (MyApplication) getApplication();
		mModel = app.getModel().getSample3Model();
		mController = app.getController().getSample3Controller();

		VideoView videoView = (VideoView) this.findViewById(R.id.videoView);
		videoView.setMediaController(new MediaController(this));
		File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		String path = new File(dir, "test.mp4").getAbsolutePath();
		videoView.setVideoPath(path);
		mVideoView = videoView;
	}

	@Override
	public void onResume() {
		super.onResume();
		update();
		if (mModel.getPlayPosition() > 0) {
			mVideoView.seekTo(mModel.getPlayPosition());
			mVideoView.start();
		}
	}
	@Override
	public void onPause() {
		mModel.setPlayPosition(mVideoView.getCurrentPosition());
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
