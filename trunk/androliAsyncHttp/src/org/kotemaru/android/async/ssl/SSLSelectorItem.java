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
	private static final boolean IS_DEBUG = true;
	
	private final SocketChannel mChannel;
	private final SSLEngine mEngine;
	private SelectorItemListener mItemListener;
	private ByteBuffer mSSLWriteBuffer;
	private ByteBuffer mSSLReadBuffer;
	private ByteBuffer mPlainReadBuffer;
	private ByteBuffer mPlainWriteBuffer;
	private int mSlectFlag;
	private boolean mIsHandshaked = false;

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
		mSSLReadBuffer = ByteBuffer.allocate(session.getPacketBufferSize());
		mPlainWriteBuffer = ByteBuffer.allocate(session.getApplicationBufferSize());
		mPlainReadBuffer = ByteBuffer.allocate(session.getApplicationBufferSize());

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
		mSlectFlag = flag;
		if ((mSlectFlag & OP_READ) != 0) {
			SelectorThread.getInstance().resume(mChannel, SelectionKey.OP_READ);
		} else {
			SelectorThread.getInstance().pause(mChannel, SelectionKey.OP_READ);
		}
		if ((mSlectFlag & OP_WRITE) != 0) {
			SelectorThread.getInstance().resume(mChannel, SelectionKey.OP_WRITE);
		} else {
			SelectorThread.getInstance().pause(mChannel, SelectionKey.OP_WRITE);
		}
	}
	public void release() {
		SelectorThread.getInstance().release(mChannel);
	}

	public boolean doReadable() {
		if ((mSlectFlag & OP_READ) != 0 && mReadState == IOState.PLAIN) {
			try {
				mItemListener.onReadable();
				return true;
			} catch (IOException e) {
				doError("doReadable fail.", e);
			}
		}
		return false;
	}
	public boolean doWritable() {
		if ((mSlectFlag & OP_WRITE) != 0 && mWriteState == IOState.PLAIN) {
			try {
				mItemListener.onWritable();
				return true;
			} catch (IOException e) {
				doError("doWritable fail.", e);
			}
		}
		return false;
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
		Log.v(TAG,"onConnect:"+key);
		try {
			if (mChannel.isConnectionPending()) {
				if (!mChannel.finishConnect()) {
					// TODO:本当はリトライが必要。でもSelectorからだから大丈夫なのかも。
					throw new IOException("finishConnect() fail");
				}
			}
			SelectorThread.getInstance().pause(mChannel, OP_ALL);
			if (!doHandshake()) {
				onHandshakeFinish();
			}
		} catch (Exception e) {
			doError("Connection fail:", e);
		}
	}
	public void onHandshakeFinish() {
		Log.v(TAG,"onHandshakeFinish:");
		try {
			SelectorThread.getInstance().pause(mChannel, OP_ALL);
			mReadState = IOState.SSL;
			mWriteState = IOState.PLAIN;
			mItemListener.onConnect();
		} catch (Exception e) {
			doError("Connection fail:", e);
		}
	}
	@Override
	public void onWritable(SelectionKey key) {
		Log.v(TAG,"onWritable:"+mReadState+":"+key);
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
				while (mSSLWriteBuffer.hasRemaining() && doWritable());
			}
		} catch (IOException e) {
			doError("onWritable fail.", e);
		}
	}
	@Override
	public void onReadable(SelectionKey key) {
		Log.v(TAG,"onReadable:"+mReadState+":"+key);
		try {
			if (mReadState == IOState.HANDSHAKE) {
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
			Log.v(TAG,"onReadable:unwrap="+status);
			if (status == SSLEngineResult.Status.OK) {
				mReadState = IOState.PLAIN;
				mSSLReadBuffer.compact();
				mPlainReadBuffer.flip();
				while (mPlainReadBuffer.hasRemaining() && doReadable());
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

	//-----------------------------------------------------------------------------------------------
	// Handshake
	//-----------------------------------------------------------------------------------------------
	private boolean doHandshake() throws Exception {
		if (mIsHandshaked) return false;
		mEngine.beginHandshake();
		mSSLReadBuffer.clear();
		mPlainReadBuffer.clear();
		mPlainWriteBuffer.clear();
		mSSLWriteBuffer.clear();
		postNextHandshake(mEngine.getHandshakeStatus());
		return true;
	}
	private void doHandshakeUnwrap() throws IOException {
		int n = mChannel.read(mSSLReadBuffer);
		if (n < 0) {
			throw new IOException("Hand shake filed: : read EOF");
		}
		mSSLReadBuffer.flip();
		SSLEngineResult.HandshakeStatus hsStatus = null;
		SSLEngineResult result = null;
		while (mSSLReadBuffer.hasRemaining()) {
			result = mEngine.unwrap(mSSLReadBuffer, mPlainReadBuffer);
			if(IS_DEBUG) log("client unwrap: ", result);
			hsStatus = result.getHandshakeStatus();
			if (hsStatus == SSLEngineResult.HandshakeStatus.FINISHED) {
				break;
			}
			SSLEngineResult.Status state = result.getStatus();
			if (state == SSLEngineResult.Status.BUFFER_UNDERFLOW) break;
			if (state != SSLEngineResult.Status.OK) {
				throw new IOException("Hand shake filed: " + state);
			}
			hsStatus = doHandshakeTask();
		}
		mSSLReadBuffer.compact();
		postNextHandshake(hsStatus);
	}
	private void doHandshakeWrap() throws IOException {
		mSSLWriteBuffer.clear();
		SSLEngineResult result = mEngine.wrap(mPlainWriteBuffer, mSSLWriteBuffer);
		if(IS_DEBUG) log("client wrap: ", result);
		if (result.getStatus() == SSLEngineResult.Status.OK) {
			mSSLWriteBuffer.flip();
			while (mSSLWriteBuffer.hasRemaining()) {
				if (mChannel.write(mSSLWriteBuffer) < 0) {
					throw new IOException("Hand shake filed: write EOF");
				}
			}
		} else {
			throw new IOException("Hand shake filed:"+result.getStatus());
		}
		postNextHandshake(doHandshakeTask());
	}
	private SSLEngineResult.HandshakeStatus doHandshakeTask() throws IOException {
		Runnable runnable;
		while ((runnable = mEngine.getDelegatedTask()) != null) {
			if(IS_DEBUG) Log.v(TAG, "doHandshakeTask:" + runnable);
			runnable.run();
		}
		SSLEngineResult.HandshakeStatus hsStatus = mEngine.getHandshakeStatus();
		if (hsStatus == HandshakeStatus.NEED_TASK) {
			throw new IOException("handshake shouldn't need additional tasks");
		}
		return hsStatus;
	}

	private void postNextHandshake(SSLEngineResult.HandshakeStatus hsStatus) throws IOException {
		SelectorThread.getInstance().pause(mChannel, OP_ALL);
		if(IS_DEBUG) Log.d(TAG, "postNextHandshake=" + hsStatus);
		switch (hsStatus) {
		case NEED_WRAP:
			mWriteState = IOState.HANDSHAKE;
			SelectorThread.getInstance().resume(mChannel, SelectionKey.OP_WRITE);
			break;
		case NEED_UNWRAP:
			mReadState = IOState.HANDSHAKE;
			SelectorThread.getInstance().resume(mChannel, SelectionKey.OP_READ);
			break;
		case NEED_TASK:
			hsStatus = doHandshakeTask();
			postNextHandshake(hsStatus);
			break;
		case FINISHED:
			mIsHandshaked = true;
			mSSLWriteBuffer.clear();
			mSSLReadBuffer.clear();
			mPlainWriteBuffer.clear();
			mPlainReadBuffer.clear();
			onHandshakeFinish();
		default:
			break;
		}
	}

	private static void log(String str, SSLEngineResult result) {
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
