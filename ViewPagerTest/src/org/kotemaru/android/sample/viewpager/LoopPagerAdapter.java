package org.kotemaru.android.sample.viewpager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LoopPagerAdapter extends PagerAdapter {
	private static final String TAG = "LoopPagerAdapter";
	
	private ViewPager viewPager;
	private Page[] pages;
	
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			if (position == 0) viewPager.setCurrentItem(getCount()-2,false);
			if (position == getCount()-1) viewPager.setCurrentItem(1,false);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			// Log.d(TAG, "onPageScrollStateChanged:"+state);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// Log.d(TAG, "onPageScrolled:"+ position+","+ positionOffset+","+ positionOffsetPixels);
		}
	};

	public LoopPagerAdapter(ViewPager viewPager, View[] views) {
		super();
		this.viewPager = viewPager;
		this.pages = new Page[views.length+2];
		
		for (int i=0;i<views.length; i++) {
			pages[i+1] = new Page(views[i]);
		}
		pages[0] = new Page(views[views.length-1]);
		pages[pages.length-1] = new Page(views[0]);
		viewPager.setOnPageChangeListener(onPageChangeListener);
	}
	
	@Override
	public int getCount() {
		return pages.length;
	}

	@Override
	public Object instantiateItem(ViewGroup viewGroup, int position) {
		Log.d(TAG, "instantiateItem:" + position);
		Page page = pages[position];
		View view = page.getView();
		for (int i=0;i<pages.length; i++) {
			if (pages[i].getView() == view) pages[i].setValid(false);
		}
		page.setValid(true);
		
		viewGroup.removeView(view);
		viewGroup.addView(view);
		return page;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		Page page = (Page) object;
		return page.isValid() && page.getView() == view;
	}

	@Override
	public void destroyItem(ViewGroup viewGroup, int position, Object object) {
		Page page = (Page) object;
		Log.d(TAG, "destroyItem:" + position+" : "+((TextView)page.getView()).getText());
		if (page.isValid()) {
			viewGroup.removeView(page.getView());
		}
		page.setValid(false);
	}
}