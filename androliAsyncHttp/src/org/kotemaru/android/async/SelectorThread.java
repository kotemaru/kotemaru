package org.kotemaru.android.async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.kotemaru.android.async.http.BuildConfig;

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
	public void close() {
		mIsRunnable = false;
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
	public synchronized void openClient(String host, int port, SelectorListener listener) {
		mOpenQueue.add(new OpenRequest(host, port, listener));
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
				SocketChannel channel = ChannelPool.getInstance().getChannel(addr);
				channel.configureBlocking(false);
				channel.register(mSelector, channel.validOps(), req);
				req.listener.onRegister(channel);
				if (channel.isConnected()) {
					req.listener.onConnect(null);
				} else {
					channel.connect(addr);
				}
			} catch (Exception e){
				req.listener.onError("Open fail. "+req.host+":"+req.port, e);
			}
			ite.remove();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				try {
					mainLoop();
				} catch (ConcurrentModificationException e) {
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
			mSelector.select(TIMEOUT);
			Set<SelectionKey> keys = mSelector.selectedKeys();
			for (SelectionKey key : keys) {
				keys.remove(key);
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
		SelectorListener listener;

		public OpenRequest(String host, int port, SelectorListener listener) {
			this.host = host;
			this.port = port;
			this.listener = listener;
		}
	}
}
