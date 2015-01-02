package org.kotemaru.android.async;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import android.util.Log;

public abstract class BaseSelectorItem implements SelectorItem, SelectorListener {
	private static final String TAG = BaseSelectorItem.class.getSimpleName();
	protected static final boolean IS_DEBUG = BuildConfig.DEBUG;

	protected final SocketChannel mChannel;
	protected SelectorItemListener mItemListener;
	protected int mSelectorFlag;

	public BaseSelectorItem(SocketChannel channel) {
		mChannel = channel;
	}
	@Override
	public SocketChannel getChannel() {
		return mChannel;
	}

	@Override
	public abstract int write(ByteBuffer buffer);
	@Override
	public abstract int read(ByteBuffer buffer);
	@Override
	public abstract void close();

	@Override
	public void release() {
		SelectorThread.getInstance().release(mChannel);
	}

	protected void doError(String msg, Throwable err) {
		Log.w(TAG, msg, err);
		if (mItemListener != null) {
			mItemListener.onError(msg, err);
		}
	}
	@Override
	public void onError(String msg, Throwable t) {
		doError(msg, t);
	}
	@Override
	public void setListener(SelectorItemListener listener) {
		mItemListener = listener;
	}
	@Override
	public void requireOn(int flag) {
		mSelectorFlag = flag;
		if ((mSelectorFlag & OP_READ) != 0) {
			SelectorThread.getInstance().resume(mChannel, SelectionKey.OP_READ);
		} else {
			SelectorThread.getInstance().pause(mChannel, SelectionKey.OP_READ);
		}
		if ((mSelectorFlag & OP_WRITE) != 0) {
			SelectorThread.getInstance().resume(mChannel, SelectionKey.OP_WRITE);
		} else {
			SelectorThread.getInstance().pause(mChannel, SelectionKey.OP_WRITE);
		}
	}
}
