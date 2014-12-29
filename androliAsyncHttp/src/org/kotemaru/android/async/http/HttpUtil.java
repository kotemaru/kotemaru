package org.kotemaru.android.async.http;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpMessage;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;
import org.kotemaru.android.async.http.AsyncHttpRequest.MethodType;

public class HttpUtil {
	public static final ProtocolVersion PROTOCOL_VERSION = new ProtocolVersion("HTTP", 1, 1);
	public static final byte CR = '\r';
	public static final byte LF = '\n';
	public static final byte[] CRLF = "\r\n".getBytes();
	public static final byte[] HTTP11 = " HTTP/1.1".getBytes();
	public static final byte[] COOKIE = "Cookie:".getBytes();
	public static final String LATIN_1 = "ISO-8859-1";

	public static final String SET_COOKIE = "Set-Cookie";
	public static final String CHUNKED = "chunked";


	public static boolean hasChunkedTransferHeader(HttpMessage httpMessage) {
		Header[] headers = httpMessage.getHeaders(HttpHeaders.TRANSFER_ENCODING);
		for (Header header : headers) {
			if (header.getValue() == null) continue;
			String val = header.getValue().toLowerCase(Locale.US).trim();
			if (val.startsWith(CHUNKED)) return true;
		}
		return false;
	}
	public static long getContentLength(HttpMessage httpMessage) {
		Header header = httpMessage.getFirstHeader(HttpHeaders.CONTENT_LENGTH);
		if (header == null || header.getValue() == null) return -1;
		try {
			return Long.parseLong(header.getValue());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public static void formatRequestHeader(ByteBuffer byteBuffer, MethodType type, URI uri, HttpMessage message,
			AsyncHttpClient httpClient) {
		try {
			// Request line
			byteBuffer.put(type.name().getBytes(LATIN_1));
			byteBuffer.put((byte) ' ');
			if (uri.getRawPath() != null) {
				byteBuffer.put(uri.getRawPath().getBytes(LATIN_1));
			} else {
				byteBuffer.put((byte) '/');
			}
			if (uri.getRawFragment() != null) {
				byteBuffer.put((byte) '#');
				byteBuffer.put(uri.getRawFragment().getBytes(LATIN_1));
			}
			if (uri.getRawQuery() != null) {
				byteBuffer.put((byte) '?').put(uri.getRawQuery().getBytes(LATIN_1));
			}
			byteBuffer.put(HTTP11).put(CRLF);

			// Headers
			HeaderIterator ite = message.headerIterator();
			while (ite.hasNext()) {
				Header header = ite.nextHeader();
				byteBuffer.put(header.getName().getBytes(LATIN_1)).put((byte) ':')
						.put(header.getValue().getBytes(LATIN_1)).put(CRLF);
			}

			List<Cookie> cookies = httpClient.getCookies(uri);
			for (Cookie cookie : cookies) {
				byteBuffer.put(COOKIE).put(cookie.getName().getBytes(LATIN_1)).put((byte) '=')
						.put(cookie.getValue().getBytes(LATIN_1)).put(CRLF);
			}

			byteBuffer.put(CRLF);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static BasicHttpResponse parseResponseHeader(ByteBuffer byteBuffer) {
		int endPos = getHeaderEndLength(byteBuffer);
		if (endPos == -1) return null;
		byteBuffer.flip();
		BasicLineParser parser = new BasicLineParser(PROTOCOL_VERSION);

		CharArrayBuffer buffer = getLine(byteBuffer, endPos);
		ParserCursor cursor = new ParserCursor(0, buffer.length());
		StatusLine statusLine = parser.parseStatusLine(buffer, cursor);
		BasicHttpResponse response = new BasicHttpResponse(statusLine);

		buffer = getLine(byteBuffer, endPos);
		while (buffer != null && buffer.length() > 0) {
			Header header = parser.parseHeader(buffer);
			response.addHeader(header);
			buffer = getLine(byteBuffer, endPos);
		}

		return response;
	}
	private static CharArrayBuffer getLine(ByteBuffer byteBuffer, int endPos) {
		int offset = byteBuffer.position();
		byte[] rawBuffer = byteBuffer.array();
		for (int i = offset; i < endPos - 1; i++) {
			if (rawBuffer[i] == CR && rawBuffer[i + 1] == LF) {
				int len = i - offset;
				CharArrayBuffer buffer = new CharArrayBuffer(len);
				buffer.append(rawBuffer, offset, len);
				byteBuffer.position(i + 2);
				return buffer;
			}
		}
		return null;
	}
	private static int getHeaderEndLength(ByteBuffer byteBuffer) {
		int pos = byteBuffer.position();
		byte[] rawBuffer = byteBuffer.array();
		for (int i = 0; i < pos - 3; i++) {
			if (rawBuffer[i + 0] == CR
					&& rawBuffer[i + 1] == LF
					&& rawBuffer[i + 2] == CR
					&& rawBuffer[i + 3] == LF) {
				return i + 4;
			}
		}
		return -1;
	}

	public static List<Cookie> getCookies(CookieSpec cookieSpec, CookieStore cookieStore, URI uri) {
		final CookieOrigin cookieOrigin = getCookieOrigin(uri);
		final List<Cookie> cookies = new ArrayList<Cookie>(cookieStore.getCookies());
		final List<Cookie> matchedCookies = new ArrayList<Cookie>();
		final Date now = new Date();
		for (final Cookie cookie : cookies) {
			if (!cookie.isExpired(now)) {
				if (cookieSpec.match(cookie, cookieOrigin)) {
					matchedCookies.add(cookie);
				}
			}
		}
		return matchedCookies;
	}
	public static void setCookie(CookieSpec cookieSpec, CookieStore cookieStore, Header header, URI uri)
			throws MalformedCookieException {
		final CookieOrigin cookieOrigin = getCookieOrigin(uri);
		List<Cookie> cookies = cookieSpec.parse(header, cookieOrigin);
		for (Cookie cookie : cookies) {
			if (cookie.getDomain().length() < 4) {
				((SetCookie) cookie).setDomain(uri.getHost());
			}
			cookieStore.addCookie(cookie);
		}
	}

	public static CookieOrigin getCookieOrigin(URI uri) {
		// TODO: path,scheme,port
		final CookieOrigin cookieOrigin = new CookieOrigin(
				uri.getHost(),
				uri.getPort() >= 0 ? uri.getPort() : 80,
				uri.getPath() != null ? uri.getPath() : "/",
				uri.getScheme().equals("https"));
		return cookieOrigin;
	}
}
