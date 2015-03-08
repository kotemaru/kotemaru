package org.kotemaru.android.fw.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class LoopHScrollView extends HorizontalScrollView {
	private InnerLayout mInnerLayout;
	private AnimeManager mAnimeManager;
	private GestureDetector mGestureDetector;
	private CsutomOnGestureListener mOnGestureListener = new CsutomOnGestureListener();

	public LoopHScrollView(Context context) {
		this(context, null);
	}
	public LoopHScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public LoopHScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mAnimeManager = new AnimeManager();
		mGestureDetector = new GestureDetector(context, mOnGestureListener);

		mInnerLayout = new InnerLayout(context);
		mInnerLayout.setOrientation(LinearLayout.HORIZONTAL);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		mInnerLayout.setLayoutParams(params);
		this.addView(mInnerLayout);
	}

	public void setChildViewGroup(ViewGroup child) {
		CloneView clone1 = new CloneView(getContext(), child);
		CloneView clone2 = new CloneView(getContext(), child);

		LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		mInnerLayout.removeAllViews();
		mInnerLayout.addView(clone1, childParams);
		mInnerLayout.addView(child, childParams);
		mInnerLayout.addView(clone2, childParams);
	}

	public class InnerLayout extends LinearLayout {
		public InnerLayout(Context context) {
			super(context);
		}
		@Override
		public void onWindowFocusChanged(boolean hasFocus) {
			super.onWindowFocusChanged(hasFocus);
			resizing();
		}
		@Override
		public void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			resizing();
		}
		private void resizing() {
			if (getChildCount() == 3) {
				View clone1 = this.getChildAt(0);
				View origin = this.getChildAt(1);
				View clone2 = this.getChildAt(2);
				int width = origin.getMeasuredWidth();
				if (width < LoopHScrollView.this.getMeasuredWidth()) {
					// TODO: Can not scroll small child view.
					mInnerLayout.removeView(clone1);
					mInnerLayout.removeView(clone2);
				} else {
					clone1.setMinimumWidth(width);
					clone2.setMinimumWidth(width);
				}
			}
		}
	}

	public static class CloneView extends ViewGroup {
		ViewGroup mOrigin;

		public CloneView(Context context, ViewGroup origin) {
			super(context);
			mOrigin = origin;
		}
		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
		}
		@Override
		protected void dispatchDraw(Canvas canvas) {
			mOrigin.draw(canvas);
		}
		@Override
		public boolean dispatchKeyEvent(KeyEvent event) {
			return mOrigin.dispatchKeyEvent(event);
		}
		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			boolean result = mOrigin.dispatchTouchEvent(ev);
			this.invalidate();
			return result;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) mOnGestureListener.onDown(ev);
		return super.onInterceptTouchEvent(ev);
	}
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return mGestureDetector.onTouchEvent(ev);
	}

	private class CsutomOnGestureListener extends GestureDetector.SimpleOnGestureListener {
		boolean mIsFirstScroll = true; // Note: 子要素のACTION_DOWNが届かず誤動作するので初回を無視する。

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (!mIsFirstScroll) mAnimeManager.startScroll(velocityX);
			mIsFirstScroll = false;
			return true;
		}
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			mAnimeManager.startFling(-velocityX / 50);
			return true;
		}
		@Override
		public boolean onDown(MotionEvent ev) {
			mAnimeManager.stopFling();
			mIsFirstScroll = true;
			return true;
		}
	};

	private class AnimeManager {
		private static final int INTERVAL = 50; // ms
		private static final float ATTENUAION_RATE = 0.90F;
		private float mDelta = 0;
		private final Runnable mUpdateRunner = new Runnable() {
			@Override
			public void run() {
				onUpdate();
			}
		};
		public void update() {
			Handler handler = getHandler();
			if (handler == null) return;
			handler.postDelayed(mUpdateRunner, INTERVAL);
		}

		public void startScroll(float delta) {
			stopFling();
			if (!loopScrollPosition()) {
				scrollBy((int) delta, 0);
			}
		}
		public void startFling(float delta) {
			mDelta = delta;
			update();
		}
		public void stopFling() {
			mDelta = 0.0F;
		}
		private void onUpdate() {
			if (Math.abs(mDelta) > 1.0F) {
				scrollBy((int) mDelta, 0);
				mDelta = mDelta * ATTENUAION_RATE;
				loopScrollPosition();
				update();
			} else {
				stopFling();
			}
		}
		private boolean loopScrollPosition() {
			int curX = computeHorizontalScrollOffset();
			int unitWidth = computeHorizontalScrollRange() / 3;
			if (curX > unitWidth * 1.8F) {
				scrollTo(curX - unitWidth, 0);
				return true;
			} else if (curX < unitWidth * 0.2F) {
				scrollTo(curX + unitWidth, 0);
				return true;
			}
			return false;
		}
	}
}
