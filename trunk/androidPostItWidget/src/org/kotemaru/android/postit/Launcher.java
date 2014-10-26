package org.kotemaru.android.postit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.wallpaper.WallpaperService;


/**
 * 各種 Intent 発行ユーティリティ
 * @author kotemaru.org
 */
public class Launcher {
	public static final int CHOOSE_PICTURE_DAY = 1000;
	public static final int CHOOSE_PICTURE_NIGHT = 1002;
	public static String ACTION_CHANGE_SETTENGS = "org.kotemaru.android.postit.ACTION_CHANGE_SETTENGS";

	/**
	 * 各付箋の編集Activityの起動。
	 * @param context
	 * @param postItData 付箋データ。
	 */
	public static void startPostItSettingsActivity(Context context, PostItData postItData) {
		Intent intent = new Intent(context, PostItSettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(PostItSettingActivity.POST_IT_ID, postItData.getId());
		context.startActivity(intent);
	}
	
	/**
	 * 画像選択の開始。
	 * @param context
	 * @param code リクエストコード
	 */
	public static void startChoosePicture(Activity context, int code) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		context.startActivityForResult(intent, code);
	}
	
	/**
	 * Live壁紙に設定の変更を通知。
	 * @param context
	 */
	public static void notifyChangeSettings(Context context) {
		Intent intent = new Intent(ACTION_CHANGE_SETTENGS);
		context.startService(intent);
	}
	
	/**
	 * ライブ壁紙を設定。
	 * <li>API-15以前は選択画面を表示。
	 * @param context
	 * @param liveWallpaper ライブ壁紙
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void setupLiveWallpaper(Activity context, Class<? extends WallpaperService> liveWallpaper) {
		Intent intent = new Intent();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			String pkgName = liveWallpaper.getPackage().getName();
			String clsName = liveWallpaper.getCanonicalName();
			intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
			intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(pkgName, clsName));
		} else {
			intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
		}
		context.startActivityForResult(intent, 0);
	}

}
