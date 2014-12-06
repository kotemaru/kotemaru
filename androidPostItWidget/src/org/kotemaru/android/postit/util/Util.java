package org.kotemaru.android.postit.util;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;


/**
 * 雑多なユーティリティ。
 * @author kotemaru.org
 */
public class Util {
	private static final String TAG = Util.class.getSimpleName();

	
	/**
	 * システムのステータスバーの高さを得る。
	 * @param context
	 * @return ステータスバーの高さpx値
	 */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * ディスプレイサイズを得る。
	 * ステータスバーは含むがナビゲーションバーは含まない。
	 * @param context
	 * @return
	 */
	public static Point getDisplaySize(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		return point;
	}

	/**
	 * URIからストリームを開く。
	 * <li>サポートスキーマは現在のところ "content://" と "assets://" のみ。
	 * @param context
	 * @param uri
	 * @return
	 * @throws IOException
	 */
	public static InputStream openUri(Context context, Uri uri) throws IOException {
		String scheme = uri.getScheme();
		if ("assets".equals(scheme)) {
			return context.getAssets().open(uri.getPath().substring(1));
		} else {
			return context.getContentResolver().openInputStream(uri);
		}
	}

	/**
	 * URIから画像のサイズを得る。
	 * @param context
	 * @param uri
	 * @return
	 * @throws IOException
	 */
	public static BitmapFactory.Options loadBitmapSize(Context context, Uri uri) throws IOException {
		if (uri == null) return null;
		InputStream in = null;
		try {
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inJustDecodeBounds = true;
			in = openUri(context, uri);
			BitmapFactory.decodeStream(in, null, option);
			return option;
		} catch (IOException e) {
			Log.e(TAG, "loadBitmapSize:" + e, e);
			throw e;
		} finally {
			if (in != null) in.close();
		}
	}
	
	/**
	 * URIから画像を得る。サイズの調整も行う。
	 * <li>要求サイズ丁度では無い。画像の劣化が起こらない最小サイズ。
	 * @param context
	 * @param uri
	 * @param size 要求サイズ。
	 * @return サイズ補正済Bitmap。
	 * @throws IOException
	 */
	public static Bitmap loadBitmap(Context context, Uri uri, Point size) throws IOException {
		if (uri == null) return null;
		InputStream in = null;
		try {
			BitmapFactory.Options realSize = loadBitmapSize(context, uri);
			if (realSize == null) return null;
			int scaleW = realSize.outWidth / size.x + 1;
			int scaleH = realSize.outHeight / size.y + 1;
			int scale = Math.max(scaleW, scaleH);

			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inSampleSize = scale;
			in = openUri(context, uri);
			Bitmap bitmap = BitmapFactory.decodeStream(in, null, option);
			return bitmap;
		} catch (IOException e) {
			Log.e(TAG, "loadBitmap:" + e, e);
			throw e;
		} finally {
			if (in != null) in.close();
		}
	}

	/**
	 * オーバレイ・レイヤ用のパラメータを返す。
	 * @return
	 */
	public static WindowManager.LayoutParams getWindowLayoutParams() {
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
						// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.TOP | Gravity.START;
		return params;
	}
	

	public static int sp2px(Context context, int sp) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (sp * scale + 0.5f);
	}
	public static int dp2px(Context context, int dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
	public static int dp2px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
}