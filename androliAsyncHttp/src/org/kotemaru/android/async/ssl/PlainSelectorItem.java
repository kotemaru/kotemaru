package org.kotemaru.android.async.ssl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.kotemaru.android.async.BuildConfig;
import org.kotemaru.android.async.SelectorListener;
import org.kotemaru.android.async.SelectorThread;

import android.util.Log;

public class PlainSelectorItem implements SelectorItem, SelectorListener {
	private static final String TAG = PlainSelectorItem.class.getSimpleName();
	private static final boolean IS_DEBUG = BuildConfig.DEBUG;
	
	private final SocketChannel mChannel;
	private SelectorItemListener mItemListener;
	private int mFlag;

	public PlainSelectorItem(SocketChannel channel) {
		mChannel = channel;
	}
	@Override
	public SocketChannel getChannel() {
		return mChannel;
	}
	
	public int write(ByteBuffer buffer) {
		try {
			return mChannel.write(buffer);
		} catch (IOException e) {
			doError("write fial.", e);
			return 0;
		}
	}

	public int read(ByteBuffer buffer) {
		try {
			return mChannel.read(buffer);
		} catch (IOException e) {
			doError("read fial.", e);
			return 0;
		}
	}
	
	public void release() {
		SelectorThread.getInstance().release(mChannel);
	}

	public void close() {
		try {
			mChannel.close();
		} catch (IOException e) {
			doError("close fial.", e);
		}
	}

	private void doError(String msg, Throwable err) {
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
		mFlag = flag;
		if ((mFlag & OP_READ) != 0) {
			SelectorThread.getInstance().resume(mChannel, SelectionKey.OP_READ);
		} else {
			SelectorThread.getInstance().pause(mChannel, SelectionKey.OP_READ);
		}
		if ((mFlag & OP_WRITE) != 0) {
			SelectorThread.getInstance().resume(mChannel, SelectionKey.OP_WRITE);
		} else {
			SelectorThread.getInstance().pause(mChannel, SelectionKey.OP_WRITE);
		}
	}

	// ------------------------------------------------------------------------------------------
	@Override
	public void onRegister(SocketChannel channel) {
		mItemListener.onRegister(this);
	}
	@Override
	public void onAccept(SelectionKey key) {
		// not used.
	}
	@Override
	public void onConnect(SelectionKey key) {
		if (IS_DEBUG) Log.v(TAG, "onConnect:");
		try {
			if (mChannel.isConnectionPending()) {
				if (!mChannel.finishConnect()) {
					// TODO:本当はリトライが必要。でもSelectorからだから大丈夫なのかも。
					throw new IOException("finishConnect() fail");
				}
			}
			SelectorThread.getInstance().pause(mChannel, OP_ALL);
			mItemListener.onConnect();
		} catch (IOException e) {
			doError("Connection fail:", e);
		}
	}
	@Override
	public void onWritable(SelectionKey key) {
		if (IS_DEBUG) Log.v(TAG, "onWritable:");
		try {
			if ((mFlag & OP_WRITE) != 0) {
				mItemListener.onWritable();
			}
		} catch (IOException e) {
			doError("onWritable fail.", e);
		}
	}
	@Override
	public void onReadable(SelectionKey key) {
		if (IS_DEBUG) Log.v(TAG, "onReadable:");
		try {
			if ((mFlag & OP_READ) != 0) {
				mItemListener.onReadable();
			}
		} catch (IOException e) {
			doError("onWritable fail.", e);
		}
	}
	@Override
	public boolean isConnected() {
		return mChannel.isConnected();
	}

}
