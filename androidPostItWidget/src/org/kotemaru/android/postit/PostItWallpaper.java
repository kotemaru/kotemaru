package org.kotemaru.android.postit;

import java.io.IOException;
import java.util.List;

import org.kotemaru.android.postit.util.Util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class PostItWallpaper extends WallpaperService {
	private static final String TAG = PostItWallpaper.class.getSimpleName();

	private int mStatusBarHeight;
	private Rect mBounds;
	private CtrlPanel mCtrlPanel;
	private PostItViewManager mPostItViewManager;
	private Settings mSettings;
	private DrawEngine mEngine;
	private boolean mIsRaisePostIt = true;

	@Override
	public void onCreate() {
		super.onCreate();
		mStatusBarHeight = Util.getStatusBarHeight(this);
		Point size = Util.getDisplaySize(this);
		mBounds = new Rect(0,  0, size.x, size.y - mStatusBarHeight);

		mCtrlPanel = CtrlPanel.create(this);
		mPostItViewManager = new PostItViewManager(this);
		mSettings = new Settings(this).load();

		mCtrlPanel.hide();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) return super.onStartCommand(intent, flags, startId);

		String action = intent.getAction();
		Log.d(TAG, "onStartCommand:" + action);
		if (Launcher.ACTION_CHANGE_SETTENGS.equals(action)) {
			mSettings.load();
			update();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		mEngine = new DrawEngine();
		return mEngine;
	}
	
	
	public CtrlPanel getCtrlPanel() {
		return this.mCtrlPanel;
	}

	public PostItViewManager getPostItViewManager() {
		return mPostItViewManager;
	}

	public PostItView createPostIt(PostItData data) {
		long id = PostItDataProvider.createPostItData(this, data);
		mPostItViewManager.update();
		PostItView view = mPostItViewManager.getPostItViewFromId(id);
		return view;
	}

	public void setRaisePostIt(boolean b) {
		this.mIsRaisePostIt = b;
		mEngine.update();
	}
	public boolean isRaisePostIt() {
		return this.mIsRaisePostIt;
	}
	public boolean isVisible() {
		if (mEngine == null) return false;
		return mEngine.mVisible;
	}
	public void update() {
		if (mEngine == null) return ;
		mEngine.update();
	}

	public Rect getBounds() {
		return mBounds;
	}

	class DrawEngine extends Engine {
		private static final int ALPHA = (int) (255 * 0.7);

		private final Paint mPaint = new Paint();
		private boolean mVisible;
		private String mBackgroundUri = null;
		private Bitmap mBackground = null;
		private long mLastTapTime = -1;

		DrawEngine() {
		}

		public Bitmap getBackgroundBitmap() {
			String uri = mSettings.getBackgroundUri(System.currentTimeMillis());
			if (uri == null) return null;
			if (uri.equals(mBackgroundUri)) return mBackground;
			mBackgroundUri = uri;
			
			mBackground = null;
			try {
				Point size = Util.getDisplaySize(PostItWallpaper.this);
				size.y -= mStatusBarHeight;
				Bitmap bitmap = Util.loadBitmap(PostItWallpaper.this, Uri.parse(uri), size);
				if (bitmap == null) return null;
				// TODO:crop			
				//Bitmap.createBitmap(bitmap,0,0, width, height);
				float aspect = (float)bitmap.getWidth()/(float)bitmap.getHeight();
				int w = (int) (size.y * aspect);
				int h = size.y;
				mBackground = Bitmap.createScaledBitmap(bitmap, w, h, true);
				bitmap.recycle();
			} catch (IOException e) {
				Log.e(TAG,"setBackgroundUri:"+e);
			}
			return mBackground;
		}

		public void update() {
			onVisibilityChanged(mVisible);
		}
		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			if (visible) {
				mPostItViewManager.update();
				mPostItViewManager.show(mIsRaisePostIt);
				drawFrame();
			} else {
				mPostItViewManager.show(false);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			drawFrame();
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xStep, float yStep, int xPixels, int yPixels) {
			drawFrame();
		}

		@Override
		public void onTouchEvent(MotionEvent ev) {
			// Log.e("DEBIG","onTouchEvent:"+ev);
		}

		@Override
		public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
			if (mSettings.isDoubleTapCtrlAction()) {
				if (mLastTapTime > 0) {
					long time = System.currentTimeMillis() - mLastTapTime;
					if (time < 400) {
						mCtrlPanel.toggle();
					}
				}
				mLastTapTime = System.currentTimeMillis();
			} else {
				mCtrlPanel.toggle();
			}

			return super.onCommand(action, x, y, z, extras, resultRequested);
		}


		void drawFrame() {
			final SurfaceHolder holder = getSurfaceHolder();

			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();
				if (canvas != null) {
					canvas.drawColor(0, Mode.CLEAR);
					canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), 255, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
					canvas.translate(0.0F, (float) mStatusBarHeight);
					Bitmap bgBitmap = getBackgroundBitmap();
					if (bgBitmap != null) {
						int rx = (bgBitmap.getWidth() - canvas.getWidth())/2;
						canvas.drawBitmap(bgBitmap, -rx, 0, mPaint);
					}
					canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), ALPHA, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
					if (!mIsRaisePostIt) {
						List<PostItView> list = mPostItViewManager.getPostItViewList();
						for (PostItView view : list) {
							drawPostItView(canvas, view);
						}
					}
				}
			} finally {
				if (canvas != null) holder.unlockCanvasAndPost(canvas);
			}
		}

		private void drawPostItView(Canvas canvas, PostItView postItView) {
			PostItData data = postItView.getPostItData();
			if (data.isEnabled()) {
				int save = canvas.save();
				canvas.translate((float) data.getPosX(), (float) data.getPosY());
				postItView.draw(canvas);
				canvas.restoreToCount(save);
			}
		}

	}
}
