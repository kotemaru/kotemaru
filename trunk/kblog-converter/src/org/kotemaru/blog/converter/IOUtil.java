package org.kotemaru.blog.converter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import java.util.ArrayList;

import java.util.List;
import java.util.Properties;

public class IOUtil {
	private static final String UTF8 = "utf-8";
	
	public static void debug(String msg) {
		System.out.println(msg);
	}
	
	public static void putText(File file, String html)  throws IOException {
		File dir = file.getParentFile();
		dir.mkdirs();
		OutputStream out = new FileOutputStream(file);
		try {
			Writer writer = new OutputStreamWriter(out,"utf-8");
			writer.write(html);
			writer.close();
		} finally {
			out.close();
		}
	}

	public static String getText(File file)  throws IOException {
		return getText(file, new StringBuffer()).toString();
	}
	public static StringBuffer getText(File file, StringBuffer sbuf)  throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			BufferedInputStream buffIn = new BufferedInputStream(in);
			Reader reader = new InputStreamReader(buffIn, "UTF-8");
			char[] buff = new char[4096];
			int n;
			while ((n=reader.read(buff)) > 0) {
				sbuf.append(buff,0,n);
			}
			return sbuf;
		} finally {
			in.close();
		}
		
	}

	public static Properties getProps(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			BufferedInputStream buffIn = new BufferedInputStream(in);
			Reader reader = new InputStreamReader(buffIn, "UTF-8");
			Properties pr = new Properties();
			pr.load(reader);
			return pr;
		} finally {
			in.close();
		}
	}

	
	public static String readLine(InputStream in) throws IOException {
		byte[] buff = new byte[4096];
		int off = 0;
		int ch;
		while ((ch=in.read()) > 0 && ch != '\n') {
			buff[off++] = (byte)ch;
		}
		String str = new String(buff,0,off,UTF8);
		return str;
	}
	

	public static byte[] getBytes(InputStream in)  throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		transfer(in, out);
		return out.toByteArray();
	}
	
	public static int transfer(InputStream in, OutputStream out)  throws IOException {
		int len = 0;
		int n;
		byte[] buff = new byte[4096];
		while ((n=in.read(buff)) > 0) {
			out.write(buff);
			len += n;
		}
		return len;
	}

	
	public static List<File> find(File root, String name) {
		List<File> list = new ArrayList<File>();
		find(root, name, list);
		return list;
	}
	public static void find(File file, String name, List<File> list) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for(int i=0; i<files.length; i++) {
				find(files[i], name, list);
			}
		} else {
			if (name.equals(file.getName())) {
				list.add(file);
			}
		}
	}

	public static String getString(Reader r) throws IOException {
		StringBuffer sbuf = new StringBuffer();
		int n;
		char[] buff = new char[4096];
		while ((n=r.read(buff)) > 0) {
			sbuf.append(buff,0,n);
		}
		return sbuf.toString();
	}
	
}
