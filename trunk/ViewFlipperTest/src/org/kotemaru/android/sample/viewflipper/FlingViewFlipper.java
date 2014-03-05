package org.kotemaru.android.sample.viewflipper;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ViewFlipper;

public class FlingViewFlipper extends ViewFlipper {
	private static final String TAG = "FlingViewFlipper";
	private final Animation right_in_trans_anim = createAnim(1, 0);
	private final Animation right_out_trans_anim = createAnim(0, 1);
	private final Animation left_in_trans_anim = createAnim(-1, 0);
	private final Animation left_out_trans_anim = createAnim(0, -1);
	private GestureDetector gestureDetector;

	public FlingViewFlipper(Context context, AttributeSet attrSet) {
		super(context, attrSet);
		this.gestureDetector = new GestureDetector(context, onGestureListener);
		setFlipInterval(0);
	}
	public void setViews(View[] views) {
		removeAllViews();
		for (int i = 0; i < views.length; i++) {
			addView(views[i]);
		}
	}

	public boolean onTouchEvent(MotionEvent ev) {
		gestureDetector.onTouchEvent(ev);
		return false;
	}

	private OnGestureListener onGestureListener = new SimpleOnGestureListener() {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Log.d(TAG, "onFling:" + velocityX);
			if (velocityX < -300) {
				setOutAnimation(left_out_trans_anim);
				setInAnimation(right_in_trans_anim);
				showNext();
				return true;
			} else if (velocityX > 300) {
				setOutAnimation(right_out_trans_anim);
				setInAnimation(left_in_trans_anim);
				showPrevious();
				return true;
			}
			return false;
		}
	};

	private static Animation createAnim(float startX,float entX) {
		Animation anim = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT, startX, Animation.RELATIVE_TO_PARENT, entX,
			Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0
		);
		anim.setDuration(300);
		anim.setStartOffset(0);
		return anim;
	}
}