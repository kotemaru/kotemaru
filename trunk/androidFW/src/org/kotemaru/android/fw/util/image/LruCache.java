package org.kotemaru.android.fw.util.image;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kotemaru.android.fw.annotation.UiThreadOnly;
import org.kotemaru.android.fw.util.image.ImageLoader.CacheState;

import android.graphics.Bitmap;
import android.widget.ImageView;

class LruCache {
	private final LinkedList<CacheInfo> mCachedList = new LinkedList<CacheInfo>();
	private final Map<CharSequence, CacheInfo> mCacheInfoMap = new HashMap<CharSequence, CacheInfo>();
	private final ImageLoaderProducer mProducer;
	private final int mTagKey;
	private long mCacheSize = 0;

	public LruCache(ImageLoaderProducer producer) {
		mProducer = producer;
		mTagKey = producer.getImageViewTagKey();
	}
	public synchronized boolean changeState(CacheInfo cinfo, CacheState before, CacheState after) {
		if (cinfo.getState() == before) {
			cinfo.setState(after);
			return true;
		}
		return false;
	}
	public synchronized void clear() {
		mCachedList.clear();
		mCacheInfoMap.clear();
	}

	public synchronized void request(ImageView imageView, CacheInfo cinfo) {
		cinfo.addImageView(imageView);
		imageView.setTag(mTagKey, cinfo);
	}

	public synchronized void touch(CacheInfo cinfo) {
		mCachedList.remove(cinfo);
		mCachedList.addLast(cinfo);
	}
	
	@UiThreadOnly
	public synchronized void onLoad(CacheInfo cinfo, Bitmap bitmap) {
		addCache(cinfo, bitmap);
		List<ImageView> views = cinfo.getImageViews();
		for (ImageView imageView : views) {
			CacheInfo nowcinfo = (CacheInfo) imageView.getTag(mTagKey);
			if (cinfo == nowcinfo) {
				applyImage(imageView, cinfo);
				imageView.setTag(mTagKey, null);
			}
		}
		views.clear();
	}
	private synchronized void addCache(CacheInfo cinfo, Bitmap bitmap) {
		Bitmap oldBitmap = cinfo.setBitmap(bitmap);
		if (oldBitmap != null) {
			mCacheSize -= oldBitmap.getByteCount();
		}
		if (bitmap == null) {
			cinfo.setState(CacheState.FAILED);
			mCachedList.remove(cinfo);
		} else {
			cinfo.setState(CacheState.CACHED);
			mCachedList.addLast(cinfo);
			mCacheSize += bitmap.getByteCount();
		}
		while (mCacheSize > mProducer.getCacheSize()) {
			CacheInfo rmInfo = mCachedList.pollFirst();
			rmInfo.setState(CacheState.NIL);
			oldBitmap = rmInfo.setBitmap(null);
			if (oldBitmap != null) {
				mCacheSize -= oldBitmap.getByteCount();
			}
		}
	}

	public synchronized boolean checkRequest(CacheInfo cinfo) {
		List<ImageView> views = cinfo.getImageViews();
		for (ImageView imageView : views) {
			CacheInfo nowcinfo = (CacheInfo) imageView.getTag(mTagKey);
			if (cinfo == nowcinfo) return true;
		}
		cinfo.setState(CacheState.NIL);
		return false;
	}

	public synchronized CacheInfo getCacheInfo(CharSequence imageId, boolean isCreate) {
		CacheInfo cinfo = mCacheInfoMap.get(imageId);
		if (cinfo == null && isCreate) {
			cinfo = new CacheInfo(imageId);
			mCacheInfoMap.put(imageId, cinfo);
		}
		if (mCacheInfoMap.size() > mProducer.getCacheSize() * 5) {
			triming();
		}
		return cinfo;
	}
	private synchronized void triming() {
		for (Map.Entry<CharSequence, CacheInfo> ent : mCacheInfoMap.entrySet()) {
			CacheInfo val = ent.getValue();
			if (val.getState() == CacheState.NIL) {
				mCacheInfoMap.remove(ent.getKey());
			}
		}
	}
	public synchronized boolean applyImage(ImageView imageView, CacheInfo cinfo) {
		switch (cinfo.getState()) {
		case CACHED:
			imageView.setImageBitmap(cinfo.getBitmap());
			return true;
		case FAILED:
			imageView.setImageResource(mProducer.getFailedImageResourceId());
			return true;
		default:
			return false;
		}
	}

	public static class CacheInfo {
		final CharSequence mImageId;
		private CacheState mState = CacheState.NIL;
		private Bitmap mBitmap;
		private List<ImageView> mImageViews;

		private CacheInfo(CharSequence imageId) {
			mImageId = imageId;
		}
		private CacheState getState() {
			return mState;
		}
		private void setState(CacheState state) {
			mState = state;
		}
		private Bitmap getBitmap() {
			return mBitmap;
		}
		private Bitmap setBitmap(Bitmap bitmap) {
			Bitmap old = mBitmap;
			mBitmap = bitmap;
			return old;
		}
		private List<ImageView> getImageViews() {
			return mImageViews;
		}
		private void addImageView(ImageView imageView) {
			if (mImageViews == null) {
				mImageViews = new LinkedList<ImageView>();
			}
			mImageViews.add(imageView);
		}
	}
}
