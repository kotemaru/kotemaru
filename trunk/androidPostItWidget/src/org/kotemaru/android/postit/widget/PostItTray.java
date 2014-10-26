package org.kotemaru.android.postit.widget;

import org.kotemaru.android.postit.PostItWallpaper;
import org.kotemaru.android.postit.R;
import org.kotemaru.android.postit.data.PostItColor;
import org.kotemaru.android.postit.data.PostItData;
import org.kotemaru.android.postit.util.AnimFactory;
import org.kotemaru.android.postit.util.AnimFactory.AnimEndListener;
import org.kotemaru.android.postit.util.Launcher;
import org.kotemaru.android.postit.util.Util;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Point;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * 付箋制御用の画面。
 * <li>ホーム画面にオーバレイ表示する。必要のない時は非表示。
 * <li>付箋の新規作成、削除、表示モード変更を行うアイコンを配置する。
 * <li>タッチイベントを受け取るためサイズは全画面となる。
 * <li>何も表示していない部分も半透明にして暗くする。（ユーザが混乱しないように）
 * <li>
 * 
 * @author kotemaru.org
 */
public class PostItTray {
	private PostItWallpaper mPostItWallpaper;
	private View mFullScreenFrame;
	private View mTrayLayout;
	private ImageView mLayer;
	private ImageView mTrash;
	private Animation mFadeOutAnim;
	private Animation mFadeInAnim;

	public static PostItTray create(PostItWallpaper postItWallpaper) {
		PostItTray postItTray = new PostItTray(postItWallpaper);
		WindowManager.LayoutParams params = Util.getWindowLayoutParams();
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = WindowManager.LayoutParams.MATCH_PARENT;
		params.x = 0;
		params.y = 0;
		WindowManager wm = (WindowManager) postItWallpaper.getSystemService(Context.WINDOW_SERVICE);
		wm.addView(postItTray.getView(), params);
		return postItTray;
	}

	public PostItTray(PostItWallpaper postItWallpaper) {
		final Context context = postItWallpaper;
		mPostItWallpaper = postItWallpaper;
		LayoutInflater inflater = LayoutInflater.from(context);
		mFullScreenFrame = inflater.inflate(R.layout.post_it_tray, null);
		mTrayLayout = mFullScreenFrame.findViewById(R.id.tray);

		mFullScreenFrame.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View view) {
				hide();
			}
		});
		mFullScreenFrame.setOnDragListener(mNewPostItDropListener);

		setupNewPostItDragStartListener(R.id.post_it_blue, PostItColor.BLUE);
		setupNewPostItDragStartListener(R.id.post_it_green, PostItColor.GREEN);
		setupNewPostItDragStartListener(R.id.post_it_yellow, PostItColor.YELLOW);
		setupNewPostItDragStartListener(R.id.post_it_pink, PostItColor.PINK);

		mLayer = (ImageView) mFullScreenFrame.findViewById(R.id.layer);
		mLayer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				boolean isRaise = mPostItWallpaper.isRaisePostIt();
				mPostItWallpaper.setRaisePostIt(!isRaise);
				mLayer.setImageResource(isRaise ? R.drawable.layer_0 : R.drawable.layer_1);
				hide();
			}
		});

		mTrash = (ImageView) mFullScreenFrame.findViewById(R.id.trash);

		mFadeOutAnim = AnimFactory.getFedeOut(context, new AnimEndListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				mFullScreenFrame.setVisibility(View.GONE);
			}
		});
		mFadeInAnim = AnimFactory.getFedeIn(context, new AnimEndListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				if (!mPostItWallpaper.isVisible()) {
					mFullScreenFrame.setVisibility(View.GONE);
				}
			}
		});
	}

	private void setupNewPostItDragStartListener(int resId, int color) {
		ImageView postit = (ImageView) mFullScreenFrame.findViewById(resId);
		postit.setOnTouchListener(new NewPostItDragStartListener(color));
	}

	private class NewPostItDragStartListener implements OnTouchListener {
		private String mColor;

		public NewPostItDragStartListener(int color) {
			mColor = Integer.toString(color);
		}

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View view, MotionEvent ev) {
			int action = ev.getAction();
			if (action == MotionEvent.ACTION_DOWN) {
				ClipData clipData = ClipData.newPlainText("newPostIt", mColor);
				DragShadowBuilder myShadow = new DragShadowBuilder(view);
				return view.startDrag(clipData, myShadow, null, 0);
			}
			return false;
		}
	};

	private View.OnDragListener mNewPostItDropListener = new View.OnDragListener() {
		@Override
		public boolean onDrag(View view, DragEvent ev) {
			int action = ev.getAction();
			if (action == DragEvent.ACTION_DROP) {
				ClipData.Item item = ev.getClipData().getItemAt(0);
				int color = Integer.parseInt(item.getText().toString());
				PostItData data = new PostItData(-1, color, (int) ev.getX(), (int) ev.getY());
				PostItView postItView = mPostItWallpaper.createPostIt(data);
				data = postItView.getPostItData();
				Launcher.startPostItSettingsActivity(mPostItWallpaper, data);
				hide();
			}
			return false;
		};
	};

	public View getView() {
		return mFullScreenFrame;
	}

	public Point getTrashPoint() {
		int x = mTrash.getLeft() + mTrash.getWidth() / 2;
		int y = mTrash.getTop() + mTrash.getHeight() / 2;
		return new Point(x, y);
	}

	private boolean isOnTrash(int x, int y) {
		int left = mTrash.getLeft();
		int right = mTrash.getRight();
		int top = mTrash.getTop();
		int bottom = mTrash.getBottom();
		return (left < x && x < right && top < y && y < bottom);
	}

	public boolean noticeDrag(View view, int x, int y) {
		boolean isOnTrash = isOnTrash(x, y);
		mTrash.setImageResource(isOnTrash ? R.drawable.trash_red : R.drawable.trash_white);
		return isOnTrash;
	}
	public boolean noticeDrop(View view, int x, int y) {
		boolean isOnTrash = isOnTrash(x, y);
		mTrash.setImageResource(R.drawable.trash_white);
		return isOnTrash;
	}

	public void toggle() {
		if (mFullScreenFrame.getVisibility() == View.VISIBLE) {
			hide();
		} else {
			show();
		}
	}
	public void show() {
		mFullScreenFrame.setVisibility(View.VISIBLE);
		mTrayLayout.startAnimation(mFadeInAnim);
	}
	public void hide() {
		mTrayLayout.startAnimation(mFadeOutAnim);
	}
	public void gone() {
		mFullScreenFrame.setVisibility(View.GONE);
	}

}
