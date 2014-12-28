package org.kotemaru.android.async.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.kotemaru.android.async.ChannelPool;
import org.kotemaru.android.async.SelectorListener;
import org.kotemaru.android.async.SelectorThread;
import org.kotemaru.android.async.http.ChunkedPartProcessor.ChunkedListener;
import org.kotemaru.android.async.http.HttpUtil.MethodType;

import android.util.Log;

public abstract class AsyncHttpRequest
		extends AbstractHttpMessage
		implements SelectorListener, HttpUriRequest {
	private static final String TAG = AsyncHttpRequest.class.getSimpleName();

	public enum State {
		PREPARE, CONNECT,
		REQUEST_HEADER, REQUSET_BODY,
		RESPONSE_WAIT, RESPONSE_HEADER, RESPONSE_BODY_PART, RESPONSE_BODY,
		DONE, ERROR
	}

	private static final int BUFFER_SIZE = 4096;

	private final byte[] mRawBuffer = new byte[BUFFER_SIZE];
	private final ByteBuffer mBuffer = ByteBuffer.wrap(mRawBuffer);
	private final PartByteArrayInputStream mResponseBodyStream = new PartByteArrayInputStream();
	private final ChunkedListener mChunkedListener = new ChunkedListener() {
		@Override
		public void onChunkedBlock(byte[] buffer, int offset, int length) {
			onResponseBodyPart(buffer, offset, length);
		}
		@Override
		public void onChunkedFinish() {
			onResponseBody();
		}
	};
	private final ChunkedPartProcessor mChunkedPartProcessor = new ChunkedPartProcessor(mChunkedListener);

	private State mState = State.PREPARE;
	private boolean mIsAborted;
	private SocketChannel mChannel;
	private URI mUri;

	private AsyncHttpListener mAsyncHttpListener;
	private BasicHttpResponse mHttpResponse;
	private InputStream mRequestContent;

	private boolean mIsChunkedMode;
	private AsyncHttpClient mHttpClient;

	public AsyncHttpRequest(URI uri) {
		mUri = uri;
	}
	public abstract MethodType getMethodType();
	public abstract HttpEntity getHttpEntity();

	void execute(AsyncHttpClient httpClient) throws IOException {
		mHttpClient = httpClient;
		SelectorThread selector = SelectorThread.getInstance();
		String host = mUri.getHost();
		int port = (mUri.getPort() == -1) ? 80 : mUri.getPort();
		this.setHeader(new BasicHeader("Host", host + ":" + port));
		// this.setHeader(new BasicHeader("Connection", "keep-alive"));
		selector.openClient(host, port, this);
	}
	public State getState() {
		return mState;
	}
	public void setState(State state) {
		mState = state;
	}

	public AsyncHttpListener getAsyncHttpListener() {
		return mAsyncHttpListener;
	}
	public void setAsyncHttpListener(AsyncHttpListener asyncHttpListener) {
		mAsyncHttpListener = asyncHttpListener;
	}
	// ---------------------------------
	@Override
	public void onAccept(SelectionKey key) {
		// not use.
	}

	@Override
	public void onRegister(SocketChannel channel) {
		Log.v(TAG, "onRegister:" + channel);
		mChannel = channel;
		if (mIsAborted) {
			abort();
		}
	}

	@Override
	public void onConnect(SelectionKey key) {
		Log.v(TAG, "onConnect:" + key);
		try {
			if (mChannel.isConnectionPending()) {
				mChannel.finishConnect();
			}
			if (key != null) {
				key.interestOps(SelectionKey.OP_WRITE);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		setState(State.CONNECT);
		mAsyncHttpListener.onConnect(this);

		mBuffer.clear();
		HttpEntity entity = getHttpEntity();
		if (entity != null) {
			addRequestHeader(entity.getContentEncoding());
			addRequestHeader(entity.getContentType());
			if (entity.getContentLength() >= 0) {
				addRequestHeader(new BasicHeader("Content-Length", Long.toString(entity.getContentLength())));
			}
		}
		HttpUtil.formatRequestHeader(mBuffer, getMethodType(), mUri, this, mHttpClient);
		mBuffer.flip();

		setState(State.REQUEST_HEADER);
	}

	@Override
	public void onWritable(SelectionKey key) {
		Log.v(TAG, "onWritable:" + key + ":" + mState);
		if (mState == State.REQUEST_HEADER) {
			if (mBuffer.hasRemaining()) {
				try {
					mChannel.write(mBuffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				mAsyncHttpListener.onRequestHeader(this);
				if (getHttpEntity() != null) {
					setState(State.REQUSET_BODY);
					try {
						mRequestContent = getHttpEntity().getContent();
					} catch (Exception e) {
						doError("Post entity fail", e);
					}
				} else {
					mBuffer.clear();
					setState(State.RESPONSE_WAIT);
					key.interestOps(SelectionKey.OP_READ);
					mAsyncHttpListener.onRequestBody(this);
				}
			}
		} else if (mState == State.REQUSET_BODY) {
			try {
				if (mAsyncHttpListener.isRequestBodyPart()) {
					ByteBuffer buffer = mAsyncHttpListener.onRequestBodyPart(mBuffer);
					if (buffer != null) {
						int n = mChannel.write(mBuffer);
					} else {
						mBuffer.clear();
						setState(State.RESPONSE_WAIT);
						key.interestOps(SelectionKey.OP_READ);
						mAsyncHttpListener.onRequestBody(this);
					}
				} else {
					mBuffer.clear();
					byte[] buff = mBuffer.array();
					int n = mRequestContent.read(buff);
					if (n > 0) {
						mBuffer.position(n);
						mBuffer.flip();
						n = mChannel.write(mBuffer);
						Log.e("DEBUG", "====>write size=" + n);
					}
					if (n == -1) {
						mBuffer.clear();
						setState(State.RESPONSE_WAIT);
						key.interestOps(SelectionKey.OP_READ);
						mAsyncHttpListener.onRequestBody(this);
					}
				}
			} catch (Exception e) {
				doError("Post entity fail", e);
			}
		}
	}

	private void addRequestHeader(Header header) {
		if (header != null) return;
		this.addHeader(header);
	}
	@Override
	public void onReadable(SelectionKey key) {
		Log.v(TAG, "onReadable:" + key + ":" + mState);
		if (mState == State.RESPONSE_WAIT || mState == State.RESPONSE_HEADER) {
			setState(State.RESPONSE_HEADER);
			int n = readIntoBuffer();
			if (n <= 0) {
				doError("Header reading fail", null);
				return;
			}
			mHttpResponse = HttpUtil.parseResponseHeader(mBuffer);
			if (mHttpResponse != null) {
				setState(State.RESPONSE_BODY);
				mIsChunkedMode = HttpUtil.hasChunkedTransferHeader(mHttpResponse);
				mAsyncHttpListener.onResponseHeader(mHttpResponse);
				mHttpClient.setCookies(mHttpResponse, mUri);
				if (mBuffer.remaining() > 0) {
					processBodyPart(key, n);
				}
			}
		} else if (mState == State.RESPONSE_BODY) {
			int n = readIntoBuffer();
			mBuffer.flip();
			processBodyPart(key, n);
		} else {
			doError("Bad state " + mState + " onReadable().", null);
		}
	}

	private void doError(String msg, Throwable err) {
		Log.e(TAG, msg, err);
		abort();
	}
	private void processBodyPart(SelectionKey key, int readSize) {
		byte[] buffer = mBuffer.array();
		int offset = mBuffer.position();
		int length = mBuffer.limit() - offset;

		if (length > 0) {
			if (mIsChunkedMode) {
				mChunkedPartProcessor.addPart(buffer, offset, length);
			} else {
				onResponseBodyPart(buffer, offset, length);
			}
		}
		if (readSize == -1) { // EOF
			onResponseBody();
			key.interestOps(0);
		}
		mBuffer.clear();
	}
	private void onResponseBodyPart(byte[] buffer, int offset, int length) {
		if (mAsyncHttpListener.isResponseBodyPart()) {
			mAsyncHttpListener.onResponseBodyPart(buffer, offset, length);
		} else {
			mResponseBodyStream.addPart(buffer, offset, length);
		}
	}
	private void onResponseBody() {
		setState(State.DONE);
		mHttpResponse.setEntity(new InputStreamEntity(mResponseBodyStream, mResponseBodyStream.getLength()));
		mAsyncHttpListener.onResponseBody(mHttpResponse);
		onFinish();
	}
	private void onFinish() {
		ChannelPool.getInstance().releaseChannel(mChannel);
	}

	private int readIntoBuffer() {
		try {
			int n = mChannel.read(mBuffer);
			Log.e("DEBUG", "===>read=" + n + ":" + mBuffer.remaining());
			return n;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public ProtocolVersion getProtocolVersion() {
		return HttpUtil.PROTOCOL_VERSION;
	}

	@Override
	public void abort() throws UnsupportedOperationException {
		mIsAborted = true;
		if (mChannel != null) {
			try {
				mChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public boolean isAborted() {
		return mIsAborted;
	}

	@Override
	public String getMethod() {
		return getMethodType().name();
	}
	@Override
	public URI getURI() {
		return mUri;
	}
}
