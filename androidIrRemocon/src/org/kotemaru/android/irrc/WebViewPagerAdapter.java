package org.kotemaru.android.irrc;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class WebViewPagerAdapter extends FragmentStatePagerAdapter {
	private List<String> remoconList;
	private WebViewFragment[] fragments;
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int index) {
			getWebViewFragment(index).onSelected();
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	};

	public WebViewPagerAdapter(RemoconActivity activity, List<String> remoconList) {
		super(activity.getSupportFragmentManager());
		this.remoconList = remoconList;
		this.fragments = new WebViewFragment[remoconList.size()];
	}

	@Override
	public int getCount() {
		return fragments.length;
	}

	@Override
	public Fragment getItem(int i) {
		WebViewFragment fragment = new WebViewFragment();
		fragment.setUrl(remoconList.get(i));
		fragments[i] = fragment;
		return fragments[i];
	}

	public WebViewFragment getWebViewFragment(int index) {
		return fragments[index];
	}

	public OnPageChangeListener getOnPageChangeListener() {
		return onPageChangeListener;
	}

}