package org.kotemaru.android.postit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kotemaru.android.postit.data.PostItData;
import org.kotemaru.android.postit.data.PostItDataProvider;
import org.kotemaru.android.postit.util.Util;
import org.kotemaru.android.postit.widget.PostItView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/**
 * PostItViewクラスを管理する。
 * <li>PostItViewの作成、更新、削除、同期を行う。
 * <li>PostItView は WindowManager の子Viewとなるので追加、削除処理を行う。
 * <li>ここでは View の管理を行うだけでデータは操作しない。
 * @author kotemaru.org
 */
public class PostItViewManager {
	private PostItWallpaper mPostItWallpaper;
	private WindowManager mWindowManager;
	private LayoutInflater mLayoutInflater;

	/**
	 * 付箋Viewの一覧。
	 * <li>１０件程度を想定するのでマップにはしない。
	 */
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

	/**
	 * すべての付箋の表示切り替え。
	 * @param isShow true=表示。
	 */
	public void show(boolean isShow) {
		for (PostItView postItView : mPostItViewList) {
			postItView.setVisibility(isShow ? View.VISIBLE : View.GONE);
		}
	}

	/**
	 * 付箋View一覧取得。
	 * @return
	 */
	public List<PostItView> getPostItViewList() {
		return mPostItViewList;
	}

	/**
	 * ID指定して付箋Viewの取得
	 * @param id 付箋ID
	 * @return 付箋View。見つからなければnull。
	 */
	public PostItView getPostItViewFromId(long id) {
		for (PostItView postItView : mPostItViewList) {
			if (postItView.getPostItId() == id) return postItView;
		}
		return null;
	}

	/**
	 * 付箋Viewの同期。
	 * <li>DBの付箋データと表示を同期させる。
	 * <li>付箋の作成、削除にも対応する。
	 */
	public void syncPostItDataProvider() {
		LongSparseArray<PostItData> providerAllDataMap = PostItDataProvider.getPostItDataMap(mPostItWallpaper);
		Iterator<PostItView> ite = mPostItViewList.iterator();
		// 既存付箋Viewの更新と削除
		while (ite.hasNext()) {
			PostItView postItView = ite.next();
			long id = postItView.getPostItId();
			PostItData providerData = providerAllDataMap.get(id);
			if (providerData != null) {
				updatePostItView(postItView, providerData);
				providerAllDataMap.remove(id);
			} else {
				removePostItView(postItView);
				ite.remove();
			}
		}

		// 新規付箋Viewの作成
		for (int i = 0; i < providerAllDataMap.size(); i++) {
			long id = providerAllDataMap.keyAt(i);
			PostItData data = providerAllDataMap.get(id);
			PostItView postItView = createPostItView(data);
			mPostItViewList.add(postItView);
		}
	}

	/**
	 * 付箋Viewのデータ差し替え。
	 * @param view
	 * @param data
	 */
	private void updatePostItView(PostItView view, PostItData data) {
		view.setPostItData(data);
		view.setVisibility(data.isEnabled() ? View.VISIBLE : View.GONE);
	}
	/**
	 * 付箋Viewの削除。
	 * <li>WindowManagerから削除。
	 * @param postItView
	 */
	private void removePostItView(PostItView postItView) {
		mWindowManager.removeView(postItView);
	}

	/**
	 * 付箋Viewの新規作成。
	 * <li>
	 * @param data 付箋データ
	 * @return 付箋View
	 */
	private PostItView createPostItView(PostItData data) {
		@SuppressLint("InflateParams")
		PostItView view = (PostItView) mLayoutInflater.inflate(R.layout.post_it_view, null);
		view.onCreate(this);

		WindowManager.LayoutParams params = Util.getWindowLayoutParams();
		mWindowManager.addView(view, params);
		view.setPostItData(data);
		return view;
	}

}