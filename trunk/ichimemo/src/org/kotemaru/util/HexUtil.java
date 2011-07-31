
package org.kotemaru.util;

import java.security.Key;
import java.security.MessageDigest;

public class HexUtil {
	private final static String MD = "MD5";
	private final static String UTF8 = "UTF-8";
	private final static char[] HEX_MAP = {
		'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
	};
	private final static int[] RV_HEX_MAP = {
		 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,-1,-1,-1,-1,-1,-1,
		-1,10,11,12,13,14,15,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,10,11,12,13,14,15,-1,-1,-1,-1,-1,-1,-1,-1,-1,
	};

	public static String encodeHex(String str) {
		try {
			return encodeHex(str.getBytes(UTF8));
		} catch (java.io.UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String encodeHex(byte[] bytes) {
		char[] buff = new char[bytes.length*2];
		for (int i=0; i<bytes.length; i++) {
			int h0 = (bytes[i] >> 4) & 0x0f;
			int h1 = bytes[i] & 0x0f;
			buff[i*2]   = HEX_MAP[h0];
			buff[i*2+1] = HEX_MAP[h1];
		}
		return new String(buff);
	}

	public static String decodeHex(String str) {
		try {
			return new String(decodeHexBytes(str), UTF8);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	public static byte[] decodeHexBytes(String str) {
		int len = str.length()/2;
		byte[] bytes = new byte[len];
		for (int i=0; i<len; i++) {
			int c0 = (int)str.charAt(i*2) - '0';
			int h0 = RV_HEX_MAP[c0];
			int c1 = (int)str.charAt(i*2+1) - '0';
			int h1 = RV_HEX_MAP[c1];
			bytes[i] = (byte)(h0<<4 | h1);
		}
		return bytes;
	}

	public static String md5(String data) {	
		try {
			MessageDigest md = MessageDigest.getInstance(MD);
			md.update(data.getBytes(UTF8));
			return encodeHex(md.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static byte[] md5raw(String data) {	
		try {
			MessageDigest md = MessageDigest.getInstance(MD);
			md.update(data.getBytes(UTF8));
			return md.digest();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
