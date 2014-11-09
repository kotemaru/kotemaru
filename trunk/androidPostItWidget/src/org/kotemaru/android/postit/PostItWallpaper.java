package org.kotemaru.android.postit;

import java.util.List;

import org.kotemaru.android.postit.data.PostItData;
import org.kotemaru.android.postit.data.PostItDataProvider;
import org.kotemaru.android.postit.data.Settings;
import org.kotemaru.android.postit.util.Launcher;
import org.kotemaru.android.postit.util.Util;
import org.kotemaru.android.postit.widget.PostItTray;
import org.kotemaru.android.postit.widget.PostItView;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Live壁紙のサービス。
 * <li>アプリのメインとなるContext。
 * <li>付箋と付箋トレイの表示制御を行う。
 * @author kotemaru.org
 */
public class PostItWallpaper extends WallpaperService {
	private static final String TAG = PostItWallpaper.class.getSimpleName();

	/** ステータスバーの高さ。サイズ補正用 */
	private int mStatusBarHeight;
	/** 壁紙の描画領域の矩形 */
	private Rect mBounds;
	/** 付箋の表示場所。true=上位レイヤ、false=壁紙。 */
	private boolean mIsRaisePostIt = true;

	private PostItTray mPostItTray;
	private PostItViewManager mPostItViewManager;
	private Settings mSettings;
	private DrawEngine mEngine;

	@Override
	public void onCreate() {
		super.onCreate();
		mStatusBarHeight = Util.getStatusBarHeight(this);
		Point size = Util.getDisplaySize(this);
		mBounds = new Rect(0, 0, size.x, size.y - mStatusBarHeight);

		mPostItTray = PostItTray.create(this);
		mPostItViewManager = new PostItViewManager(this);
		mSettings = new Settings(this).load();

		mPostItTray.hide();
	}

	/**
	 * インテントの受信。
	 * <li>アプリ設定の更新処理。
	 * <li>- OnSharedPreferenceChangeListenerがうまく動かないのでここで受けている。
	 */
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

	/**
	 * 付箋データの新規作成。
	 * <li>DBに付箋を新規作成してViewも作成して返す。
	 * @param data 初期データ。IDは無視。
	 * @return 付箋View
	 */
	public PostItView createPostIt(PostItData data) {
		long id = PostItDataProvider.createPostItData(this, data);
		mPostItViewManager.syncPostItDataProvider();
		PostItView view = mPostItViewManager.getPostItViewFromId(id);
		return view;
	}

	/**
	 * 壁紙の再描画。
	 */
	public void update() {
		if (mEngine == null) return;
		mEngine.update();
	}

	/**
	 * 付箋の表示場所の設定。
	 * @param b true=上位レイヤ、false=壁紙
	 */
	public void setRaisePostIt(boolean b) {
		this.mIsRaisePostIt = b;
		if (mEngine == null) return;
		mEngine.update();
	}
	public boolean isRaisePostIt() {
		return this.mIsRaisePostIt;
	}

	public Rect getBounds() {
		return mBounds;
	}
	public int getStatusBarHeight() {
		return mStatusBarHeight;
	}

	public PostItTray getPostItTray() {
		return mPostItTray;
	}

	public PostItViewManager getPostItViewManager() {
		return mPostItViewManager;
	}

	public boolean isVisible() {
		if (mEngine == null) return false;
		return mEngine.mVisible;
	}

	/**
	 * 壁紙の描画エンジン。
	 * <li>壁紙画像を描画し、付箋が壁紙表示なら付箋の描画もする。
	 * <li>(重要)壁紙非表示になったら上位レイヤの付箋も非表示にする制御を行う。
	 * <li>付箋トレイの表示 on/off をする壁紙へのタップアクションを制御する。
	 */
	private class DrawEngine extends Engine {
		private static final int ALPHA = (int) (255 * 0.7);

		private final Paint mPaint = new Paint();
		/** 現在の表示状態 */
		private boolean mVisible;
		/** 現在の背景URI */
		private String mBackgroundUri = null;
		/** 現在の背景Bitmap */
		private Bitmap mBackground = null;
		/** ダブルタップ検知用タイムスタンプ */
		private long mLastTapTime = -1;

		DrawEngine() {
		}

		public void update() {
			onVisibilityChanged(mVisible);
		}

		/**
		 * 壁紙の表示/非表示の通知を受ける。
		 * <li>このアプリがLive壁紙である理由がここにある。
		 * <li>通常のアプリはホームアプリがフロントになった事をイベントで知ることができない。
		 * <li>Live壁紙アプリは唯一このメソッドで知ることができる。
		 * <li>ここでは壁紙が非表示になると上位レイヤの付箋も非表示にすることで他のアプリの上に付箋を出さないようにしている。
		 */
		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			if (visible) {
				mPostItViewManager.syncPostItDataProvider();
				mPostItViewManager.show(mIsRaisePostIt);
				drawFrame();
			} else {
				mPostItViewManager.show(false);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			mBackgroundUri = null;
			drawFrame();
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xStep, float yStep, int xPixels, int yPixels) {
			mBackgroundUri = null;
			drawFrame();
		}

		/**
		 * タップされたら付箋トレイを表示する。
		 * <li>4.4.4ではバグが有りホームアプリのアイコンをタップしてもここが呼ばれてしまう。
		 * <li>仕方が無いのでダブルタップにも対応している。
		 */
		@Override
		public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
			if (mSettings.isDoubleTapCtrlAction()) {
				if (mLastTapTime > 0) {
					long time = System.currentTimeMillis() - mLastTapTime;
					if (time < 400) {
						mPostItTray.toggle();
					}
				}
				mLastTapTime = System.currentTimeMillis();
			} else {
				mPostItTray.toggle();
			}

			return super.onCommand(action, x, y, z, extras, resultRequested);
		}

		/**
		 * 壁紙の描画。
		 * <li>壁紙はステータスバーとナビゲーションバーを含まない領域に描画する。
		 * <li>付箋が壁紙描画になっていれば付箋を半透明で上書きする。
		 */
		private void drawFrame() {
			final SurfaceHolder holder = getSurfaceHolder();

			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();
				if (canvas != null) {
					// 初期化
					canvas.drawColor(0, Mode.CLEAR);
					canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), 255, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
					canvas.translate(0.0F, (float) mStatusBarHeight);
					// 背景画像の描画
					Bitmap bgBitmap = getBackgroundBitmap();
					if (bgBitmap != null) {
						int rx = (bgBitmap.getWidth() - canvas.getWidth()) / 2;
						canvas.drawBitmap(bgBitmap, -rx, 0, mPaint);
					}
					// 付箋の描画。半透明。
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

		/**
		 * 壁紙へ付箋を描画する。
		 * @param canvas 壁紙用Canvas
		 * @param postItView 付箋
		 */
		private void drawPostItView(Canvas canvas, PostItView postItView) {
			PostItData data = postItView.getPostItData();
			if (data.isEnabled()) {
				int save = canvas.save();
				canvas.translate((float) data.getPosX(), (float) data.getPosY());
				postItView.draw(canvas);
				canvas.restoreToCount(save);
			}
		}

		/**
		 * 背景画像の取得。
		 * <li>背景は時間で変わるので背景URIが変更になったら画像を読み込み直す。
		 * <li>画像はアスペクト比をそのままでかつ画面サイズピッタリになるように補正したものを返す。
		 * <li>背景画像未設定の場合はシステムのデフォルト。
		 * @return 背景画像
		 */
		private Bitmap getBackgroundBitmap() {
			String uri = mSettings.getBackgroundUri(System.currentTimeMillis());
			if (uri != null && uri.equals(mBackgroundUri)) return mBackground;
			mBackgroundUri = uri;

			mBackground = null;
			try {
				Point size = Util.getDisplaySize(PostItWallpaper.this);
				size.y -= mStatusBarHeight;
				Bitmap srcBitmap;
				if (uri != null) {
					srcBitmap = Util.loadBitmap(PostItWallpaper.this, Uri.parse(uri), size);
				} else {
					srcBitmap = getSystemDefaultWallpaper();
				}
				if (srcBitmap == null) return null;
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
				mBackground = Bitmap.createScaledBitmap(cropBitmap, size.x, size.y, true);
				cropBitmap.recycle();
				srcBitmap.recycle();
			} catch (Exception e) {
				Log.e(TAG, "setBackgroundUri:" + e);
			}
			return mBackground;
		}
		private Bitmap getSystemDefaultWallpaper() {
			int resId = Resources.getSystem().getIdentifier("default_wallpaper", "drawable", "android");
			Drawable drawable = getResources().getDrawable(resId);
			return ((BitmapDrawable) drawable).getBitmap();
		}
	}
}
