package org.kotemaru.android.async;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import android.util.Log;

public class PlainSelectorItem extends BaseSelectorItem implements SelectorListener {
	private static final String TAG = PlainSelectorItem.class.getSimpleName();
	
	public PlainSelectorItem(SocketChannel channel) {
		super(channel);
	}
	
	@Override
	public int write(ByteBuffer buffer) {
		try {
			return mChannel.write(buffer);
		} catch (IOException e) {
			doError("write fial.", e);
			return 0;
		}
	}

	@Override
	public int read(ByteBuffer buffer) {
		try {
			return mChannel.read(buffer);
		} catch (IOException e) {
			doError("read fial.", e);
			return 0;
		}
	}
	
	@Override
	public void close() {
		try {
			mChannel.close();
		} catch (IOException e) {
			doError("close fial.", e);
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
			if ((mSelectorFlag & OP_WRITE) != 0) {
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
			if ((mSelectorFlag & OP_READ) != 0) {
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
