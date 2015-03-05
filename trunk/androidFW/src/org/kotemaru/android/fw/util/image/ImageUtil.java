package org.kotemaru.android.fw.util.image;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;

public class ImageUtil {
	private static final String TAG = null;

	public static BitmapFactory.Options loadBitmapSize(InputStream in) throws IOException {
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, option);
		return option;
	}

	public static Bitmap loadBitmap(InputStream in, Point size) throws IOException {
		try {
			in.mark(1024*1024);
			BitmapFactory.Options realSize = loadBitmapSize(in);
			in.reset();
			if (realSize == null) return null;
			int scaleW = realSize.outWidth / size.x + 1;
			int scaleH = realSize.outHeight / size.y + 1;
			int scale = Math.max(scaleW, scaleH);

			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeStream(in, null, option);
			return bitmap;
		} catch (IOException e) {
			Log.e(TAG, "loadBitmap:" + e, e);
			throw e;
		} finally {
			if (in != null) in.close();
		}
	}
	
	public static Bitmap scaleOutside(Bitmap srcBitmap, Point size) {
		float dispAspect = (float) size.x / (float) size.y;
		float imgAspect = (float) srcBitmap.getWidth() / (float) srcBitmap.getHeight();
		int x, y, w, h;
		if (dispAspect > imgAspect) {
			w = srcBitmap.getWidth();
			h = (int) (srcBitmap.getWidth() / dispAspect);
			x = 0;
			y = srcBitmap.getHeight() / 2 - h / 2;
		} else {
			w = (int) (srcBitmap.getHeight() * dispAspect);
			h = srcBitmap.getHeight();
			x = srcBitmap.getWidth() / 2 - w / 2;
			y = 0;
		}
		// Log.d(TAG, "image size=" + dispAspect + "," + imgAspect + "," + x + "," + y + "," + w + "," + h);
		Bitmap cropBitmap = Bitmap.createBitmap(srcBitmap, x, y, w, h);
		Bitmap dstBitmap = Bitmap.createScaledBitmap(cropBitmap, size.x, size.y, true);
		cropBitmap.recycle();
		return dstBitmap;
	}
	
	public static Bitmap scaleInside(Bitmap srcBitmap, Point size) {
		float dispAspect = (float) size.x / (float) size.y;
		float imgAspect = (float) srcBitmap.getWidth() / (float) srcBitmap.getHeight();
		int w, h;
		if (dispAspect > imgAspect) {
			w = (int)(size.y * imgAspect);
			h = size.y;
		} else {
			w = size.x;
			h = (int)(size.x * imgAspect);
		}
		Bitmap dstBitmap = Bitmap.createScaledBitmap(srcBitmap, w, h, true);
		return dstBitmap;
	}

	
	
}
