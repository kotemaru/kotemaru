package org.kotemaru.android.fw.util.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.kotemaru.android.fw.FwApplicationContext;
import org.kotemaru.android.fw.annotation.ThreadUnsafe;
import org.kotemaru.android.fw.thread.ThreadManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;

@ThreadUnsafe
public class DefaultImageLoaderProducer implements ImageLoaderProducer {
	protected static final String TAG = DefaultImageLoaderProducer.class.getSimpleName();
	protected Context mContext;
	protected File mCacheDir;
	protected HttpClient mHttpClient;
	protected String mBaseUri;
	protected boolean mIsEscapeFileName = false;
	protected byte[] mByteBuffer = new byte[4096];
	protected long mCacheSize;
	protected int mFailedImageResourceId = android.R.drawable.ic_delete;
	protected int mLoadingImageResourceId = android.R.drawable.spinner_background;
	protected int mImageViewTagKey = -1;
	protected Point mImageSize = null;
	protected boolean mIsImageSizeJustFit = false;
	
	public DefaultImageLoaderProducer(Context context) {
		mContext = context;
		mCacheSize = Runtime.getRuntime().maxMemory() / 10;
	}

	@Override
	public ThreadManager getThreadManager() {
		return ((FwApplicationContext) mContext.getApplicationContext()).getThreadManager();
	}

	@Override
	public File getCacheFile(CharSequence imageId) {
		if (mCacheDir == null) return null;
		String fname = mIsEscapeFileName ? toFileName(imageId) : imageId.toString();
		File file = new File(mCacheDir, fname);
		return file;
	}

	@Override
	public int clearCacheFiles() {
		int failCount = 0;
		File files[] = mCacheDir.listFiles();
		for (File file : files) {
			if (file.delete() == false) failCount++;
		}
		return failCount;
	}

	@Override
	public Bitmap createBitmap(InputStream in) {
		try {
			try {
				Bitmap bitmap;
				if (mImageSize != null) {
					bitmap = ImageUtil.loadBitmap(in, mImageSize);
					if (mIsImageSizeJustFit) {
						bitmap = ImageUtil.scaleInside(bitmap, mImageSize);
					}
				} else {
					bitmap = BitmapFactory.decodeStream(in);
				}
				return bitmap;
			} finally {
				in.close();
			}
		} catch (IOException e) {
			Log.e(TAG, e.toString(), e);
			return null;
		}
	}

	@Override
	public void saveContent(HttpResponse response, File imageFile) throws IOException {
		if (!mIsEscapeFileName) imageFile.getParentFile().mkdirs();

		InputStream in = response.getEntity().getContent();
		try {
			FileOutputStream out = new FileOutputStream(imageFile);
			try {
				int n;
				while ((n = in.read(mByteBuffer)) >= 0) {
					out.write(mByteBuffer, 0, n);
				}
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}
	}

	@Override
	public HttpClient getHttpClient() {
		if (mHttpClient == null) {
			mHttpClient = new DefaultHttpClient();
		}
		return mHttpClient;
	}
	@Override
	public HttpUriRequest getHttpRequest(CharSequence imageId) {
		String uri = (mBaseUri == null) ? imageId.toString() : (mBaseUri + imageId);
		return new HttpGet(uri);
	}

	@Override
	public long getCacheSize() {
		return mCacheSize;
	}

	@Override
	public int getLoadingImageResourceId() {
		return mLoadingImageResourceId;
	}
	@Override
	public int getFailedImageResourceId() {
		return mFailedImageResourceId;
	}

	@Override
	public int getImageViewTagKey() {
		return mImageViewTagKey;
	}

	// ---------------------------------------------------------------------------------------
	public boolean isEscapeFileName() {
		return mIsEscapeFileName;
	}
	public DefaultImageLoaderProducer setEscapeFileName(boolean isEscapeFileName) {
		mIsEscapeFileName = isEscapeFileName;
		return this;
	}

	public DefaultImageLoaderProducer setHttpClient(HttpClient httpClient) {
		mHttpClient = httpClient;
		return this;
	}
	public DefaultImageLoaderProducer setCacheSize(long cacheSize) {
		mCacheSize = cacheSize;
		return this;
	}
	public DefaultImageLoaderProducer setFailedImageResourceId(int failedImageResourceId) {
		mFailedImageResourceId = failedImageResourceId;
		return this;
	}
	public DefaultImageLoaderProducer setLoadingImageResourceId(int loadingImageResourceId) {
		mLoadingImageResourceId = loadingImageResourceId;
		return this;
	}
	public DefaultImageLoaderProducer setImageViewTagKey(int imageViewTagKey) {
		mImageViewTagKey = imageViewTagKey;
		return this;
	}

	public Point getImageSize() {
		return mImageSize;
	}
	public DefaultImageLoaderProducer setImageSize(Point imageSize) {
		mImageSize = imageSize;
		return this;
	}

	public boolean isImageSizeJustFit() {
		return mIsImageSizeJustFit;
	}
	public DefaultImageLoaderProducer setImageSizeJustFit(boolean imageSizeJustFit) {
		mIsImageSizeJustFit = imageSizeJustFit;
		return this;
	}

	public DefaultImageLoaderProducer setCacheDir(File cacheDir) {
		mCacheDir = cacheDir;
		if (cacheDir != null) cacheDir.mkdirs();
		return this;
	}
	public DefaultImageLoaderProducer setBaseUri(String baseUri) {
		mBaseUri = baseUri;
		mIsEscapeFileName = (baseUri == null);
		return this;
	}

	// ---------------------------------------------------------------------------------------
	private static final String INVALID_CHARS = "<>:*?\"/\\|#";
	private static final String ESCAPE_CHARS[] = { "#3c", "#3e", "#3a", "#2a", "#3f", "#22", "#2f", "#5c", "#7c", "#23" };

	protected String toFileName(CharSequence imageId) {
		StringBuilder sbuf = new StringBuilder(imageId.length() * 2);
		for (int i = 0; i < imageId.length(); i++) {
			char ch = imageId.charAt(i);
			int idx = INVALID_CHARS.indexOf(ch);
			if (idx >= 0) {
				sbuf.append(ESCAPE_CHARS[idx]);
			} else {
				sbuf.append(ch);
			}
		}
		return sbuf.toString();
	}

}
