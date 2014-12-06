package org.kotemaru.android.postit.util;

import org.kotemaru.android.postit.PostItSettingActivity;
import org.kotemaru.android.postit.data.PostItData;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
	public static String ACTION_CHANGE_DATA = "org.kotemaru.android.postit.ACTION_CHANGE_DATA";
	public static final String POST_IT_ID = "POST_IT_ID";

	/**
	 * 各付箋の編集Activityの起動。
	 * @param context
	 * @param postItData 付箋データ。
	 */
	public static void startPostItSettingsActivity(Context context, PostItData postItData) {
		Intent intent = new Intent(context, PostItSettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(POST_IT_ID, postItData.getId());
		context.startActivity(intent);
	}
	
	/**
	 * 画像選択の開始。
	 * @param context
	 * @param code リクエストコード
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static void startChoosePicture(Activity context, int code) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*");
			context.startActivityForResult(intent, code);
		} else {
			Intent intent = new Intent(Intent.ACTION_PICK);
			//intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			context.startActivityForResult(intent, code);
		}
	}
	/**
	 * startChoosePicture()の結果を処理して選択画像のURIを取得する。
	 * <li>永続的パーミッションを取得しないとOS再起動後にパーミッションエラーになる。
	 * @param context
	 * @param requestCode
	 * @param resultCode
	 * @param returnedIntent
	 * @return 画像URI
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static Uri getResultChoosePictureUri(Context context, int requestCode, int resultCode, Intent returnedIntent) {
		if (resultCode != Activity.RESULT_OK) return null;
		Uri uri = returnedIntent.getData();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			final int takeFlags = returnedIntent.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
			context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
		}
		return uri;
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
	 * Live壁紙にデータの変更を通知。
	 * @param context
	 */
	public static void notifyChangeData(Context context) {
		Intent intent = new Intent(ACTION_CHANGE_DATA);
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
