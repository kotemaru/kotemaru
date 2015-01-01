package org.kotemaru.android.async.ssl;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public interface ErrorListener {
	public void onError(String msg, Throwable t);
}
