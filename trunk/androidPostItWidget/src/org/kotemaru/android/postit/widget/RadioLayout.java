package org.kotemaru.android.postit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class RadioLayout extends LinearLayout {

	public RadioLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void check(int resId) {
		RadioItem item = (RadioItem) findViewById(resId);
		if (item == null) return;
		onSelect(item);
	}
	public int getCheckedRadioButtonId() {
		RadioItem item = getSelectedItem();
		if (item == null) return -1;
		return item.getId();
	}
	public void onSelect(RadioItem item) {
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.setSelected(false);
		}
		item.setSelected(true);
	}
	public RadioItem getSelectedItem() {
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child.isSelected()) return (RadioItem) child;
		}
		return null;
	}

	public String getValue() {
		RadioItem item = getSelectedItem();
		if (item == null) return null;
		return item.getValue();
	}

	public int getValueInt(int defo) {
		RadioItem item = getSelectedItem();
		if (item == null) return defo;
		return item.getValueInt();
	}

	public float getValueFloat(float defo) {
		RadioItem item = getSelectedItem();
		if (item == null) return defo;
		return item.getValueFloat();
	}

	public boolean isValueBool(boolean defo) {
		RadioItem item = getSelectedItem();
		if (item == null) return defo;
		return item.isValueBool();
	}

	private interface ValueMatcher {
		boolean isMatch(RadioItem item);
	}

	public void setValue(final String value) {
		if (value == null) return;
		setValueGeneric(new ValueMatcher() {
			public boolean isMatch(RadioItem item) {
				return value.equals(item.getValue());
			}
		});
	}
	public void setValueInt(final int value) {
		setValueGeneric(new ValueMatcher() {
			public boolean isMatch(RadioItem item) {
				return value == item.getValueInt();
			}
		});
	}
	public void setValueFloat(final float value) {
		setValueGeneric(new ValueMatcher() {
			public boolean isMatch(RadioItem item) {
				return value == item.getValueFloat();
			}
		});
	}
	public void setValueBool(final boolean value) {
		setValueGeneric(new ValueMatcher() {
			public boolean isMatch(RadioItem item) {
				return value == item.isValueBool();
			}
		});
	}

	private void setValueGeneric(ValueMatcher matcher) {
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child instanceof RadioItem) {
				RadioItem item = (RadioItem) child;
				if (matcher.isMatch(item)) {
					onSelect(item);
					return;
				}
			}
		}
	}

}
