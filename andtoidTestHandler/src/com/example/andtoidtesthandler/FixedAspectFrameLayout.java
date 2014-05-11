package com.example.andtoidtesthandler;

//Based on http://stackoverflow.com/a/13846628/804479

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class FixedAspectFrameLayout extends FrameLayout {
	private float aspectRate;

	public FixedAspectFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FixedAspectFrameLayout);
		this.aspectRate = a.getFloat(R.styleable.FixedAspectFrameLayout_aspectRate, 1.0F);
		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		if (heightMode != MeasureSpec.EXACTLY && widthMode != MeasureSpec.EXACTLY) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} else if (widthMode == MeasureSpec.EXACTLY) {
			int h = (int) (MeasureSpec.getSize(widthMeasureSpec) / aspectRate);
			super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
		} else if (heightMode == MeasureSpec.EXACTLY) {
			int w = (int) (MeasureSpec.getSize(heightMeasureSpec) * aspectRate);
			super.onMeasure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), heightMeasureSpec);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}