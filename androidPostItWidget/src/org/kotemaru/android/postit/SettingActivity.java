package org.kotemaru.android.postit;

import org.kotemaru.android.postit.PostItConst.PostItColor;
import org.kotemaru.android.postit.data.PostItData;
import org.kotemaru.android.postit.data.PostItDataProvider;
import org.kotemaru.android.postit.data.Settings;
import org.kotemaru.android.postit.util.Launcher;
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

/**
 * アプリメイン＆設定画面。
 * <li>初回起動時の初期化を行う。
 * <li>PostItWallpaperが壁紙に設定されていなければダイアログを出して設定画面に遷移する。
 * <li>背景画像、付箋トレイ表示アクションの設定を行う。
 * @author kotemaru.org
 */
public class SettingActivity extends Activity {
	private Settings mSettings;
	private WallpaperManager mWallpaperManager;
	private Button mChoosePictueDay;
	private Button mChoosePictueNight;
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
		mChoosePictueDay = (Button) findViewById(R.id.choose_picture);
		mChoosePictueDay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Launcher.startChoosePicture(SettingActivity.this, Launcher.CHOOSE_PICTURE_DAY);
			}
		});
		mChoosePictueNight = (Button) findViewById(R.id.choose_picture2);
		mChoosePictueNight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Launcher.startChoosePicture(SettingActivity.this, Launcher.CHOOSE_PICTURE_NIGHT);
			}
		});
		mCtrlActionRadioGroup = (RadioLayout) findViewById(R.id.ctrl_action_radio_group);
	}

	/**
	 * 初回起動時の初期化処理。
	 * <li>サンプルの付箋を登録する。
	 */
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

	/**
	 * PostItWallpaperが壁紙でなければダイアログを表示する。
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (!hasPostItWallpaper()) {
			showSetWallpaperDialog();
		}
		mCtrlActionRadioGroup.setValue(mSettings.getCtrlAction());
	}

	/**
	 * PostItWallpaperが壁紙になっているか確認。
	 * @return true=なっている。
	 */
	private boolean hasPostItWallpaper() {
		WallpaperInfo winfo = mWallpaperManager.getWallpaperInfo();
		if (winfo == null) return false;
		return PostItWallpaper.class.getCanonicalName().equals(winfo.getServiceName());
	}

	/**
	 * Viewの値からアプリ設定を更新。
	 * <li>PostItWallpaperに設定変更を通知する。
	 */
	@Override
	public void onPause() {
		mSettings.setCtrlAction(mCtrlActionRadioGroup.getValue());
		mSettings.save();
		Launcher.notifyChangeSettings(this);
		super.onPause();
	}

	/**
	 * 画像選択画面から選択画像の戻り値を受け取る。
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
		super.onActivityResult(requestCode, resultCode, returnedIntent);
		if (requestCode == Launcher.CHOOSE_PICTURE_DAY) {
			if (resultCode == RESULT_OK) {
				Uri uri = returnedIntent.getData();
				mSettings.setBackgroundUri("12:00", uri.toString());
			}
		}
		if (requestCode == Launcher.CHOOSE_PICTURE_NIGHT) {
			if (resultCode == RESULT_OK) {
				Uri uri = returnedIntent.getData();
				mSettings.setBackgroundUri("00:00", uri.toString());
			}
		}
	}

	/**
	 * PostItWallpaperを壁紙に設定する旨のダイアログ表示。
	 * <li>注意: 手抜きDialogなので落ちるかもしれないが初回だけなのでこのまま。
	 */
	private void showSetWallpaperDialog() {
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
