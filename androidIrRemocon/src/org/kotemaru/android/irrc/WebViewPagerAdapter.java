package org.kotemaru.android.irrc;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class WebViewPagerAdapter extends PagerAdapter {
	private List<String> remoconList;
	private WebViewFragment[] fragments;
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int index) {
			WebViewFragment fragment = getWebViewFragment(index);
			if (fragment != null) fragment.onSelected();
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	};

	public WebViewPagerAdapter(RemoconActivity activity, List<String> remoconList) {
		super();
		this.remoconList = remoconList;
		this.fragments = new WebViewFragment[remoconList.size()];
		for (int i=0; i<fragments.length; i++) {
			fragments[i] = new WebViewFragment(activity);
		}
	}

	@Override
	public int getCount() {
		return fragments.length;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Log.d("WebViewPagerAdapter", "getItem:" + position);
		WebViewFragment fragment = fragments[position];
		fragment.setUrl(remoconList.get(position));
		fragment.load();
		container.addView(fragment.getWebview());
		fragments[position] = fragment;
		return fragment;
	}
	@Override
	public boolean isViewFromObject(View view, Object key) {
		if (key instanceof WebViewFragment) {
			return ((WebViewFragment)key).getWebview() == view;
		}
		return false;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		Log.d("WebViewPagerAdapter", "destroyItem:" + object);
		((WebViewFragment)object).onDestroy();
		super.destroyItem(container, position, object);
	}
	
	public void onDestroy() {
		Log.i("WebViewPagerAdapter", "Fragment.onDestroy();" + this);
		for (int i=0; i<fragments.length; i++) {
			if (fragments[i] != null) fragments[i].onDestroy();
		}
	}

	public WebViewFragment getWebViewFragment(int index) {
		return fragments[index];
	}

	public OnPageChangeListener getOnPageChangeListener() {
		return onPageChangeListener;
	}

}