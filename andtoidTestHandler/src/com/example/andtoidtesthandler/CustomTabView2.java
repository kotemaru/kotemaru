package com.example.andtoidtesthandler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.TextView;

public class CustomTabView2 extends TextView {
	public static final String TAG = CustomTabView2.class.getSimpleName();

	private GestureDetector mGestureDetector;
	private AnimeManager mAnimeManager = new AnimeManager();

	private int mSelectedIndex = 0;
	private String[] mTabs;
	private Paint paint = new Paint();
	private int mCenterX;
	private int mTabsWidth;
	private int mPadding = 10;
	private int mMargin = 0;
	private Rect[] mTabRect;
	private Rect[] mTabRectAbs;

	public CustomTabView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mGestureDetector = new GestureDetector(context, onGestureListener);
		Paint p = getPaint();
		p.setColor(Color.GRAY);
		p.setTypeface(Typeface.DEFAULT_BOLD);
	}

	public void setTabs(String[] tabs) {
		mTabs = tabs;
		mTabRectAbs = new Rect[tabs.length];
		for (int i = 0; i < mTabRectAbs.length; i++) {
			mTabRectAbs[i] = new Rect();
		}
		mTabRect = new Rect[tabs.length*3];
		for (int i = 0; i < mTabRect.length; i++) {
			mTabRect[i] = new Rect();
		}
		resizing();
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Log.d(TAG, "onWindowFocusChanged:" + hasFocus);
		super.onWindowFocusChanged(hasFocus);
		resizing();
	}
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.d(TAG, "onSizeChanged:" + w);
		resizing();
	}
	public void resizing() {
		if (mTabs != null || this.getWidth() == 0) {
			mTabsWidth = drawTabs(null, getPaint(), 0, 0, false);
			if (this.getWidth() > mTabsWidth) {
				mMargin += (this.getWidth() - mTabsWidth) / mTabs.length / 2;
				Log.d(TAG, "resizing:" + this.getWidth() + "," + mTabsWidth + "," + mMargin);
				mTabsWidth = drawTabs(null, getPaint(), 0, 0, false);
			}
			setCenter(mTabRectAbs[0].centerX());
		}
	}

	
	@Override
	public void onDraw(Canvas canvas) {
		int sc = canvas.save();
		canvas.drawColor(Color.WHITE, Mode.CLEAR);
		Paint p = getPaint();
		p.setColor(Color.GRAY);

		int x = this.getWidth() / 2 - mCenterX;
		drawTabs(canvas, p, x, 0, true);
		drawTabs(canvas, p, x - mTabsWidth, 1, true);
		drawTabs(canvas, p, x + mTabsWidth, 2, true);
		
		p.setColor(Color.RED);
		p.setXfermode(new PorterDuffXfermode(Mode.XOR));

		int rw = mTabRectAbs[mSelectedIndex].width()/2;
		int ox =  this.getWidth() / 2;
		canvas.drawRect(ox-rw, 0, ox+rw, 40, p);
		canvas.restoreToCount(sc);
	}

	private int drawTabs(Canvas canvas, Paint p, int x, int block, boolean isDraw) {
		FontMetrics fontMetrics = p.getFontMetrics();
		int y = (int) (fontMetrics.bottom + 20);
		for (int i = 0; i < mTabs.length; i++) {
			String text = mTabs[i];
			float textWidth = p.measureText(text);
			x += mPadding + mMargin;
			if (isDraw) {
				canvas.drawText(text, x, y, p);
				mTabRect[mTabs.length*block+i].set(x, 0, (int) (x + textWidth), y);
			} else {
				mTabRectAbs[i].set(x, 0, (int) (x + textWidth), y);
			}
			x = (int) (x + textWidth) + mPadding + mMargin;
		}
		return x;
	}
	
	public void selectTab(int idx) {
		mSelectedIndex = idx;
		setCenter(mTabRectAbs[idx].centerX());
	}

	private void setCenter(int x) {
		mCenterX = x;
		invalidate();
	}
	private void setCenterBy(int delta) {
		mCenterX += delta;
		if (mCenterX < 0) {
			mCenterX = mTabsWidth + mCenterX;
		} else if (mCenterX > mTabsWidth) {
			mCenterX = mTabsWidth - mCenterX;
		}
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Log.d(TAG, "onTouchEvent:" + ev);
		mGestureDetector.onTouchEvent(ev);
		return true;
	}

	private OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			// Log.d(TAG, "onScroll:" + velocityX);
			setCenterBy((int) velocityX);
			return true;
		}
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Log.d(TAG, "onFling:" + velocityX);
			mAnimeManager.startFling(-velocityX / 100);
			return false;
		}
		@Override
		public boolean onDown(MotionEvent e) {
			Log.d(TAG, "onDown:" + e);
			mAnimeManager.stop();
			return false;
		}
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Log.d(TAG, "onSingleTapConfirmed:" + e);
			for (int i = 0; i < mTabRect.length; i++) {
				if (mTabRect[i].left < e.getX() && e.getX() < mTabRect[i].right) {
					//Rect rect = mTabRectAbs[i % mTabs.length];
					Rect rect = mTabRect[i];
					int x = rect.centerX();
					mSelectedIndex = i % mTabs.length;
					Log.e("DEBUG",mTabs[i % mTabs.length]+":"+x);
					mAnimeManager.startDest(mTabRect[i].centerX(), mTabRectAbs[i % mTabs.length].centerX());
				}
			}
			return false;
		}
	};

	private class AnimeManager {
		private static final int ANIME_INTERVAL = 50; // ms
		private static final int ANIME_NONE = 0;
		private static final int ANIME_FLING = 1;
		private static final int ANIME_DEST = 2;
		private int mAnimeMode = ANIME_NONE;
		private float mDelta = 0;
		private int mRemainOrg = 0;
		private int mRemain = 0;
		private int mDest = 0;

		private final Runnable onUpdateHandler = new Runnable() {
			public void run() {
				onUpdate();
			}
		};

		private void startFling(float delta) {
			mAnimeMode = ANIME_FLING;
			mDelta = delta;
			getHandler().postDelayed(onUpdateHandler, ANIME_INTERVAL);
		}
		private void startDest(int dest, int dest2) {
			mAnimeMode = ANIME_DEST;
			mRemainOrg = mRemain = dest - CustomTabView2.this.getWidth() / 2;
			mDest = dest2;
			getHandler().postDelayed(onUpdateHandler, ANIME_INTERVAL);
		}
		private void stop() {
			mAnimeMode = ANIME_NONE;
		}

		private void onUpdate() {
			if (mAnimeMode == ANIME_FLING) {
				if (Math.abs(mDelta) > 1.0F) {
					setCenterBy((int) mDelta);
					mDelta = mDelta * 0.90F;
					getHandler().postDelayed(onUpdateHandler, ANIME_INTERVAL);
				} else {
					onProgressAnime(1.0F);
					mAnimeMode = ANIME_NONE;
				}
			} else if (mAnimeMode == ANIME_DEST) {
				int delta = mRemain / 5;
				if (Math.abs(delta) > 1.0F) {
					delta += (delta>0?2:-2);
					setCenterBy(delta);
					mRemain -= delta;
					getHandler().postDelayed(onUpdateHandler, ANIME_INTERVAL);
					onProgressAnime(1.0F - ((float) mRemain / mRemainOrg));
				} else {
					setCenter(mDest);
					onProgressAnime(1.0F);
					mAnimeMode = ANIME_NONE;
				}
			}
		}

		private void onProgressAnime(float percent) {
			// Log.e("DEBUG", "onProgressAnime:"+percent);
			if (percent < 0.9F) return;
			// refreshSelected();
		}
	}

}
