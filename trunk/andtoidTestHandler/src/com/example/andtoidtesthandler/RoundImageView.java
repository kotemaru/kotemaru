package com.example.andtoidtesthandler;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class RoundImageView extends ImageView {

	public static final String TAG = RoundImageView.class.getSimpleName();
	private Paint mMaskedPaint;
	private Paint mCopyPaint;
	private Drawable mMaskDrawable;
	private Rect mBounds;
	private RectF mBoundsF;
	private URL imageUrl;
	private Bitmap imageBitmap;
	private ImageLoadAsyncTask imageLoadAsyncTask;

	public RoundImageView(Context context) {
		this(context, null);
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mMaskedPaint = new Paint();
		mMaskedPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_ATOP));
		mCopyPaint = new Paint();
		mMaskDrawable = getResources().getDrawable(R.drawable.round_image_view_bg);

		setImageUrl("http://seiga.nicovideo.jp/book/static/img/book/000/218/078/HgQTekDaWWRgyuvF.410x410.jpg");

	}

	public void setImageUrl(String urlStr) {
		try {
			URL url = new URL(urlStr);
			if (url.equals(imageUrl)) return;
			this.setImageDrawable(null);
			if (imageBitmap != null) imageBitmap.recycle();
			imageBitmap = null;
			imageUrl = url;
			if (this.isActivated()) {
				startLoadImage();
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, "Image load failed:" + urlStr, e);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		startLoadImage();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		cancelLoadImage();
		if (imageBitmap != null) imageBitmap.recycle();
		imageBitmap = null;
	}

	private void startLoadImage() {
		if (imageUrl != null && imageBitmap == null) {
			this.setImageResource(R.drawable.waiting);
			cancelLoadImage();
			imageLoadAsyncTask = new ImageLoadAsyncTask();
			imageLoadAsyncTask.execute(imageUrl);
		}
	}
	private void onLoadImage(Bitmap bitmap) {
		this.imageBitmap = bitmap;
		this.setImageBitmap(bitmap);
	}
	private void cancelLoadImage() {
		if (imageLoadAsyncTask != null) {
			imageLoadAsyncTask.cancel(true);
		}
	}

	private class ImageLoadAsyncTask extends AsyncTask<URL, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(URL... params) {
			URL imageUrl = params[0];
			Log.i(TAG, "Image loading:" + imageUrl);
			InputStream in;
			try {
				in = imageUrl.openStream();
				try {
					if (this.isCancelled()) return null;
					Bitmap bitmap = BitmapFactory.decodeStream(in);
					return bitmap;
				} finally {
					in.close();
				}
			} catch (Exception e) {
				Log.e(TAG, "Image load failed:" + imageUrl, e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (!this.isCancelled()) {
				onLoadImage(result);
			} else {
				result.recycle();
			}
		}
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mBounds = new Rect(0, 0, w, h);
		mBoundsF = new RectF(mBounds);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int sc = canvas.saveLayer(mBoundsF, mCopyPaint,
				Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG);
		mMaskDrawable.setBounds(mBounds);
		mMaskDrawable.draw(canvas);
		canvas.saveLayer(mBoundsF, mMaskedPaint, 0);
		super.onDraw(canvas);
		canvas.restoreToCount(sc);
	}
}