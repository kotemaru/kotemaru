package org.kotemaru.android.async;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ChannelPool {
	public static final String TAG = ChannelPool.class.getSimpleName();
	private static volatile ChannelPool sInstance;

	public static ChannelPool getInstance() {
		if (sInstance == null) {
			sInstance = new ChannelPool();
		}
		return sInstance;
	}

	private Map<SocketAddress, LinkedList<SocketChannel>> mChannelPool =
			new HashMap<SocketAddress, LinkedList<SocketChannel>>();

	public ChannelPool() {
	}

	public synchronized SocketChannel getChannel(SocketAddress addr) throws IOException {
		LinkedList<SocketChannel> list = mChannelPool.get(addr);
		if (list == null || list.isEmpty()) {
			return SocketChannel.open();
		}
		SocketChannel channel = list.pop();
		if (channel.isConnected()) return channel;
		return getChannel(addr);
	}

	public synchronized void releaseChannel(SocketChannel channel) {
		if (!channel.isConnected()) return;
		SocketAddress addr = channel.socket().getRemoteSocketAddress();
		LinkedList<SocketChannel> list = mChannelPool.get(addr);
		if (list == null) {
			list = new LinkedList<SocketChannel>();
			mChannelPool.put(addr, list);
		}
		list.push(channel);
	}

}
