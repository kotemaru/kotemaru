package org.kotemaru.android.async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.kotemaru.android.async.http.BuildConfig;

import android.util.Log;

public class SelectorThread extends Thread {
	private static final String TAG = SelectorThread.class.getSimpleName();
	private static final int TIMEOUT = 5 * 1000; // ms
	private Selector mSelector;
	private volatile boolean mIsRunnable;
	private LinkedList<OpenRequest> mOpenQueue = new LinkedList<OpenRequest>();

	static {
		if (BuildConfig.DEBUG) {
			java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
			java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
		}
	}

	private static volatile SelectorThread sInstance;

	public static SelectorThread getInstance() {
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

	public synchronized void openClient(String host, int port, SelectorListener listener)  {
		mOpenQueue.add(new OpenRequest(host, port, listener));
		mSelector.wakeup();
	}
	private synchronized void doOpenRequest() throws IOException {
		Iterator<OpenRequest> ite = mOpenQueue.iterator();
		while (ite.hasNext()) {
			OpenRequest req = ite.next();
			SocketAddress addr = new InetSocketAddress(req.host, req.port);
			SocketChannel channel = ChannelPool.getInstance().getChannel(addr);
			channel.configureBlocking(false);
			channel.register(mSelector, channel.validOps(), req);
			req.mListener.onRegister(channel);
			if (channel.isConnected()) {
				req.mListener.onConnect(null);
			} else {
				channel.connect(addr);
			}
			ite.remove();
		}
	}

	@Override
	public void run() {
		try {
			mainLoop();
		} catch (IOException e) {
			Log.e(TAG, "Selector fail:" + e, e);
		}
	}
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
					Log.e("DEBUG","===>"+key.channel().isOpen());
					continue;
				}
				SelectorListener listener = attach.mListener;
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
		SelectorListener mListener;

		public OpenRequest(String host, int port, SelectorListener listener) {
			this.host = host;
			this.port = port;
			this.mListener = listener;
		}
	}
}
