package org.kotemaru.android.async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;

import org.kotemaru.android.async.ssl.SelectorItem;
import org.kotemaru.android.async.ssl.SelectorItem.SelectorItemListener;

import android.util.Log;

/**
 * Selector実行スレッド。
 * - シングルトン。
 * - クラスが初期化されると長時間通信してやつが誤動作とかあるかなー？
 * @author kotemaru.org
 */
public class SelectorThread extends Thread {
	private static final String TAG = SelectorThread.class.getSimpleName();
	private static final int TIMEOUT = 10 * 1000; // ms
	private Selector mSelector;
	private volatile boolean mIsRunnable;
	private final LinkedList<OpenRequest> mOpenQueue = new LinkedList<OpenRequest>();
	private final LinkedList<SocketChannel> mPauseQueue = new LinkedList<SocketChannel>();

	static {
		if (BuildConfig.DEBUG) {
			java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
			java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
		}
	}

	private static volatile SelectorThread sInstance;

	public static SelectorThread getInstance() {
		synchronized (SelectorThread.class) {
			if (sInstance == null) {
				try {
					sInstance = new SelectorThread();
					sInstance.start();
				} catch (IOException e) {
					Log.e(TAG, "Fatal: Bad start SelectorThread:" + e, e);
					throw new Error("Bad start SelectorThread", e);
				}
			}
			return sInstance;
		}
	}

	private SelectorThread() throws IOException {
		super(TAG);
		mSelector = Selector.open();
		mIsRunnable = true;
	}
	public void close() throws IOException {
		mIsRunnable = false;
		mSelector.close();
		sInstance.interrupt();
		sInstance = null;
	}
	public Selector getSelector() {
		return mSelector;
	}

	/**
	 * 通信開始要求。
	 * @param host 接続先ホスト。
	 * @param port 接続先ポート。
	 * @param listener Selectorリスナ
	 */
	public synchronized void openSocketClient(String host, int port, boolean isSsl, SelectorItemListener listener) {
		mOpenQueue.add(new OpenRequest(host, port, isSsl, listener));
		mSelector.wakeup();
	}

	/**
	 * 通信開始要求の消化。
	 * - プールにChannelがあれば再利用する。
	 */
	private synchronized void doOpenRequest() {
		Iterator<OpenRequest> ite = mOpenQueue.iterator();
		while (ite.hasNext()) {
			OpenRequest req = ite.next();
			try {
				SocketAddress addr = new InetSocketAddress(req.host, req.port);
				SelectorItem selectorItem = SelectorItemPool.getInstance().getSelectorItem(addr,req.isSsl);
				SocketChannel channel = selectorItem.getChannel();
				req.listener = selectorItem;
				channel.configureBlocking(false);
				channel.register(mSelector, SelectionKey.OP_CONNECT, req);
				req.itemListener.onRegister(selectorItem);
				if (channel.isConnected()) {
					req.listener.onConnect(channel.keyFor(mSelector));
				} else {
					channel.connect(addr);
				}
			} catch (Exception e) {
				req.listener.onError("Open fail. " + req.host + ":" + req.port, e);
			}
			ite.remove();
		}
	}

	public synchronized void release(SocketChannel channel) {
		SelectionKey key = channel.keyFor(mSelector);
		key.attach(null);
		key.interestOps(0);
		mSelector.wakeup();
	}

	public synchronized void pause(SocketChannel channel, int ops) {
		//mPauseQueue.add(channel);
		SelectionKey key = channel.keyFor(mSelector);
		key.interestOps(key.interestOps() & ~ops);
		Log.d(TAG,"pause="+key.interestOps());
		mSelector.wakeup();
	}
	public synchronized void resume(SocketChannel channel, int ops) {
		SelectionKey key = channel.keyFor(mSelector);
		key.interestOps(key.interestOps() | ops);
		if (key.interestOps() != 0) {
			try {
				channel.register(mSelector, key.interestOps(), key.attachment());
			} catch (ClosedChannelException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		mSelector.wakeup();
	}
	private synchronized void doPauseRequest() {
		Iterator<SocketChannel> ite = mPauseQueue.iterator();
		while (ite.hasNext()) {
			SocketChannel channel = ite.next();
			SelectionKey key = channel.keyFor(mSelector);
			key.interestOps(0);
			ite.remove();
		}
	}

	@Override
	public void run() {
		try {
			while (mIsRunnable) {
				try {
					mainLoop();
				} catch (ConcurrentModificationException e) {
					// Note: NIO bug?
					Log.w(TAG, "Selector fail:ignore:" + e, e);
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "Selector fail:" + e, e);
			throw new Error(e);
		}
	}

	/**
	 * Selectorループ。
	 * @throws IOException
	 */
	public void mainLoop() throws IOException {
		while (mIsRunnable) {
			doOpenRequest();
			doPauseRequest();
			int n = mSelector.select(TIMEOUT);
			if (BuildConfig.DEBUG) {
				Log.v(TAG,"select:count="+n);
			}
			Iterator<SelectionKey> keys = mSelector.selectedKeys().iterator();
			while (keys.hasNext()) {
				SelectionKey key = keys.next();
				keys.remove();
				if (BuildConfig.DEBUG) {
					Log.v(TAG,"select:radyOps="+key.readyOps()+",attach="+key.attachment());
				}
				if (!key.isValid()) continue;
				OpenRequest attach = (OpenRequest) key.attachment();
				if (attach == null) {
					key.channel().close();
					key.cancel();
					continue;
				}
				SelectorListener listener = attach.listener;
				if (key.isAcceptable()) listener.onAccept(key);
				if (key.isConnectable()) listener.onConnect(key);
				if (key.isValid() && key.isReadable()) listener.onReadable(key);
				if (key.isValid() && key.isWritable()) listener.onWritable(key);
			}
		}
	}

	private static class OpenRequest {
		String host;
		int port;
		boolean isSsl;
		SelectorListener listener;
		SelectorItemListener itemListener;

		public OpenRequest(String host, int port, boolean isSsl, SelectorItemListener listener) {
			this.host = host;
			this.port = port;
			this.isSsl = isSsl;
			this.itemListener=listener;
		}
	}
}
