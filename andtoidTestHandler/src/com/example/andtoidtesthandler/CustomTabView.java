package com.example.andtoidtesthandler;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomTabView extends HorizontalScrollView {
	public static final String TAG = CustomTabView.class.getSimpleName();

	private GestureDetector mGestureDetector;
	private LinearLayout mLayout;
	private TextView[][] mTextViews = new TextView[3][];
	private int mSelectedIndex = 0;
	private AnimeManager mAnimeManager = new AnimeManager();
	private OnClickListener tagOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			selectTab(v);
		}
	};

	public CustomTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mGestureDetector = new GestureDetector(context, onGestureListener);

	}

	public void setTabs(String[] tabs) {
		this.removeAllViews();

		mLayout = new InnerLayout(getContext());
		mLayout.setOrientation(LinearLayout.HORIZONTAL);
		for (int j = 0; j < 3; j++) {
			mTextViews[j] = new TextView[tabs.length];
			for (int i = 0; i < tabs.length; i++) {
				TextView view = new TextView(this.getContext());
				view.setText(tabs[i]);
				view.setPadding(20, 0, 20, 0);
				view.setOnClickListener(tagOnClickListener);
				mLayout.addView(view);
				mTextViews[j][i] = view;
			}
		}
		this.addView(mLayout);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Log.d(TAG, "onWindowFocusChanged:" + hasFocus);
		super.onWindowFocusChanged(hasFocus);
		if (this.getWidth() > mLayout.getWidth() / 3 + 50) {
			int gap = this.getWidth() - mLayout.getWidth() / 3;
			int margin = (gap / mTextViews[0].length - 1) / 2 + 10;

			for (int j = 0; j < 3; j++) {
				for (int i = 0; i < mTextViews[j].length; i++) {
					LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTextViews[j][i].getLayoutParams();
					params.setMargins(margin, 0, margin, 0);
				}
			}
		}
	}
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		int viewWidth = mLayout.getWidth() / 3;
		int maxPosition = viewWidth * 2;
		int minPosition = viewWidth - this.getWidth();
		// Log.e("DEBUG", "onScrollChanged:"+l+","+viewWidth+","+maxPosition);

		if (l > maxPosition) {
			scrollTo(l - viewWidth, 0);
		} else if (l < minPosition) {
			scrollTo(viewWidth + l, 0);
		}
	}

	public void selectTab(int idx) {
		selectTab(mTextViews[1][idx]);
	}
	public void selectTab(View view) {
		int index = 0;
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < mTextViews[j].length; i++) {
				if (view == mTextViews[j][i]) {
					index = i;
				}
			}
		}
		mSelectedIndex = index;
		int dest = view.getLeft() + view.getWidth() / 2 - this.getWidth() / 2;
		mAnimeManager.startDest(dest);
	}
	public void refreshSelected() {
		// Log.e("DEBUG", "onProgressAnime:"+percent);
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < mTextViews[j].length; i++) {
				mTextViews[j][i].setBackgroundColor((i == mSelectedIndex) ? Color.RED : Color.WHITE);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Log.d(TAG, "onTouchEvent:" + ev);
		mGestureDetector.onTouchEvent(ev);
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = (ev.getAction() & MotionEvent.ACTION_MASK);
		if (action == MotionEvent.ACTION_DOWN) {
			onGestureListener.onDown(ev);
		}
		return super.onInterceptTouchEvent(ev);
	}

	private OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			// Log.d(TAG, "onScroll:" + velocityX);
			final int x = (int) velocityX;
			getHandler().postDelayed(new Runnable() {
				public void run() {
					scrollBy(x, 0);
				}
			}, 1);
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
	};

	private class InnerLayout extends LinearLayout {
		public InnerLayout(Context context) {
			super(context);
			super.setOrientation(LinearLayout.HORIZONTAL);
		}
		@Override
		public void onSizeChanged(int w, int h, int oldw, int oldh) {
			Log.d(TAG, "onSizeChanged:" + w);
			super.onSizeChanged(w, h, oldw, oldh);
			getHandler().postDelayed(new Runnable() {
				@Override
				public void run() {
					selectTab(mSelectedIndex);
				};
			}, 1);
		}
	}

	private class AnimeManager {
		private static final int ANIME_INTERVAL = 50; // ms
		private static final int ANIME_NONE = 0;
		private static final int ANIME_FLING = 1;
		private static final int ANIME_DEST = 2;
		private int mAnimeMode = ANIME_NONE;
		private float mDelta = 0;
		private int mRemainOrg = 0;
		private int mRemain = 0;

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
		private void startDest(int dest) {
			mAnimeMode = ANIME_DEST;
			mRemainOrg = mRemain = dest - computeHorizontalScrollOffset();
			getHandler().postDelayed(onUpdateHandler, ANIME_INTERVAL);
		}
		private void stop() {
			mAnimeMode = ANIME_NONE;
		}

		private void onUpdate() {
			if (mAnimeMode == ANIME_FLING) {
				if (Math.abs(mDelta) > 1.0F) {
					scrollBy((int) mDelta, 0);
					mDelta = mDelta * 0.95F;
					getHandler().postDelayed(onUpdateHandler, ANIME_INTERVAL);
				} else {
					onProgressAnime(1.0F);
					mAnimeMode = ANIME_NONE;
				}
			} else if (mAnimeMode == ANIME_DEST) {
				int delta = mRemain / 5;
				if (Math.abs(delta) > 1.0F) {
					scrollBy(delta, 0);
					mRemain -= delta;
					getHandler().postDelayed(onUpdateHandler, ANIME_INTERVAL);
					onProgressAnime(1.0F - ((float) mRemain / mRemainOrg));
				} else {
					// scrollTo(mDestPosition, 0);
					onProgressAnime(1.0F);
					mAnimeMode = ANIME_NONE;
				}
			}
		}

		private void onProgressAnime(float percent) {
			// Log.e("DEBUG", "onProgressAnime:"+percent);
			if (percent < 0.9F) return;
			refreshSelected();
		}
	}
}
