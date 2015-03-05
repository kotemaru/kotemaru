package org.kotemaru.android.fw.util.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.kotemaru.android.delegatehandler.annotation.GenerateDelegateHandler;
import org.kotemaru.android.delegatehandler.annotation.Handle;
import org.kotemaru.android.fw.thread.ThreadManager;
import org.kotemaru.android.fw.util.image.LruCache.CacheInfo;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

@GenerateDelegateHandler
public class ImageLoader {
	private static final String TAG = ImageLoader.class.getSimpleName();
	public static final String IMAGE_LOADER_THREAD = "ImageLoader.NetworkThread";

	public enum CacheState {
		NIL, CACHED, NOW_LODING, FAILED
	}

	public interface OnClearCacheFilesListener {
		public void onClearCacheFiles(int failCount);
	}

	public interface OnLoadImageListener {
		public void onLoadImage(ImageView imageView, CharSequence imageId, Bitmap bitmap);
	}

	private ImageLoaderHandler mHandler;
	private LruCache mLruCache;
	private ImageLoaderProducer mProducer;

	public ImageLoader(ImageLoaderProducer producer) {
		ThreadManager tm = producer.getThreadManager();
		tm.registerThread(IMAGE_LOADER_THREAD, 1, Thread.MIN_PRIORITY);

		mProducer = producer;
		mHandler = new ImageLoaderHandler(this, tm);
		mLruCache = new LruCache(producer);
	}

	// UI-Thread
	public boolean setImage(ImageView imageView, CharSequence imageId) {
		if (imageId == null || imageId.length() == 0) {
			imageView.setImageResource(mProducer.getFailedImageResourceId());
			return false;
		}
		CacheInfo cinfo = mLruCache.getCacheInfo(imageId, true);
		if (mLruCache.applyImage(imageView, cinfo)) {
			mLruCache.touch(cinfo);
			return true;
		} else {
			imageView.setImageResource(mProducer.getLoadingImageResourceId());
			mLruCache.request(imageView, cinfo);
			mHandler.loadImage(cinfo);
			return false;
		}
	}

	public void clear() {
		mLruCache.clear();
	}

	@Handle(thread = ThreadManager.WORKER)
	public void clearCacheFiles(OnClearCacheFilesListener listener) {
		int failCount = -1;
		try {
			failCount = mProducer.clearCacheFiles();
		} finally {
			if (listener != null) {
				listener.onClearCacheFiles(failCount);
			}
		}
	}

	@Handle(thread = ThreadManager.UI)
	public void onLoadImage(CacheInfo cinfo, Bitmap bitmap) {
		mLruCache.onLoad(cinfo, bitmap);
	}

	@Handle(thread = ThreadManager.WORKER)
	public void loadImage(CacheInfo cinfo) {
		File imageFile = mProducer.getCacheFile(cinfo.mImageId);
		Log.d(TAG, "imageFile " + imageFile);
		if (imageFile != null && imageFile.canRead()) {
			Bitmap bitmap = loadBitmap(imageFile);
			mHandler.onLoadImage(cinfo, bitmap);
		} else if (mLruCache.changeState(cinfo, CacheState.NIL, CacheState.NOW_LODING)) {
			mHandler.downloadImage(cinfo);
		}
	}

	@Handle(thread = IMAGE_LOADER_THREAD)
	public void downloadImage(CacheInfo cinfo) {
		if (!mLruCache.checkRequest(cinfo)) return;

		CharSequence imageId = cinfo.mImageId;
		File imageFile = mProducer.getCacheFile(imageId);
		HttpClient httpClient = mProducer.getHttpClient();
		HttpUriRequest request = mProducer.getHttpRequest(imageId);
		Bitmap bitmap;
		try {
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() != 200
					|| response.getEntity() == null) {
				Log.e(TAG, "Http status " + response.getStatusLine() + ":" + request.getURI());
				bitmap = null;
			} else {
				if (imageFile != null) {
					mProducer.saveContent(response, imageFile);
					bitmap = loadBitmap(imageFile);
				} else {
					ResetableNetInputStream in = new ResetableNetInputStream(response.getEntity().getContent());
					bitmap = mProducer.createBitmap(in);
				}
			}
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			bitmap = null;
		}
		mHandler.onLoadImage(cinfo, bitmap);
	}

	private Bitmap loadBitmap(File imageFile) {
		try {
			InputStream in = new ResetableFileInputStream(imageFile);
			return mProducer.createBitmap(in);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "loadBitmap:" + e, e);
			return null;
		}
	}

}
