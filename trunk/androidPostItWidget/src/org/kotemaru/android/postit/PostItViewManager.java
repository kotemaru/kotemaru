package org.kotemaru.android.postit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kotemaru.android.postit.util.Util;

import android.content.Context;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class PostItViewManager {
	private PostItWallpaper mPostItWallpaper;
	private WindowManager mWindowManager;
	private LayoutInflater mLayoutInflater;

	private List<PostItView> mPostItViewList = new ArrayList<PostItView>();

	public PostItViewManager(PostItWallpaper context) {
		mPostItWallpaper = context;
		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	public PostItWallpaper getPostItWallpaper() {
		return mPostItWallpaper;
	}
	public WindowManager getWindowManager() {
		return mWindowManager;
	}
	
	public void show(boolean isShow) {
		for (PostItView postItView : mPostItViewList) {
			postItView.setVisibility(isShow ? View.VISIBLE : View.GONE);
		}
	}

	public List<PostItView> getPostItViewList() {
		return mPostItViewList;
	}
	public PostItView getPostItViewFromId(long id) {
		for (PostItView postItView : mPostItViewList) {
			if (postItView.getPostItId() == id) return postItView;
		}
		return null;
	}
	public PostItData getPostItViewFromView(View view) {
		for (PostItView postItView : mPostItViewList) {
			if (postItView == view) return postItView.getPostItData();
		}
		return null;
	}
	public void update() {
		LongSparseArray<PostItData> map = PostItDataProvider.getPostItDataMap(mPostItWallpaper);
		Iterator<PostItView> ite = mPostItViewList.iterator();
		while (ite.hasNext()) {
			PostItView postItView = ite.next();
			long id = postItView.getPostItId();
			PostItData newData = map.get(id);
			if (newData == null) {
				removePostItView(postItView);
				ite.remove();
			} else {
				updatePostItView(postItView, newData);
				map.remove(id);
			}
		}

		for (int i = 0; i < map.size(); i++) {
			long id = map.keyAt(i);
			PostItData data = map.get(id);
			PostItView postItView = createPostItView(data);
			mPostItViewList.add(postItView);
		}
	}
	private void updatePostItView(PostItView view, PostItData data) {
		view.setPostItData(data);
		if (data.isEnabled()) {
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(View.GONE);
		}
	}
	private void removePostItView(PostItView postItView) {
		mWindowManager.removeView(postItView);
	}

	private PostItView createPostItView(PostItData data) {
		PostItView view = (PostItView) mLayoutInflater.inflate(R.layout.post_it_view, null);
		view.onCreate(this);

		WindowManager.LayoutParams params = Util.geWindowLayoutParams();
		mWindowManager.addView(view, params);
		view.setPostItData(data);
		return view;
	}

	
}