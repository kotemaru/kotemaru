package org.kotemaru.android.fw.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class LoopHScrollView extends HorizontalScrollView {
	private InnerLayout mInnerLayout;
	private GestureDetector mGestureDetector;
	private AnimeManager mAnimeManager;

	public LoopHScrollView(Context context) {
		this(context, null);
	}
	public LoopHScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public LoopHScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mGestureDetector = new GestureDetector(context, onGestureListener);
		mAnimeManager = new AnimeManager(this);

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
				CloneView clone1 = (CloneView) this.getChildAt(0);
				ViewGroup origin = (ViewGroup) this.getChildAt(1);
				CloneView clone2 = (CloneView) this.getChildAt(2);
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
			this.invalidate();
			return mOrigin.dispatchTouchEvent(ev);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return mGestureDetector.onTouchEvent(ev);
	}

	private OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			mAnimeManager.startScroll(velocityX);
			return true;
		}
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			mAnimeManager.startFling(-velocityX / 50);
			return true;
		}
		@Override
		public boolean onDown(MotionEvent e) {
			mAnimeManager.stopFling();
			return true;
		}
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return false;
		}
	};

	private static class AnimeManager {
		private static final int INTERVAL = 50; // ms
		private static final float ATTENUAION_RATE = 0.90F;
		private float mDelta = 0;
		private LoopHScrollView mView;

		public AnimeManager(LoopHScrollView self) {
			mView = self;
		}

		private final Runnable onUpdateHandler = new Runnable() {
			public void run() {
				onUpdate();
			}
		};

		public void startScroll(float delta) {
			stopFling();
			if (!loopScrollPosition()) {
				mView.scrollBy((int) delta, 0);
			}
		}
		public void startFling(float delta) {
			mDelta = delta;
			mView.getHandler().postDelayed(onUpdateHandler, INTERVAL);
		}
		public void stopFling() {
			mDelta = 0.0F;
		}

		private void onUpdate() {
			if (Math.abs(mDelta) > 1.0F) {
				mView.scrollBy((int) mDelta, 0);
				mDelta = mDelta * ATTENUAION_RATE;
				loopScrollPosition();
				mView.getHandler().postDelayed(onUpdateHandler, INTERVAL);
			} else {
				stopFling();
			}
		}

		private boolean loopScrollPosition() {
			int curX = mView.computeHorizontalScrollOffset();
			int unitWith = mView.computeHorizontalScrollRange() / 3;
			if (curX > unitWith * 1.8F) {
				mView.scrollTo((int) (unitWith * 0.8F), 0);
				return true;
			} else if (curX < unitWith * 0.2F) {
				mView.scrollTo((int) (unitWith * 1.2F), 0);
				return true;
			}
			return false;
		}
	}
}
