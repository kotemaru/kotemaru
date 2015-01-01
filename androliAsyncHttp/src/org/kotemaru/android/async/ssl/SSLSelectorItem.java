package org.kotemaru.android.async.ssl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

import org.kotemaru.android.async.SelectorListener;
import org.kotemaru.android.async.SelectorThread;

import android.annotation.SuppressLint;
import android.util.Log;

public class SSLSelectorItem implements SelectorItem, SelectorListener {
	private static final String TAG = SSLSelectorItem.class.getSimpleName();
	private final SocketChannel mChannel;
	private final SSLEngine mEngine;
	private SelectorItemListener mItemListener;
	private ByteBuffer mSSLWriteBuffer;
	private ByteBuffer mSSLReadBuffer;
	private ByteBuffer mPlainReadBuffer;
	private ByteBuffer mPlainWriteBuffer;
	private int mFlag;

	enum IOState {
		SSL, PLAIN, CLOSE, HANDSHAKE
	}

	private IOState mReadState = IOState.SSL;
	private IOState mWriteState = IOState.PLAIN;

	public SSLSelectorItem(SSLEngine engine, SocketChannel channel) {
		mEngine = engine;
		mChannel = channel;

		SSLSession session = mEngine.getSession();
		mSSLWriteBuffer = ByteBuffer.allocate(session.getPacketBufferSize());
		mSSLReadBuffer = ByteBuffer.allocate(session.getPacketBufferSize() * 3);
		mPlainWriteBuffer = ByteBuffer.allocate(session.getApplicationBufferSize());
		mPlainReadBuffer = ByteBuffer.allocate(session.getApplicationBufferSize() * 3);

	}
	@Override
	public SocketChannel getChannel() {
		return mChannel;
	}

	@SuppressLint("TrulyRandom")
	// @see http://android-developers.blogspot.jp/2013/08/some-securerandom-thoughts.html
	public int write(ByteBuffer buffer) {
		if (mWriteState == IOState.CLOSE) return -1;
		if (mWriteState != IOState.PLAIN) return 0; // mSSLWriteBuffer is busy.

		try {
			int maxLen = buffer.remaining();
			SSLEngineResult res = mEngine.wrap(buffer, mSSLWriteBuffer);
			switch (res.getStatus()) {
			case CLOSED:
				mWriteState = IOState.CLOSE;
				return -1;
			case BUFFER_OVERFLOW:
				throw new IOException("SSL Buffer overflow.");
			case OK:
				mWriteState = IOState.SSL;
				SelectorThread.getInstance().resume(mChannel, SelectionKey.OP_WRITE);
				mSSLWriteBuffer.flip();
				buffer.position(buffer.limit());
				return maxLen;
			default: // BUFFEER_UNDERFLOW
				buffer.position(buffer.limit());
				return maxLen;
			}
		} catch (IOException e) {
			doError("write fail.", e);
			return 0;
		}
	}

	public int read(ByteBuffer buffer) {
		if (mReadState == IOState.CLOSE) return -1;
		if (mReadState != IOState.PLAIN) return 0;

		int len = Math.min(buffer.remaining(), mPlainReadBuffer.remaining());
		buffer.put(mPlainReadBuffer.array(), mPlainReadBuffer.position(), len);
		mPlainReadBuffer.position(mPlainReadBuffer.position() + len);
		if (!mPlainReadBuffer.hasRemaining()) {
			mReadState = IOState.SSL;
			mPlainReadBuffer.clear();
		}
		return len;
	}

	private void closeRead() throws SSLException {
		mReadState = IOState.CLOSE;
		mEngine.closeInbound();
	}
	private void closeWrite() throws SSLException {
		mWriteState = IOState.CLOSE;
		mEngine.closeInbound();
	}

	public void close() {
		try {
			closeWrite();
			closeRead();
		} catch (SSLException e) {
			doError("close fail.", e);
		}
	}

	private void doError(String msg, Throwable err) {
		Log.w(TAG, msg, err);
		if (mItemListener != null) {
			mItemListener.onError(msg, err);
		}
	}
	@Override
	public void onError(String msg, Throwable t) {
		doError(msg, t);
	}
	@Override
	public void setListener(SelectorItemListener listener) {
		mItemListener = listener;
	}
	@Override
	public void requireOn(int flag) {
		mFlag = flag;
		if ((mFlag & OP_READ) != 0) {
			SelectorThread.getInstance().resume(mChannel, SelectionKey.OP_READ);
		} else {
			SelectorThread.getInstance().pause(mChannel, SelectionKey.OP_READ);
		}
		if ((mFlag & OP_WRITE) != 0) {
			SelectorThread.getInstance().resume(mChannel, SelectionKey.OP_WRITE);
		} else {
			SelectorThread.getInstance().pause(mChannel, SelectionKey.OP_WRITE);
		}
	}
	public void release() {
		SelectorThread.getInstance().release(mChannel);
	}

	public void doReadable() {
		if ((mFlag & OP_READ) != 0 && mReadState == IOState.PLAIN) {
			try {
				mItemListener.onReadable();
			} catch (IOException e) {
				doError("doReadable fail.", e);
			}
		}
	}
	public void doWritable() {
		if ((mFlag & OP_WRITE) != 0 && mWriteState == IOState.PLAIN) {
			try {
				mItemListener.onWritable();
			} catch (IOException e) {
				doError("doWritable fail.", e);
			}
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
		try {
			if (mChannel.isConnectionPending()) {
				if (!mChannel.finishConnect()) {
					// TODO:本当はリトライが必要。でもSelectorからだから大丈夫なのかも。
					throw new IOException("finishConnect() fail");
				}
			}
			if (!doHandshake()) {
				SelectorThread.getInstance().pause(mChannel, OP_ALL);
				mItemListener.onConnect();
			}
		} catch (Exception e) {
			doError("Connection fail:", e);
		}
	}
	@Override
	public void onWritable(SelectionKey key) {
		try {
			if (mWriteState == IOState.HANDSHAKE) {
				doHandshakeWrap();
			}
			if (mWriteState != IOState.SSL) {
				doWritable();
				return;
			}
			int n = mChannel.write(mSSLWriteBuffer);
			if (n == -1) {
				closeWrite();
				return;
			}
			if (!mSSLWriteBuffer.hasRemaining()) {
				mWriteState = IOState.PLAIN;
				mSSLWriteBuffer.clear();
			}
		} catch (IOException e) {
			doError("onWritable fail.", e);
		}
	}
	@Override
	public void onReadable(SelectionKey key) {
		try {
			if (mWriteState == IOState.HANDSHAKE) {
				doHandshakeUnwrap();
			}
			if (mReadState != IOState.SSL) {
				doReadable();
				return;
			}

			int n = mChannel.read(mSSLReadBuffer);
			if (n == -1) {
				closeRead();
				return;
			}
			mSSLReadBuffer.flip();
			SSLEngineResult res = mEngine.unwrap(mSSLReadBuffer, mPlainReadBuffer);
			SSLEngineResult.Status status = res.getStatus();
			if (status == SSLEngineResult.Status.OK) {
				mReadState = IOState.PLAIN;
				mSSLReadBuffer.compact();
			} else if (status == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
				mSSLReadBuffer.compact();
			} else if (status == SSLEngineResult.Status.CLOSED) {
				closeRead();
			} else {
				throw new IOException("Bad unwrap status " + status);
			}
		} catch (IOException e) {
			doError("onReadable fail.", e);
		}
	}
	@Override
	public boolean isConnected() {
		return mChannel.isConnected();
	}

	private boolean doHandshake() throws Exception {
		SSLEngineResult.HandshakeStatus hsStatus = mEngine.getHandshakeStatus();
		Log.v(TAG, "Handshake=" + hsStatus);
		if (hsStatus == SSLEngineResult.HandshakeStatus.FINISHED) {
			return false;
		}
		mEngine.beginHandshake();
		postNestHandshake();
		return true;
	}
	private void doHandshakeUnwrap() throws IOException {
		Log.v(TAG, "doHandshakeUnwrap");
		mSSLReadBuffer.clear();
		mPlainReadBuffer.clear();
		SSLEngineResult.Status state = SSLEngineResult.Status.BUFFER_UNDERFLOW;
		// while (state == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
		Log.v(TAG, "mChannel.read()--=" + mSSLReadBuffer.remaining());
		int n = mChannel.read(mSSLReadBuffer);
		Log.v(TAG, "mChannel.read()=" + n);
		if (n < 0) {
			doError("Hand shake filed: read EOF", new Error());
		}
		mSSLReadBuffer.flip();
		SSLEngineResult res = mEngine.unwrap(mSSLReadBuffer, mPlainReadBuffer);
		log("client unwrap: ", res);
		mSSLReadBuffer.compact();
		state = res.getStatus();
		// }
		if (state != SSLEngineResult.Status.OK && state != SSLEngineResult.Status.BUFFER_UNDERFLOW) {
			throw new IOException("Hand shake filed: " + state);
		}
		postNestHandshake();
		doHandshakeTask();
	}
	
	private void postNestHandshake() {
		SelectorThread.getInstance().pause(mChannel, SelectionKey.OP_READ);
		SelectorThread.getInstance().pause(mChannel, SelectionKey.OP_WRITE);
		SSLEngineResult.HandshakeStatus  hsStatus = mEngine.getHandshakeStatus();
		Log.d(TAG,"next="+hsStatus);
		switch (hsStatus) {
			case NEED_WRAP:
				mWriteState = IOState.HANDSHAKE;
				SelectorThread.getInstance().resume(mChannel, SelectionKey.OP_WRITE);
				break;
			case NEED_UNWRAP:
				mReadState = IOState.HANDSHAKE;
				SelectorThread.getInstance().resume(mChannel, SelectionKey.OP_READ);
				break;
			case FINISHED:
				mReadState = IOState.SSL;
				mWriteState = IOState.PLAIN;
			default:
				break;
		}
	}
	private void doHandshakeWrap() throws IOException {
		Log.v(TAG, "doHandshakeWrap");
		mPlainWriteBuffer.clear();
		mSSLWriteBuffer.clear();
		SSLEngineResult res = mEngine.wrap(mPlainWriteBuffer, mSSLWriteBuffer);
		log("client wrap: ", res);
		if (res.getStatus() == SSLEngineResult.Status.OK) {
			mSSLWriteBuffer.flip();
			while (mSSLWriteBuffer.hasRemaining()) {
				if (mChannel.write(mSSLWriteBuffer) < 0) {
					throw new IOException("Hand shake filed");
				}
			}
		} else {
			throw new IOException("Hand shake filed");
		}
		doHandshakeTask();
		postNestHandshake();
	}
	private void doHandshakeTask() throws IOException {
		Runnable runnable;
		while ((runnable = mEngine.getDelegatedTask()) != null) {
			runnable.run();
		}
		SSLEngineResult.HandshakeStatus  hsStatus = mEngine.getHandshakeStatus();
		if (hsStatus == HandshakeStatus.NEED_TASK) {
			throw new IOException("handshake shouldn't need additional tasks");
		}
	}

	private void doHandshake2() throws Exception {
		ByteBuffer plainWriteBuffer = ByteBuffer.allocate(mEngine.getSession().getApplicationBufferSize());
		plainWriteBuffer.put("test".getBytes());
		while (true) {
			SSLEngineResult res = mEngine.wrap(plainWriteBuffer, mSSLWriteBuffer);
			log("client wrap: ", res);
			runDelegatedTasks(res, mEngine);
			mSSLWriteBuffer.flip();
			mChannel.write(mSSLWriteBuffer);

			mSSLReadBuffer.clear();
			int n = mChannel.read(mSSLReadBuffer);
			Log.e("DEBUG", "n=" + n);
			res = mEngine.unwrap(mSSLReadBuffer, mPlainReadBuffer);
			log("client unwrap: ", res);
			runDelegatedTasks(res, mEngine);

			mSSLWriteBuffer.compact();
			mSSLReadBuffer.compact();

			HandshakeStatus hsStatus = res.getHandshakeStatus();
			if (hsStatus == HandshakeStatus.FINISHED) break;
		}

	}
	private static void runDelegatedTasks(SSLEngineResult result,
			SSLEngine engine) throws Exception {

		if (result.getHandshakeStatus() == HandshakeStatus.NEED_TASK) {
			Runnable runnable;
			while ((runnable = engine.getDelegatedTask()) != null) {
				Log.v(TAG, "\trunning delegated task...");
				runnable.run();
			}
			HandshakeStatus hsStatus = engine.getHandshakeStatus();
			if (hsStatus == HandshakeStatus.NEED_TASK) {
				throw new Exception(
						"handshake shouldn't need additional tasks");
			}
			Log.v(TAG, "\tnew HandshakeStatus: " + hsStatus);
		}
	}

	private static boolean resultOnce = true;

	private static void log(String str, SSLEngineResult result) {
		if (resultOnce) {
			resultOnce = false;
			System.out.println("The format of the SSLEngineResult is: \n" +
					"\t\"getStatus() / getHandshakeStatus()\" +\n" +
					"\t\"bytesConsumed() / bytesProduced()\"\n");
		}
		HandshakeStatus hsStatus = result.getHandshakeStatus();
		Log.v(TAG, str +
				result.getStatus() + "/" + hsStatus + ", " +
				result.bytesConsumed() + "/" + result.bytesProduced() +
				" bytes");
		if (hsStatus == HandshakeStatus.FINISHED) {
			Log.v(TAG, "\t...ready for application data");
		}
	}

}
