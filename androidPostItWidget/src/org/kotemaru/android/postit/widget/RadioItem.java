package org.kotemaru.android.postit.widget;

import org.kotemaru.android.postit.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.RadioButton;

public class RadioItem extends FrameLayout {
	private RadioButton mRadioButton;
	private String mValue;
	private int mValueInt;
	private float mValueFloat;
	private boolean mValueBool;

	public RadioItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RadioItem, 0, 0);
		try {
			mValue = a.getString(R.styleable.RadioItem_value);
			mValueInt = a.getInteger(R.styleable.RadioItem_value_int, 0);
			mValueFloat = a.getFloat(R.styleable.RadioItem_value_float, 0.0F);
			mValueBool = a.getBoolean(R.styleable.RadioItem_value_bool, false);
		} finally {
			a.recycle();
		}
	}
	@Override
	public void onAttachedToWindow() {
		setOnClickListener(mOnClickListener);
		mRadioButton = findRadioButton();
		if (mRadioButton != null) {
			mRadioButton.setOnClickListener(mOnClickListener);
			mRadioButton.setChecked(isSelected());
		}
	}
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			selectMe();
		}
	};
	
	
	private RadioButton findRadioButton() {
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child instanceof RadioButton) return (RadioButton) child;
		}
		return null;
	}

	private void selectMe() {
		RadioLayout group = getRadioGroup();
		if (group == null) return;
		group.onSelect(this);
	}
	private RadioLayout getRadioGroup() {
		ViewParent parent = this.getParent();
		while (parent != null) {
			if (parent instanceof RadioLayout) {
				return (RadioLayout) parent;
			}
			parent = parent.getParent();
		}
		return null;
	}
	@Override
	public void setSelected(boolean b) {
		super.setSelected(b);
		if (mRadioButton != null) {
			mRadioButton.setChecked(b);
		}
	}


	public String getValue() {
		return mValue;
	}

	public int getValueInt() {
		return mValueInt;
	}

	public float getValueFloat() {
		return mValueFloat;
	}

	public boolean isValueBool() {
		return mValueBool;
	}

}
