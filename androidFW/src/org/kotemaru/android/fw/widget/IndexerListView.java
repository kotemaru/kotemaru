package org.kotemaru.android.fw.widget;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

public class IndexerListView extends ListView {

	public IndexerListView(Context context) {
		this(context, null);
	}
	public IndexerListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public IndexerListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.setFastScrollEnabled(true);
	}

	public static class IndexerItem<T> {
		final T mData;
		final boolean mIsHeader;
		final int mSectionIntex;
		final SectionItem mSectionItem;

		public IndexerItem(T data, SectionItem sectionItem, int sectionIntex, boolean isHeader) {
			mData = data;
			mSectionItem = sectionItem;
			mSectionIntex = sectionIntex;
			mIsHeader = isHeader;
		}
		public boolean isHeader() {
			return mIsHeader;
		}
		public T getData() {
			return mData;
		}
		public String getSectionName() {
			return mSectionItem.mName;
		}
	}

	public static class SectionItem {
		final String mName;
		final int mIndex;

		public SectionItem(String name, int index) {
			mName = name;
			mIndex = index;
		}

		public String toString() {
			return mName;
		}
	}

	public static abstract class IndexerAdapter<T> extends BaseAdapter implements SectionIndexer {
		private SectionItem[] mSections;
		private List<IndexerItem<T>> mItemList = new ArrayList<IndexerItem<T>>();
		private List<T> mOriginData;

		protected IndexerAdapter() {
		}
		public List<T> getData() {
			return mOriginData;
		}

		public void setData(List<T> datas) {
			mOriginData = datas;
			mItemList.clear();
			List<SectionItem> sections = new LinkedList<SectionItem>();
			String currentSectionName = "";
			SectionItem sectionItem = null;
			for (int i = 0; i < datas.size(); i++) {
				T data = datas.get(i);
				String sectionName = getSectionName(data);
				if (!currentSectionName.equals(sectionName)) {
					currentSectionName = sectionName;
					sectionItem = new SectionItem(sectionName, i);
					sections.add(sectionItem);
					mItemList.add(new IndexerItem<T>(null, sectionItem, sections.size(), true));
				}
				mItemList.add(new IndexerItem<T>(data, sectionItem, sections.size(), false));
			}
			mSections = sections.toArray(new SectionItem[sections.size()]);
			super.notifyDataSetChanged();
		}

		public IndexerItem<T> getIndexerItem(int position) {
			return mItemList.get(position);
		}

		// --------------------------------------------------------
		// abstract
		public abstract String getSectionName(T data);
		@Override
		public abstract View getView(int position, View convertView, ViewGroup parent);

		// -----------------------------------------
		// implements BaseAdapter
		@Override
		public int getCount() {
			return mItemList.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public Object getItem(int position) {
			return getIndexerItem(position);
		}

		// -----------------------------------------
		// implements SectionIndexer
		@Override
		public Object[] getSections() {
			return mSections;
		}

		@Override
		public int getPositionForSection(int sectionIndex) {
			if (sectionIndex < 0) sectionIndex = 0;
			if (sectionIndex >= mSections.length) sectionIndex = mSections.length-1;
			return mSections[sectionIndex].mIndex;
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0; // not implement.
		}

	}

}
