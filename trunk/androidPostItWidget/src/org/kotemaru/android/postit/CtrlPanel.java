package org.kotemaru.android.postit;

import org.kotemaru.android.postit.AnimFactory.AnimEndListener;
import org.kotemaru.android.postit.util.Util;

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

public class CtrlPanel {
	private PostItWallpaper mPostItWallpaper;
	private View mFrame;
	private View mInnerLayout;
	private ImageView mTrash;
	private Animation mFadeOut;
	private Animation mFadeIn;

	public static CtrlPanel create(PostItWallpaper postItWallpaper) {
		CtrlPanel ctrlPanel = new CtrlPanel(postItWallpaper);
		WindowManager.LayoutParams params = Util.geWindowLayoutParams();
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = WindowManager.LayoutParams.MATCH_PARENT;
		params.x = 0;
		params.y = 0;
		WindowManager wm = (WindowManager) postItWallpaper.getSystemService(Context.WINDOW_SERVICE);
		wm.addView(ctrlPanel.getView(), params);
		return ctrlPanel;
	}

	public CtrlPanel(PostItWallpaper postItWallpaper) {
		final Context context = postItWallpaper;
		mPostItWallpaper = postItWallpaper;
		LayoutInflater inflater = LayoutInflater.from(context);
		mFrame = inflater.inflate(R.layout.ctrl_panel, null);
		mInnerLayout = mFrame.findViewById(R.id.inner_layout);
		
		mFadeOut = AnimFactory.getFedeOut(context, new AnimEndListener(){
			@Override
			public void onAnimationEnd(Animation animation) {
				mFrame.setVisibility(View.GONE);
			}
		});
		mFadeIn = AnimFactory.getFedeIn(context, new AnimEndListener(){
			@Override
			public void onAnimationEnd(Animation animation) {
				if (!mPostItWallpaper.isVisible()) {
					mFrame.setVisibility(View.GONE);
				}
			}
		});

		mFrame.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View view) {
				hide();
			}
		});

		final ImageView layer = (ImageView) mFrame.findViewById(R.id.layer);
		layer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				boolean isRaise = mPostItWallpaper.isRaisePostIt();
				mPostItWallpaper.setRaisePostIt(!isRaise);
				layer.setImageResource(isRaise ? R.drawable.layer_0 : R.drawable.layer_1);
				hide();
			}
		});
		mTrash = (ImageView) mFrame.findViewById(R.id.trash);

		ImageView postit = (ImageView) mFrame.findViewById(R.id.post_it_blue);
		postit.setOnTouchListener(new NewPostItDragStartListener(PostItColor.BLUE));
		postit = (ImageView) mFrame.findViewById(R.id.post_it_green);
		postit.setOnTouchListener(new NewPostItDragStartListener(PostItColor.GREEN));
		postit = (ImageView) mFrame.findViewById(R.id.post_it_yellow);
		postit.setOnTouchListener(new NewPostItDragStartListener(PostItColor.YELLOW));
		postit = (ImageView) mFrame.findViewById(R.id.post_it_pink);
		postit.setOnTouchListener(new NewPostItDragStartListener(PostItColor.PINK));
		
		
		
		mFrame.setOnDragListener(mNewPostItDropListener);
	}

	private class NewPostItDragStartListener implements OnTouchListener {
		private String mColor;

		public NewPostItDragStartListener(int color) {
			mColor = Integer.toString(color);
		}

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
				Launcher.startPostItEditActivity(mPostItWallpaper, data);
				hide();
			}
			return false;
		};
	};

	public View getView() {
		return mFrame;
	}

	public Point getTrashPoint() {
		int x = mTrash.getLeft() + mTrash.getWidth()/2;
		int y = mTrash.getTop() + mTrash.getHeight()/2;
		return new Point(x,y);
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
		if (mFrame.getVisibility() == View.VISIBLE) {
			hide();
		} else {
			show();
		}
	}
	public void show() {
		mFrame.setVisibility(View.VISIBLE);
		mInnerLayout.startAnimation(mFadeIn);
	}
	public void hide() {
		mInnerLayout.startAnimation(mFadeOut);
	}
	public void gone() {
		mFrame.setVisibility(View.GONE);
	}

}
