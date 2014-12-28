package org.kotemaru.android.async
;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public interface SelectorListener {
	public void onRegister(SocketChannel channel);
	public void onAccept(SelectionKey key);
	public void onConnect(SelectionKey key);
	public void onWritable(SelectionKey key);
	public void onReadable(SelectionKey key);
}
