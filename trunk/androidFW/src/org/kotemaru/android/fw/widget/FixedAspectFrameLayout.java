package org.kotemaru.android.fw.widget;

import org.kotemaru.android.fw.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 縦横比固定FrameLayout。
 * <li>上位のLayoutによって動的に決定された幅(or高さ)に応じて縦横比が一定になるように高さ(or幅)を決定する。
 * <li>縦横比はカスタム属性 aspectRate に float (幅÷高さ) で与る。
 * <li>使用例：<xmp>
 * <org.kotemaru.android.fw.widget.FixedAspectFrameLayout
 * xmlns:custom="http://schemas.android.com/apk/res/org.kotemaru.android.fw"
 * android:layout_width="match_parent"
 * android:layout_height="wrap_content"
 * custom:aspectRate="2.35" >
 * </org.kotemaru.android.fw.widget.FixedAspectFrameLayout>
 * </xmp>
 * <li>参考：http://stackoverflow.com/a/13846628/804479
 * 
 * @author kotemaru.org
 */
public class FixedAspectFrameLayout extends FrameLayout {
	private float mAspectRate;

	public FixedAspectFrameLayout(Context context) {
		this(context, null);
	}
	public FixedAspectFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public FixedAspectFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.fw_FixedAspectFrameLayout);
		this.mAspectRate = a.getFloat(R.styleable.fw_FixedAspectFrameLayout_aspectRate, 1.0F);
		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
			int h = (int) (MeasureSpec.getSize(widthMeasureSpec) / mAspectRate);
			super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
		} else if (widthMode != MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
			int w = (int) (MeasureSpec.getSize(heightMeasureSpec) * mAspectRate);
			super.onMeasure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), heightMeasureSpec);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}
