package org.kotemaru.android.fw.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class IndexerBar extends LinearLayout {
	private ListView mListView;
	private SectionIndexer mSectionIndexer;
	private int mItemLayoutId = 0;

	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			int idx = (int)(event.getY() / (getHeight()/getChildCount()));
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				mListView.setSelection(mSectionIndexer.getPositionForSection(idx));
				return true;
			case MotionEvent.ACTION_UP:
				view.performClick();
				return true;
			}
			return false;
		}
	};

	public IndexerBar(Context context) {
		this(context, null);
	}
	public IndexerBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	@SuppressLint("ClickableViewAccessibility")
	public IndexerBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setOrientation(LinearLayout.VERTICAL);
		setOnTouchListener(mOnTouchListener);
	}

	public void setListViewInfo(ListView listView, SectionIndexer sectionIndexer) {
		mListView = listView;
		mSectionIndexer = sectionIndexer;
		setSections(mSectionIndexer.getSections());

		mListView.getAdapter().registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				setSections(mSectionIndexer.getSections());
			}
		});
	}
	public void setItemLayoutId(int id) {
		mItemLayoutId  = id;
		if (mSectionIndexer != null) {
			removeAllViews();
			setSections(mSectionIndexer.getSections());
		}
	}
	

	public void setSections(Object[] sections) {
		if (sections == null) return;

		int childCount = getChildCount();
		if (childCount < sections.length) {
			for (int i = childCount; i < sections.length; i++) {
				addView(createTextView());
			}
		} else {
			for (int i = sections.length; i < childCount; i++) {
				this.removeViewAt(0);
			}
		}

		for (int i = 0; i < sections.length; i++) {
			Object section = sections[i];
			TextView textView = (TextView) this.getChildAt(i);
			textView.setText(section.toString());
		}
	}
	
	private TextView createTextView() {
		if (mItemLayoutId == 0) {
			TextView textView = new TextView(getContext());
			LinearLayout.LayoutParams params = 
					new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0);
			params.weight = 1.0F;
			textView.setLayoutParams(params);
			textView.setTypeface(Typeface.DEFAULT_BOLD);
			textView.setGravity(Gravity.CENTER);
			return textView;
		} else {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			TextView textView =  (TextView) inflater.inflate(mItemLayoutId, this, false);
			textView.setVisibility(View.VISIBLE);
			return textView;
		}
	}
}
