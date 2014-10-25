package org.kotemaru.android.postit;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.service.wallpaper.WallpaperService;

public class Launcher {
	public static final int CHOOSE_PICTURE = 1000;
	public static final int CHOOSE_PICTURE2 = 1002;
	public static String ACTION_CHANGE_SETTENGS = "org.kotemaru.android.postit.ACTION_CHANGE_SETTENGS";

	public static void startPostItEditActivity(Context context, PostItData postItData) {
		Intent intent = new Intent(context, PostItSettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(PostItSettingActivity.POST_IT_ID, postItData.getId());
		context.startActivity(intent);
	}
	public static void startChoosePicture(Activity context) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		context.startActivityForResult(intent, CHOOSE_PICTURE);
	}
	public static void startChoosePicture2(Activity context) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		context.startActivityForResult(intent, CHOOSE_PICTURE2);
	}
	public static void notifyChangeSettings(Context context) {
		Intent intent = new Intent(ACTION_CHANGE_SETTENGS);
		context.startService(intent);
	}
	public static  void setupLiveWallpaper(Activity context, Class<? extends WallpaperService> liveWallpaper) {
		Intent intent = new Intent();
		intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
		String pkgName = liveWallpaper.getPackage().getName();
		String clsName = liveWallpaper.getCanonicalName();
		intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(pkgName, clsName));
		context.startActivityForResult(intent, 0);
	}

}
