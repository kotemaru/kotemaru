package org.kotemaru.android.async.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.kotemaru.android.async.ChannelPool;
import org.kotemaru.android.async.SelectorListener;
import org.kotemaru.android.async.SelectorThread;
import org.kotemaru.android.async.http.body.ChunkedPartReader;
import org.kotemaru.android.async.http.body.ChunkedPartWriter;
import org.kotemaru.android.async.http.body.PartReader;
import org.kotemaru.android.async.http.body.PartReader.PartReaderListener;
import org.kotemaru.android.async.http.body.PartWriter;
import org.kotemaru.android.async.http.body.PartWriter.PartWriterListener;
import org.kotemaru.android.async.http.body.StreamPartReader;
import org.kotemaru.android.async.http.body.StreamPartWriter;
import org.kotemaru.android.async.util.PartByteArrayInputStream;

import android.util.Log;

/**
 * HTTPリスエストの共通部。
 * @author kotemaru.org
 */
public abstract class AsyncHttpRequest
		extends AbstractHttpMessage
		implements SelectorListener, HttpUriRequest {
	private static final String TAG = AsyncHttpRequest.class.getSimpleName();
	private static final boolean IS_DEBUG = BuildConfig.DEBUG;

	public enum MethodType {
		GET, POST
	}

	public enum State {
		PREPARE, CONNECT,
		REQUEST_HEADER, REQUSET_BODY,
		RESPONSE_HEADER, RESPONSE_BODY,
		DONE, ERROR
	}

	private AsyncHttpClient mHttpClient;
	private AsyncHttpListener mAsyncHttpListener;

	private BasicHttpResponse mHttpResponse;
	private PartByteArrayInputStream mResponseBodyStream;
	private InputStream mRequestContent;
	private PartWriter mRequestBodyWriter;
	private PartReader mResponseBodyReader;

	private int mBufferSize = 4096;
	private ByteBuffer mBuffer;
	private SocketChannel mChannel;

	private State mState = State.PREPARE;
	private boolean mIsAborted;
	private URI mUri;

	public AsyncHttpRequest() {
	}
	public AsyncHttpRequest(URI uri) {
		mUri = uri;
	}

	/**
	 * リクエスト開始。
	 * - 開始要求するだけで通信は行わない。
	 * @param httpClient 親クライアント
	 * @param listener
	 * @throws IOException 初期化に失敗。
	 */
	public void execute(AsyncHttpClient httpClient, AsyncHttpListener listener) throws IOException {
		initState();
		mHttpClient = httpClient;
		mAsyncHttpListener = listener;

		SelectorThread selector = SelectorThread.getInstance();
		String host = mUri.getHost();
		int port = (mUri.getPort() == -1) ? 80 : mUri.getPort();
		this.setHeader(new BasicHeader("Host", host + ":" + port));
		selector.openClient(host, port, this);
	}

	/**
	 * 内部状態の取得。エラー処理のヒントに使用。
	 * @return 内部状態。
	 */
	public State getState() {
		return mState;
	}

	public URI getUri() {
		return mUri;
	}
	public void setUri(URI uri) {
		mUri = uri;
	}

	public int getBufferSize() {
		return mBufferSize;
	}
	/**
	 * 内部バッファのサイズを設定。
	 * - 初期値は 4096。
	 * - 送受信共にHTTPヘッダはこのサイズに制限される。
	 * @param bufferSize
	 */
	public void setBufferSize(int bufferSize) {
		mBufferSize = bufferSize;
	}

	// -----------------------------------------------------------------------------
	// abstract methods.
	// -----------------------------------------------------------------------------
	public abstract MethodType getMethodType();
	public abstract HttpEntity getHttpEntity();

	// -----------------------------------------------------------------------------
	// for implements org.apache.http.message.SelectorListener
	// -----------------------------------------------------------------------------
	@Override
	public ProtocolVersion getProtocolVersion() {
		return HttpUtil.PROTOCOL_VERSION;
	}

	@Override
	public void abort() throws UnsupportedOperationException {
		mIsAborted = true;
		if (mChannel != null) {
			doFinish(true);
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

	// -----------------------------------------------------------------------------
	// for implements org.kotemaru.android.async.HttpMessage
	// -----------------------------------------------------------------------------
	@Override
	public void onAccept(SelectionKey key) {
		// not use.
	}

	@Override
	public void onRegister(SocketChannel channel) {
		if (IS_DEBUG) Log.v(TAG, "onRegister:" + channel);
		mChannel = channel;
		if (mIsAborted) {
			abort();
		}
	}

	@Override
	public void onConnect(SelectionKey key) {
		if (IS_DEBUG) Log.v(TAG, "onConnect:" + key);
		setState(State.CONNECT, null, 0);
		try {
			if (mChannel.isConnectionPending()) {
				mChannel.finishConnect();
			}
		} catch (IOException e) {
			doError("Connection fail:", e);
			return;
		}
		mAsyncHttpListener.onConnect(this);  // do Callback.
		doRequestHeader(key);
	}

	@Override
	public void onWritable(SelectionKey key) {
		if (IS_DEBUG) Log.v(TAG, "onWritable:" + key + ":" + mState);
		if (mState == State.REQUEST_HEADER) {
			if (mBuffer.hasRemaining()) {
				writeFromBuffer(mBuffer);
			} else {
				doRequestBody(key);
			}
		} else if (mState == State.REQUSET_BODY) {
			try {
				int n = mRequestBodyWriter.doWrite();
				if (n < 0) {
					doResponseHeader(key);
				}
			} catch (Exception e) {
				doError("Post entity fail", e);
			}
		} else {
			doError("Bad state " + mState + " onWritable().", null);
		}
	}

	@Override
	public void onReadable(SelectionKey key) {
		if (IS_DEBUG) Log.v(TAG, "onReadable:" + key + ":" + mState);
		if (mState == State.RESPONSE_HEADER) {
			readIntoBuffer(mBuffer);
			mHttpResponse = HttpUtil.parseResponseHeader(mBuffer);
			if (mHttpResponse != null) {
				doResponseBody(key);
			}
		} else if (mState == State.RESPONSE_BODY) {
			readIntoBuffer(mBuffer);
			mBuffer.flip();
			doResponseBodyPart();
		} else {
			doError("Bad state " + mState + " onReadable().", null);
		}
	}

	@Override
	public void onError(String msg, Throwable t) {
		doError(msg, t);
	}

	// -----------------------------------------------------------------------------
	// for internal logic
	// -----------------------------------------------------------------------------
	private void doRequestHeader(SelectionKey key) {
		mBuffer.clear();
		HttpEntity entity = getHttpEntity();
		if (entity != null) {
			addRequestHeader(entity.getContentEncoding());
			addRequestHeader(entity.getContentType());
			if (entity.getContentLength() >= 0) {
				addRequestHeader(new BasicHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(entity.getContentLength())));
			} else {
				addRequestHeader(new BasicHeader(HttpHeaders.TRANSFER_ENCODING, HttpUtil.CHUNKED));
			}
		}
		HttpUtil.formatRequestHeader(mBuffer, getMethodType(), mUri, this, mHttpClient);
		mBuffer.flip();
		setState(State.REQUEST_HEADER, key, SelectionKey.OP_WRITE);
	}

	private void doRequestBody(SelectionKey key) {
		mAsyncHttpListener.onRequestHeader(this);  // do Callback.

		HttpEntity entity = getHttpEntity();
		if (entity != null) {
			try {
				mRequestContent = getHttpEntity().getContent();
			} catch (Exception e) {
				doError("Post entity fail", e);
			}
			if (entity.getContentLength() >= 0) {
				mRequestBodyWriter = new StreamPartWriter(mChannel, mPartWriterListener);
			} else {
				mRequestBodyWriter = new ChunkedPartWriter(mChannel, mPartWriterListener);
			}
			mBuffer.clear();
			setState(State.REQUSET_BODY, key, SelectionKey.OP_WRITE);
		} else {
			doResponseHeader(key);
		}
	}

	private final PartWriterListener mPartWriterListener = new PartWriterListener() {
		@Override
		public ByteBuffer onNextBuffer() throws IOException {
			mBuffer.clear();
			if (mAsyncHttpListener.isRequestBodyPart()) {
				return mAsyncHttpListener.onRequestBodyPart(mBuffer);  // do Callback.
			} else {
				byte[] buff = mBuffer.array();
				int readSize = mRequestContent.read(buff);
				if (readSize > 0) {
					mBuffer.position(readSize);
					mBuffer.flip();
					return mBuffer;
				} else {
					return null;
				}
			}
		}
	};

	private void doResponseHeader(SelectionKey key) {
		if (mRequestContent != null) {
			mAsyncHttpListener.onRequestBody(this);  // do Callback.
		}
		mBuffer.clear();
		setState(State.RESPONSE_HEADER, key, SelectionKey.OP_READ);
	}
	private void doResponseBody(SelectionKey key) {
		mAsyncHttpListener.onResponseHeader(mHttpResponse);  // do Callback.

		mHttpClient.setCookies(mHttpResponse, mUri);
		boolean isChunked = HttpUtil.hasChunkedTransferHeader(mHttpResponse);
		if (isChunked) {
			mResponseBodyReader = new ChunkedPartReader(mPartReaderListener);
		} else {
			long contentLength = HttpUtil.getContentLength(mHttpResponse);
			mResponseBodyReader = new StreamPartReader(mPartReaderListener, contentLength);
		}
		if (!mAsyncHttpListener.isResponseBodyPart()) {
			mResponseBodyStream = new PartByteArrayInputStream();
		}
		if (mBuffer.remaining() > 0) {
			doResponseBodyPart();
		}
		setState(State.RESPONSE_BODY, key, SelectionKey.OP_READ);
	}

	private final PartReaderListener mPartReaderListener = new PartReaderListener() {
		@Override
		public void onPart(byte[] buffer, int offset, int length) {
			if (mAsyncHttpListener.isResponseBodyPart()) {
				mAsyncHttpListener.onResponseBodyPart(buffer, offset, length);  // do Callback.
			} else {
				mResponseBodyStream.addPart(buffer, offset, length);
			}
		}
		@Override
		public void onFinish() {
			doResponseBody();
		}
	};

	private void doResponseBodyPart() {
		byte[] buffer = mBuffer.array();
		int offset = mBuffer.position();
		int length = mBuffer.limit() - offset;
		mResponseBodyReader.postPart(buffer, offset, length);
		mBuffer.clear();
	}
	private void doResponseBody() {
		BasicHttpEntity entity = new BasicHttpEntity();
		entity.setContent(mResponseBodyStream);
		if (mResponseBodyStream != null) {
			entity.setContentLength(mResponseBodyStream.getLength());
		}
		entity.setContentEncoding(mHttpResponse.getFirstHeader("Content-Encoding"));
		entity.setContentType(mHttpResponse.getFirstHeader("Content-Type"));

		mHttpResponse.setEntity(new InputStreamEntity(mResponseBodyStream, mResponseBodyStream.getLength()));
		mAsyncHttpListener.onResponseBody(mHttpResponse);  // do Callback.
		doFinish(false);
	}

	private void doError(String msg, Throwable err) {
		Log.e(TAG, msg, err);
		mState = State.ERROR;
		abort();
		mAsyncHttpListener.onError(msg, err);
	}
	private void doFinish(boolean isDisconnect) {
		Selector selector = SelectorThread.getInstance().getSelector();
		SelectionKey key = mChannel.keyFor(selector);
		setState(State.DONE, key, 0);
		key.attach(null);
		if (isDisconnect) {
			try {
				mChannel.close();
				key.cancel();
			} catch (IOException e) {
				Log.e(TAG, "Channel close error. ignore.", e);
			}
		} else {
			ChannelPool.getInstance().releaseChannel(mChannel);
		}
		clearState();
	}
	// -----------------------------------------------------------------------------
	// for internal util.
	// -----------------------------------------------------------------------------
	private void setState(State state, SelectionKey key, int ops) {
		mState = state;
		if (key != null) {
			key.interestOps(ops);
		}
	}
	private void initState() throws IOException {
		if (mState != State.PREPARE && mState != State.DONE && mState != State.ERROR) {
			throw new IOException("Can not reuse. Bad status " + mState);
		}
		mState = State.PREPARE;
		mIsAborted = false;
		if (mBuffer == null || mBuffer.capacity() != mBufferSize) {
			mBuffer = ByteBuffer.wrap(new byte[mBufferSize]);
		}
		clearState();
	}
	private void clearState() {
		mHttpClient = null;
		mResponseBodyStream = null;
		mChannel = null;
		mAsyncHttpListener = null;
		mHttpResponse = null;
		mRequestContent = null;
	}

	private void addRequestHeader(Header header) {
		if (header != null) return;
		this.addHeader(header);
	}

	private int readIntoBuffer(ByteBuffer buffer) {
		try {
			int n = mChannel.read(buffer);
			return n;
		} catch (IOException e) {
			doError("Read fail:", e);
			return -1;
		}
	}
	private int writeFromBuffer(ByteBuffer buffer) {
		try {
			int n = mChannel.write(buffer);
			return n;
		} catch (IOException e) {
			doError("Write fail:", e);
			return -1;
		}
	}

}
