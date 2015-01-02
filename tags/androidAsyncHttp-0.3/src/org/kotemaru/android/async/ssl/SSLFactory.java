package org.kotemaru.android.async.ssl;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

public class SSLFactory {
	public static SSLFactory sInstance;

	public static SSLFactory getInstance() throws IOException {
		if (sInstance == null) {
			try {
				sInstance = new SSLFactory();
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		return sInstance;
	}

	private final SSLContext mContext;

	public SSLFactory() throws NoSuchAlgorithmException, KeyManagementException {
		mContext = SSLContext.getInstance("TLS");
		mContext.init(null, null, null);
	}

	public SSLSelectorItem getClient(SocketChannel channel) {
		SSLEngine engine = mContext.createSSLEngine();
		engine.setUseClientMode(true);
		return new SSLSelectorItem(engine, channel);
	}

}
