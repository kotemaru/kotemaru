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
	}

	public static class IndexerItem<T> {
		private final T mData;
		private final boolean mIsHeader;
		private final SectionItem mSectionItem;

		public IndexerItem(T data, SectionItem sectionItem, boolean isHeader) {
			mData = data;
			mSectionItem = sectionItem;
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
		private final String mName;
		private final int mSectionIndex;
		private final int mPosition;

		public SectionItem(String name, int sectionIndex, int position) {
			mName = name;
			mSectionIndex = sectionIndex;
			mPosition = position;
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

		public void setListData(List<T> datas) {
			mOriginData = datas;
			mItemList.clear();
			List<SectionItem> sections = new LinkedList<SectionItem>();
			String currentSectionName = "";
			SectionItem sectionItem = null;
			for (T data : datas) {
				String sectionName = getSectionName(data);
				if (!currentSectionName.equals(sectionName)) {
					currentSectionName = sectionName;
					sectionItem = new SectionItem(sectionName, sections.size(), mItemList.size());
					sections.add(sectionItem);
					mItemList.add(new IndexerItem<T>(null, sectionItem, true));
				}
				mItemList.add(new IndexerItem<T>(data, sectionItem, false));
			}
			mSections = sections.toArray(new SectionItem[sections.size()]);
			super.notifyDataSetChanged();
		}

		public IndexerItem<T> getIndexerItem(int position) {
			return mItemList.get(position);
		}

		// --------------------------------------------------------
		// abstract
		@Override
		public abstract View getView(int position, View convertView, ViewGroup parent);
		public abstract String getSectionName(T data);

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
			if (sectionIndex >= mSections.length) sectionIndex = mSections.length - 1;
			return mSections[sectionIndex].mPosition;
		}

		@Override
		public int getSectionForPosition(int position) {
			if (position < 0) position = 0;
			if (position >= mItemList.size()) position = mItemList.size() - 1;
			return mItemList.get(position).mSectionItem.mSectionIndex;
		}
	}
}
