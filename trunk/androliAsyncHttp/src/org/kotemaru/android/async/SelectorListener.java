package org.kotemaru.android.async
;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * SocketThread用のリスナ。
 * @author kotemaru.org
 */
public interface SelectorListener {
	/**
	 * ChannelがSelectorに登録された。
	 * @param channel
	 */
	public void onRegister(SocketChannel channel);
	
	public void onAccept(SelectionKey key);
	public void onConnect(SelectionKey key);
	public void onWritable(SelectionKey key);
	public void onReadable(SelectionKey key);
	public void onError(String msg, Throwable t);
}
