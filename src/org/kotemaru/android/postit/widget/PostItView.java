package org.kotemaru.android.postit.widget;

import org.kotemaru.android.postit.PostItConst.PostItColor;
import org.kotemaru.android.postit.PostItViewManager;
import org.kotemaru.android.postit.PostItWallpaper;
import org.kotemaru.android.postit.R;
import org.kotemaru.android.postit.data.PostItData;
import org.kotemaru.android.postit.data.PostItDataProvider;
import org.kotemaru.android.postit.util.AnimFactory;
import org.kotemaru.android.postit.util.AnimFactory.AnimEndListener;
import org.kotemaru.android.postit.util.IntIntMap;
import org.kotemaru.android.postit.util.Launcher;
import org.kotemaru.android.postit.util.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;


/**
 * 付箋用のカスタムView。
 * <li>WindowManagerの子となる。
 * <li>ドラッグ＆ドロップ処理はすべて自力で行う。
 * @author kotemaru.org
 */

public class PostItView extends FrameLayout {
	
	/** 付箋の色と対応する画像リソースのマップ */
	private static final IntIntMap sColorResourceMap = new IntIntMap(new int[][] {
			{ R.drawable.post_it_blue, PostItColor.BLUE, },
			{ R.drawable.post_it_green, PostItColor.GREEN, },
			{ R.drawable.post_it_yellow, PostItColor.YELLOW, },
			{ R.drawable.post_it_pink, PostItColor.PINK, },
			{ R.drawable.post_it_red, PostItColor.RED, },
	});

	
	private PostItViewManager mManager;
	private PostItData mPostItData;
	private TextView mMemo;

	public PostItView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressLint("ClickableViewAccessibility")
	public void onCreate(PostItViewManager manager) {
		mManager = manager;
		mMemo = (TextView) findViewById(R.id.textBody);
		this.setOnTouchListener(mOnTouchListener);
	}

	public long getPostItId() {
		return mPostItData.getId();
	}

	public PostItData getPostItData() {
		return mPostItData;
	}

	/**
	 * 付箋データの設定。
	 * <li>データの内容をViewの状態に反映させる。
	 * @param postItData 付箋データ
	 */
	public void setPostItData(PostItData postItData) {
		this.mPostItData = postItData;
		setBackground(false);
		mMemo.setText(postItData.getMemo());
		mMemo.setTextSize(postItData.getFontSize());
		FrameLayout.LayoutParams memoParams = (FrameLayout.LayoutParams) mMemo.getLayoutParams();
		memoParams.width = Util.sp2px(getContext(), postItData.getWidth());
		memoParams.height = Util.sp2px(getContext(), postItData.getHeight());
		mMemo.setLayoutParams(memoParams);

		WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
		if (params == null) return;
		params.x = mPostItData.getPosX();
		params.y = mPostItData.getPosY();
		mManager.getWindowManager().updateViewLayout(this, params);
	}

	/**
	 * 背景設定。
	 * <li>通常は付箋データから背景色を設定する。
	 * <li>ゴミ箱の上にいる場合は赤の背景になる。
	 * <li>9.pngを設定すると勝手にPaddingが設定されるのでリセットが必要。
	 * @param isOnTrash true=ゴミ箱の上
	 */
	private void setBackground(boolean isOnTrash) {
		if (isOnTrash) {
			mMemo.setBackgroundResource(R.drawable.post_it_remove);
		} else {
			mMemo.setBackgroundResource(sColorResourceMap.getFirst(mPostItData.getColor()));
		}
		// Need seting padding this timing.
		int pad = Util.dp2px(getContext(), 2);
		mMemo.setPadding(pad, 0, pad, 0);
	}

	/**
	 * ドラッグ処理用のリスナ。
	 * <li>WindowManagerの子となるので通常のドラッグ処理は使えないので自力で処理。
	 */
	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		private PostItView self = PostItView.this;
		private int rx, ry;
		private long touchDownTime = -1;

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View view, MotionEvent ev) {
			PostItWallpaper postItWallpaper = mManager.getPostItWallpaper();
			PostItTray postItTray = postItWallpaper.getPostItTray();

			int action = ev.getAction();
			if (action == MotionEvent.ACTION_DOWN) {
				postItTray.show();
				rx = (int) ev.getX();
				ry = (int) ev.getY();
				touchDownTime = System.currentTimeMillis();
			} else if (action == MotionEvent.ACTION_MOVE) {
				self.onDrag(ev, rx, ry);
			} else if (action == MotionEvent.ACTION_UP) {
				boolean isClick = (System.currentTimeMillis() - touchDownTime) < 200; // ms
				if (isClick) {
					self.onClick(ev, rx, ry);
				} else {
					self.onDrop(ev, rx, ry);
				}
			}
			return false;
		}
	};

	/**
	 * クリック処理。編集画面を起動する。
	 * @param ev
	 * @param rx
	 * @param ry
	 */
	public void onClick(MotionEvent ev, int rx, int ry) {
		PostItWallpaper postItWallpaper = mManager.getPostItWallpaper();
		PostItTray postItTray = postItWallpaper.getPostItTray();
		postItTray.hide();
		Launcher.startPostItSettingsActivity(postItWallpaper, mPostItData);
	}

	/**
	 * ドラッグ処理。
	 * <li>自力で座標変更する。
	 * @param ev
	 * @param rx
	 * @param ry
	 */
	private void onDrag(MotionEvent ev, int rx, int ry) {
		PostItWallpaper postItWallpaper = mManager.getPostItWallpaper();
		PostItTray postItTray = postItWallpaper.getPostItTray();
		setAlpha(0.7F);
		WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
		params.x += (int) ev.getX() - rx;
		params.y += (int) ev.getY() - ry;
		mManager.getWindowManager().updateViewLayout(this, params);
		mPostItData.setPosX(params.x);
		mPostItData.setPosY(params.y);
		boolean isOnTrash = postItTray.noticeDrag(this, params.x + rx, params.y + ry);
		setBackground(isOnTrash);
	}
	
	/**
	 * ドロップ処理。
	 * <li>ゴミ箱かどうかで処理の振り分け。
	 * @param ev
	 * @param rx
	 * @param ry
	 */
	private void onDrop(MotionEvent ev, int rx, int ry) {
		final PostItWallpaper postItWallpaper = mManager.getPostItWallpaper();
		final PostItTray postItTray = postItWallpaper.getPostItTray();
		WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
		boolean isOnTrash = postItTray.noticeDrop(this, params.x + rx, params.y + ry);
		if (isOnTrash) {
			doTrash(params);
		} else {
			doMove(params);
		}
	}

	/**
	 * ゴミ箱行き処理。
	 * <li>データ削除してゴミ箱に吸い込まれるアニメーション開始。
	 * @param params
	 */
	private void doTrash(WindowManager.LayoutParams params) {
		final PostItWallpaper postItWallpaper = mManager.getPostItWallpaper();
		final PostItTray postItTray = postItWallpaper.getPostItTray();

		PostItDataProvider.removePostItData(postItWallpaper, mPostItData);
		Point trashPoint = postItTray.getTrashPoint();
		float pivotX = (float) (trashPoint.x - params.x);
		float pivotY = (float) (trashPoint.y - params.y);
		Animation removeAnim = AnimFactory.getRemove(postItWallpaper, pivotX, pivotY, new AnimEndListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				postItWallpaper.update();
				postItTray.hide();
			}
		});
		mMemo.startAnimation(removeAnim);
	}
	/**
	 * 移動確定処理。
	 * <li>移動先座標をDBに保存する。
	 * <li>画面の外にはみ出さないように座標は補正する。
	 * @param params
	 */
	private void doMove(WindowManager.LayoutParams params) {
		final PostItWallpaper postItWallpaper = mManager.getPostItWallpaper();
		final PostItTray postItTray = postItWallpaper.getPostItTray();

		int dp20 = Util.dp2px(postItWallpaper, 20);
		Rect bounds = postItWallpaper.getBounds();
		if (mPostItData.getPosX() < bounds.left) mPostItData.setPosX(bounds.left);
		if (mPostItData.getPosY() < bounds.top) mPostItData.setPosY(bounds.top);
		if (mPostItData.getPosX() > bounds.right - dp20) mPostItData.setPosX(bounds.right - dp20);
		if (mPostItData.getPosY() > bounds.bottom - dp20) mPostItData.setPosY(bounds.bottom - dp20);
		this.setPostItData(mPostItData);
		PostItDataProvider.updatePostItData(postItWallpaper, mPostItData);
		postItTray.hide();
	}

}
