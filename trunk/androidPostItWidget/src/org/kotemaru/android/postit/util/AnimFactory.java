package org.kotemaru.android.postit.util;

import org.kotemaru.android.postit.R;
import org.kotemaru.android.postit.R.anim;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;

/**
 * アニメーションの管理。
 * @author kotemaru.org
 */
public class AnimFactory {

	public static abstract class AnimEndListener implements Animation.AnimationListener {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
	}

	/**
	 * 付箋トレイ表示用アニメーション。
	 * @param context
	 * @param listener
	 * @return
	 */
	public static Animation getFedeIn(Context context, AnimEndListener listener) {
		Animation anim = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.fade_in);
		anim.setAnimationListener(listener);
		return anim;
	}
	/**
	 * 付箋トレイ非表示用アニメーション。
	 * @param context
	 * @param listener
	 * @return
	 */
	public static Animation getFedeOut(Context context, AnimEndListener listener) {
		Animation anim = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.fade_out);
		anim.setAnimationListener(listener);
		return anim;
	}
	/**
	 * 付箋削除用アニメーション。ゴミ箱に吸い込まれるように小さくなっていく。
	 * @param context
	 * @param pivotX 収束座標。ゴミ箱中心。
	 * @param pivotY 収束座標。ゴミ箱中心。
	 * @param listener
	 * @return
	 */
	public static Animation getRemove(Context context, float pivotX, float pivotY, AnimEndListener listener) {
		// Animation anim = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.remove);
		Animation anim = new ScaleAnimation(1.0F, 0.0F, 1.0F, 0.0F, pivotX, pivotY);
		anim.setDuration(300);
		anim.setAnimationListener(listener);
		return anim;
	}

}
