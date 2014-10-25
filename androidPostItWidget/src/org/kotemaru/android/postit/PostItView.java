package org.kotemaru.android.postit;

import org.kotemaru.android.postit.AnimFactory.AnimEndListener;
import org.kotemaru.android.postit.util.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

public class PostItView extends FrameLayout {
	private static final SparseIntArray sColorResourceMap = new SparseIntArray();
	static {
		sColorResourceMap.put(PostItColor.BLUE, R.drawable.post_it_blue);
		sColorResourceMap.put(PostItColor.GREEN, R.drawable.post_it_green);
		sColorResourceMap.put(PostItColor.YELLOW, R.drawable.post_it_yellow);
		sColorResourceMap.put(PostItColor.PINK, R.drawable.post_it_pink);
		sColorResourceMap.put(PostItColor.RED, R.drawable.post_it_red);
	}

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

	public void setPostItData(PostItData postItData) {
		this.mPostItData = postItData;
		setTrashBackground(false);
		mMemo.setText(postItData.getMemo());
		mMemo.setTextSize(postItData.getFontSize());
		FrameLayout.LayoutParams memoParams = (FrameLayout.LayoutParams) mMemo.getLayoutParams();
		memoParams.width = Util.sp2px(getContext(), postItData.getWidth());
		memoParams.height = Util.sp2px(getContext(),postItData.getHeight());
		mMemo.setLayoutParams(memoParams);

		WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
		if (params == null) return;
		params.x = mPostItData.getPosX();
		params.y = mPostItData.getPosY();
		mManager.getWindowManager().updateViewLayout(this, params);
	}
	
	private void setTrashBackground(boolean isOnTrash) {
		if (isOnTrash) {
			mMemo.setBackgroundResource(R.drawable.post_it_remove);
		} else {
			mMemo.setBackgroundResource(sColorResourceMap.get(mPostItData.getColor()));
		}
		int pad = Util.dp2px(getContext(), 2);
		mMemo.setPadding(pad, 0, pad, 0);
	}
	
	
	private WindowManager.LayoutParams onDrag(int x, int y, int rx, int ry) {
		PostItWallpaper postItWallpaper = mManager.getPostItWallpaper();
		CtrlPanel ctrlPanel = postItWallpaper.getCtrlPanel();
		setAlpha(0.7F);
		WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
		params.x += x - rx;
		params.y += y - ry;
		mManager.getWindowManager().updateViewLayout(this, params);
		mPostItData.setPosX(params.x);
		mPostItData.setPosY(params.y);
		ctrlPanel.noticeDrag(this, params.x+rx, params.y+ry);
		return params;
	}
	private WindowManager.LayoutParams onDrop(int x, int y, int rx, int ry) {
		final PostItWallpaper postItWallpaper = mManager.getPostItWallpaper();
		final CtrlPanel ctrlPanel = postItWallpaper.getCtrlPanel();
		WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
		boolean isDropTrash = ctrlPanel.noticeDrop(this, params.x+rx, params.y+ry);
		if (isDropTrash) {
			PostItDataProvider.removePostItData(postItWallpaper, mPostItData);
			Point trashPoint = ctrlPanel.getTrashPoint();
			float pivotX =  (float)(trashPoint.x - params.x);
			float pivotY =  (float)(trashPoint.y - params.y);
			//Log.d("DEBUIG","===>"+pivotX+","+pivotY);
			Animation removeAnim = AnimFactory.getRemove(postItWallpaper,pivotX, pivotY, new AnimEndListener(){
				@Override
				public void onAnimationEnd(Animation animation) {
					postItWallpaper.update();
					ctrlPanel.hide();
				}
			});
			mMemo.startAnimation(removeAnim);
		} else {
			int dp20 = Util.dp2px(postItWallpaper, 20);
			Rect bounds = postItWallpaper.getBounds();
			if (mPostItData.getPosX() < bounds.left) mPostItData.setPosX(bounds.left);
			if (mPostItData.getPosY() < bounds.top) mPostItData.setPosY(bounds.top);
			if (mPostItData.getPosX() > bounds.right-dp20) mPostItData.setPosX(bounds.right-dp20);
			if (mPostItData.getPosY() > bounds.bottom-dp20) mPostItData.setPosY(bounds.bottom-dp20);
			this.setPostItData(mPostItData);
			PostItDataProvider.updatePostItData(postItWallpaper, mPostItData);
			ctrlPanel.hide();
		}
		return params;
	}
	
	public void onClick(MotionEvent ev) {
		PostItWallpaper postItWallpaper = mManager.getPostItWallpaper();
		CtrlPanel ctrlPanel = postItWallpaper.getCtrlPanel();
		ctrlPanel.hide();
		Launcher.startPostItEditActivity(postItWallpaper, mPostItData);
	}
	
	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		private PostItView self = PostItView.this;
		private boolean isClick = false;
		private int rx, ry;

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View view, MotionEvent ev) {
			PostItWallpaper postItWallpaper = mManager.getPostItWallpaper();
			CtrlPanel ctrlPanel = postItWallpaper.getCtrlPanel();
			
			int action = ev.getAction();
			if (action == MotionEvent.ACTION_DOWN) {
				ctrlPanel.show();
				isClick = true;
				rx = (int) ev.getX();
				ry = (int) ev.getY();
			} else if (action == MotionEvent.ACTION_MOVE) {
				isClick = false;
				WindowManager.LayoutParams params = self.onDrag((int) ev.getX(), (int) ev.getY(), rx, ry);
				boolean isTrash = ctrlPanel.noticeDrag(PostItView.this, params.x+rx, params.y+ry);
				self.setTrashBackground(isTrash);
			} else if (action == MotionEvent.ACTION_UP) {
				self.setTrashBackground(false);
				if (isClick) {
					self.onClick(ev);
				} else {
					self.onDrop((int) ev.getX(), (int) ev.getY(), rx, ry);
				}
			}
			return false;
		}
	};

}
