package org.kotemaru.android.postit;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;

public class AnimFactory {

	public static abstract class AnimEndListener implements Animation.AnimationListener {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
	}

	public static Animation getFedeIn(Context context, AnimEndListener listener) {
		Animation anim = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.fade_in);
		anim.setAnimationListener(listener);
		return anim;
	}
	public static Animation getFedeOut(Context context, AnimEndListener listener) {
		Animation anim = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.fade_out);
		anim.setAnimationListener(listener);
		return anim;
	}
	public static Animation getRemove(Context context, float pivotX, float pivotY, AnimEndListener listener) {
		// Animation anim = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.remove);
		Animation anim = new ScaleAnimation(1.0F, 0.0F, 1.0F, 0.0F, pivotX, pivotY);
		anim.setDuration(300);
		anim.setAnimationListener(listener);
		return anim;
	}

}
