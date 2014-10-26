package org.kotemaru.android.postit;

import org.kotemaru.android.postit.util.Util;
import org.kotemaru.android.postit.widget.RadioLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingActivity extends Activity {
	private Settings mSettings;
	private WallpaperManager mWallpaperManager;
	private Button mChoosePictue;
	private Button mChoosePictue2;
	private RadioLayout mCtrlActionRadioGroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);
		setTitle(R.string.settings);
		mSettings = new Settings(this);
		mWallpaperManager = WallpaperManager.getInstance(this);
		if (mSettings.fiastBootInitialize()) {
			this.fiastBootInitialize();
		}
		mSettings.load();

		// Views setting.
		mChoosePictue = (Button) findViewById(R.id.choose_picture);
		mChoosePictue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Launcher.startChoosePicture(SettingActivity.this);
			}
		});
		mChoosePictue2 = (Button) findViewById(R.id.choose_picture2);
		mChoosePictue2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Launcher.startChoosePicture2(SettingActivity.this);
			}
		});
		mCtrlActionRadioGroup = (RadioLayout) findViewById(R.id.ctrl_action_radio_group);
	}

	private void fiastBootInitialize() {
		PostItData data = new PostItData(-1, PostItColor.BLUE, Util.dp2px(this, 100), Util.dp2px(this, 50));
		data.setMemo("Sample-1");
		PostItDataProvider.createPostItData(this, data);
		data.setPosX(Util.dp2px(this, 150));
		data.setPosY(Util.dp2px(this, 200));
		data.setColor(PostItColor.PINK);
		data.setMemo("Sample-2");
		PostItDataProvider.createPostItData(this, data);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hasPostItWallpaper()) {
			showAlertDialog();
		}
		mCtrlActionRadioGroup.setValue(mSettings.getCtrlAction());
	}
	private boolean hasPostItWallpaper() {
		WallpaperInfo winfo = mWallpaperManager.getWallpaperInfo();
		if (winfo == null) return false;
		return PostItWallpaper.class.getCanonicalName().equals(winfo.getServiceName());
	}

	@Override
	public void onPause() {
		mSettings.setCtrlAction(mCtrlActionRadioGroup.getValue());
		mSettings.save();
		Launcher.notifyChangeSettings(this);
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
		super.onActivityResult(requestCode, resultCode, returnedIntent);
		if (requestCode == Launcher.CHOOSE_PICTURE) {
			if (resultCode == RESULT_OK) {
				Uri uri = returnedIntent.getData();
				mSettings.setBackgroundUri("12:00", uri.toString());
			}
		}
		if (requestCode == Launcher.CHOOSE_PICTURE2) {
			if (resultCode == RESULT_OK) {
				Uri uri = returnedIntent.getData();
				mSettings.setBackgroundUri("00:00", uri.toString());
			}
		}
	}

	/**
	 * TODO: 手抜きDialog.
	 */
	private void showAlertDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(R.string.dialog_init_title);
		alertDialogBuilder.setMessage(R.string.dialog_init_msg);
		alertDialogBuilder.setPositiveButton(R.string.dialog_init_ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Launcher.setupLiveWallpaper(SettingActivity.this, PostItWallpaper.class);
					}
				});
		alertDialogBuilder.setNegativeButton(R.string.dialog_init_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
		alertDialogBuilder.setCancelable(true);
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

}
