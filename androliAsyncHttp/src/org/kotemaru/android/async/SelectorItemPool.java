package org.kotemaru.android.async;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.kotemaru.android.async.ssl.SSLFactory;
import org.kotemaru.android.async.ssl.SSLSelectorItem;

import android.util.Log;

/**
 * SelectorItemのプール。
 * - シングルトン。クラスが初期化されてもプールされていたChennelが使えないだけ。
 * @author kotemaru.org
 */
public class SelectorItemPool {
	public static final String TAG = SelectorItemPool.class.getSimpleName();
	private static volatile SelectorItemPool sInstance;

	public static SelectorItemPool getInstance() {
		synchronized (SelectorItemPool.class) {
			if (sInstance == null) {
				sInstance = new SelectorItemPool();
			}
			return sInstance;
		}
	}

	private Map<SocketAddress, LinkedList<SelectorItem>> mPlainPool =
			new HashMap<SocketAddress, LinkedList<SelectorItem>>();
	private Map<SocketAddress, LinkedList<SelectorItem>> mSslPool =
			new HashMap<SocketAddress, LinkedList<SelectorItem>>();

	public SelectorItemPool() {
	}

	public synchronized SelectorItem getSelectorItem(SocketAddress addr, boolean isSsl) throws IOException {
		if (isSsl) {
			return getSSLSelectorItem(addr);
		} else {
			return getPlainSelectorItem(addr);
		}
	}

	public synchronized void releaseSelectorItem(SelectorItem item) {
		if (!item.isConnected()) return;
		if (item instanceof SSLSelectorItem) {
			releaseSelectorItem((SSLSelectorItem) item);
		} else {
			releaseSelectorItem((PlainSelectorItem) item);
		}
	}

	private synchronized SelectorItem getPlainSelectorItem(SocketAddress addr) throws IOException {
		LinkedList<SelectorItem> list = mPlainPool.get(addr);
		if (list == null || list.isEmpty()) {
			Log.d(TAG, "crate Plain session");
			SocketChannel channel = SocketChannel.open();
			PlainSelectorItem item = new PlainSelectorItem(channel);
			return item;
		}
		SelectorItem channel = list.pop();
		if (channel.isConnected()) return channel;
		return getPlainSelectorItem(addr);
	}

	private synchronized void releaseSelectorItem(PlainSelectorItem item) {
		if (!item.isConnected()) return;
		SelectorThread.getInstance().release(item.getChannel());
		SocketAddress addr = item.getChannel().socket().getRemoteSocketAddress();
		LinkedList<SelectorItem> list = mPlainPool.get(addr);
		if (list == null) {
			list = new LinkedList<SelectorItem>();
			mPlainPool.put(addr, list);
		}
		list.push(item);
	}

	private synchronized SelectorItem getSSLSelectorItem(SocketAddress addr) throws IOException {
		LinkedList<SelectorItem> list = mSslPool.get(addr);
		if (list == null || list.isEmpty()) {
			Log.d(TAG, "crate SSL session");
			SocketChannel channel = SocketChannel.open();
			SSLSelectorItem item = SSLFactory.getInstance().getClient(channel);
			return item;
		}
		SelectorItem channel = list.pop();
		if (channel.isConnected()) return channel;
		return getSSLSelectorItem(addr);
	}

	private synchronized void releaseSelectorItem(SSLSelectorItem item) {
		if (!item.isConnected()) return;
		SelectorThread.getInstance().release(item.getChannel());
		SocketAddress addr = item.getChannel().socket().getRemoteSocketAddress();
		LinkedList<SelectorItem> list = mSslPool.get(addr);
		if (list == null) {
			list = new LinkedList<SelectorItem>();
			mSslPool.put(addr, list);
		}
		list.push(item);
	}

}
