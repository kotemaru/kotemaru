package org.kotemaru.android.postit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * カスタムのRadioグループ。
 * <li>子要素に RadioItem を持つ。
 * <li>RadioItem は FrameLayout を継承するので子要素に任意Viewを配置できる。
 * <li>RadioItem の background 属性に適当な selector を設定する必要がある。
 * <li>layout.xmlサンプル<pre>{@code
 *   <org.kotemaru.android.postit.widget.RadioLayout
        android:id="@+id/shape_radio_group"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="@dimen/base_margin"
        android:orientation="horizontal"
        android:padding="@dimen/base_margin" >

        <org.kotemaru.android.postit.widget.RadioItem
            android:id="@+id/shape_shot"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:background="@drawable/radio_selector"
            android:padding="@dimen/base_margin" >

            <ImageView
                android:layout_width="50dp"
                android:layout_height="20dp"
                android:contentDescription="@null"
                android:scaleType="fitXY"
                android:src="@drawable/post_it_blue" />
        </org.kotemaru.android.postit.widget.RadioItem>
      </org.kotemaru.android.postit.widget.RadioLayout>
   }</pre>
 * 
 * <li>radio_selector.xmlサンプル<pre>{@code
 * <selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:drawable="@drawable/radio_on_bg" android:state_selected="true" android:state_pressed="false"/>
    <item android:drawable="@drawable/radio_off_bg" android:state_selected="false" android:state_pressed="false"/>
   </selector>
}</pre>
 * @author kotemaru.org
 */
public class RadioLayout extends LinearLayout {

	public RadioLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 項目の選択。RadioGroup互換。
	 * @param resId RadioItem のリソースID
	 */
	public void check(int resId) {
		RadioItem item = (RadioItem) findViewById(resId);
		if (item == null) return;
		onSelect(item);
	}
	
	/**
	 * 選択項目の取得。RadioGroup互換。
	 * @return RadioItem のリソースID
	 */
	public int getCheckedRadioButtonId() {
		RadioItem item = getSelectedItem();
		if (item == null) return -1;
		return item.getId();
	}

	/**
	 * 項目の選択。他の項目は選択解除する。
	 * @param item 選択項目
	 */
	public void onSelect(RadioItem item) {
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.setSelected(false);
		}
		item.setSelected(true);
	}
	/**
	 * 選択項目の取得。
	 * @return RadioItemインスタンス
	 */
	public RadioItem getSelectedItem() {
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child.isSelected()) return (RadioItem) child;
		}
		return null;
	}

	//--------------------------------------------------------------------
	// 以下、選択項目の value 属性値取得メソット。
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
