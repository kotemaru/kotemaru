package org.kotemaru.android.async.ssl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.kotemaru.android.async.SelectorListener;

public interface SelectorItem extends SelectorListener {
	public static final int OP_ALL = -1;
	public static final int OP_NONE = 0;
	public static final int OP_READ = 1;
	public static final int OP_WRITE = 4;
	public interface SelectorItemListener 
		extends ErrorListener {
		public void onRegister(SelectorItem item);
		public void onConnect();
		public void onReadable() throws IOException;
		public void onWritable() throws IOException;
	}
	public SocketChannel getChannel();
	public boolean isConnected();

	public void setListener(SelectorItemListener listener);
	public void requireOn(int flag);
	public int write(ByteBuffer buffer);
	public int read(ByteBuffer buffer);
	public void release();
	public void close();

}

/**
 * app.init()->item.setWritableListener(app)
 * app.onWritable()<-item.onWritable(key)<-Selector
 * app.onWritable()->iteam.write()->channel.write()
 * 
 * 
 * 
 */
