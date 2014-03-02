package org.kotemaru.android.irrc;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class WebViewPagerAdapter extends PagerAdapter {
	// private List<String> remoconList;
	private ViewPager viewPager;
	private WebViewContainer[] containers;
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int index) {
			WebViewContainer container = getWebViewContainer(index);
			if (container != null) container.onSelected();
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			// Log.d("WebViewPagerAdapter", "onPageScrollStateChanged:"+state);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// Log.d("WebViewPagerAdapter", "onPageScrolled:"+ position+","+ positionOffset+","+ positionOffsetPixels);
		}
	};

	public WebViewPagerAdapter(RemoconActivity activity, ViewPager viewPager, List<String> remoconList) {
		super();
		this.viewPager = viewPager;
		// this.remoconList = remoconList;
		this.containers = new WebViewContainer[remoconList.size()];
		for (int i = 0; i < remoconList.size(); i++) {
			WebViewContainer container = new WebViewContainer(activity);
			container.setUrl(remoconList.get(i));
			containers[i] = container;
		}
	}

	@Override
	public int getCount() {
		return containers.length;
	}

	@Override
	public Object instantiateItem(ViewGroup viewGroup, int position) {
		Log.d("WebViewPagerAdapter", "getItem:" + position);
		WebViewContainer container = containers[position];
		container.load(false);
		viewGroup.addView(container.getWebview());
		return container;
	}

	@Override
	public boolean isViewFromObject(View view, Object key) {
		if (key instanceof WebViewContainer) {
			return ((WebViewContainer) key).getWebview() == view;
		}
		return false;
	}

	@Override
	public void destroyItem(ViewGroup viewGroup, int position, Object object) {
		Log.d("WebViewPagerAdapter", "destroyItem:" + object);

		viewGroup.removeView(((WebViewContainer) object).getWebview());
	}

	public void onDestroy() {
		Log.i("WebViewPagerAdapter", "Container.onDestroy();" + this);
		for (int i = 0; i < containers.length; i++) {
			if (containers[i] != null) containers[i].onDestroy();
		}
	}

	public WebViewContainer getWebViewContainer(int index) {
		return containers[index];
	}

	public OnPageChangeListener getOnPageChangeListener() {
		return onPageChangeListener;
	}

}