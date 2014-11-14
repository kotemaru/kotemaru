package org.kotemaru.android.postit.widget;

import org.kotemaru.android.postit.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.RadioButton;

/**
 * カスタムのラジオボタン。
 * <li>RadioLayout の子要素で有ることが必須。
 * <li>内包するViewがそのまま選択可能なボタンとなる。
 * <li>選択されると selected=true になるので適当に背景を設定して置く。
 * <li>XML定義のサンプルは RadioLayout 参照。
 * <li>カスタム属性として value を持つ。使い方はアプリ依存。
 * <li>RadioButtonが内包されている場合は選択状態と checked を同期させる。
 * @author kotemaru.org
 */

public class RadioItem extends FrameLayout {
	private RadioButton mRadioButton;
	private String mValue;
	private int mValueInt;
	private float mValueFloat;
	private boolean mValueBool;

	public RadioItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RadioItem, 0, 0);
		try {
			mValue = typedArray.getString(R.styleable.RadioItem_value);
			mValueInt = typedArray.getInteger(R.styleable.RadioItem_value_int, 0);
			mValueFloat = typedArray.getFloat(R.styleable.RadioItem_value_float, 0.0F);
			mValueBool = typedArray.getBoolean(R.styleable.RadioItem_value_bool, false);
		} finally {
			typedArray.recycle();
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
	
	/**
	 * 子要素の標準Radioボタン検索。ネストはしない。
	 * @return 標準Radioボタン。なければnull。
	 */
	private RadioButton findRadioButton() {
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child instanceof RadioButton) return (RadioButton) child;
		}
		return null;
	}

	/**
	 * クリックリスナ。
	 * <li>タップされたら自分を選択するだけ。
	 */
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			selectMe();
		}
	};

	/**
	 * 自分を選択する。グループ内の他の項目は選択解除される。
	 */
	private void selectMe() {
		RadioLayout group = getRadioGroup();
		if (group == null) return;
		group.onSelect(this);
	}
	
	/**
	 * 自分の所属するグループの取得。
	 * @return
	 */
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
	
	/**
	 * 子要素の標準Radioボタンがあれば選択状態を変更する。
	 * @param b
	 */
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
