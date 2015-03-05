package org.kotemaru.android.fw.util.image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.kotemaru.android.fw.thread.ThreadManager;

import android.graphics.Bitmap;

public interface ImageLoaderProducer {
	public ThreadManager getThreadManager();
	public File getCacheFile(CharSequence imageId);
	public int clearCacheFiles();
	public long getCacheSize();
	public HttpClient getHttpClient();
	public int getFailedImageResourceId();
	public int getImageViewTagKey();
	public int getLoadingImageResourceId();
	public HttpUriRequest getHttpRequest(CharSequence imageId);
	public void saveContent(HttpResponse response, File imageFile) throws IOException;
	public Bitmap createBitmap(InputStream in);
}
