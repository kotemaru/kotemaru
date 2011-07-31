/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.util;

import java.io.* ;
import java.net.* ;
import java.util.* ;

public class IOUtil {
	public static String getFile(String name) throws IOException {
		return getFile(new File(name));
	}
	public static String getFile(File file) throws IOException {
		return getFile(file, "UTF-8");
	}
	public static String getFile(File file, String charset) throws IOException {
		FileInputStream in = new FileInputStream(file);
		try {
			return streamToString(in, charset);
		} finally {
			if (in != null) in.close();
		}
	}
	public static  byte[] getFileBytes(File file) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		byte[] buff = new byte[(int)raf.length()];
		IOUtil.readFull(raf, buff, 0, buff.length, true);
		return buff;
	}

/*--- for GAE
	public static void putFile(String name, String data) throws IOException {
		putFile(new File(name), data);
	}
	public static void putFile(File file, String data) throws IOException {
		putFile(file, data, "UTF-8");
	}
	public static void putFile(File file, String data, String charset) throws IOException {
		InputStream in = new ByteArrayInputStream(data.getBytes(charset));
		FileOutputStream out = new FileOutputStream(file);
		transrate(in, out, true, true);
	}
---*/
	public static String getResource(Class clazz, String name) throws IOException {
		InputStream in = clazz.getResourceAsStream(name);
		if (in == null) {
			throw new IOException("Not found resource "+name+" in "+clazz.getName());
		}
		try {
			return readerToString(new InputStreamReader(in, "UTF-8"));
		} finally {
			in.close();
		}
	}
	public static String getResource(Class clazz, String name, String charset) throws IOException {
		InputStream in = clazz.getResourceAsStream(name);
		try {
			return readerToString(new InputStreamReader(in, charset));
		} finally {
			in.close();
		}
	}
	public static byte[] streamToBytes(InputStream in) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream(8192);
		transrate(in, bout);
		return bout.toByteArray();
	}
	public static String streamToString(InputStream in, String charSet) throws IOException {
		return readerToString(new InputStreamReader(in, charSet));
	}
	public static String readerToString(Reader reader) throws IOException {
		StringBuffer sbuf = new StringBuffer();
		int n = 0;
		char[] buff = new char[4096];
		while ((n=reader.read(buff)) > 0) {
			sbuf.append(buff, 0, n);
		}
		return sbuf.toString();
	}
	public static void transrate(InputStream in, OutputStream out)
			throws IOException {
		transrate(in, out, false, false);
	}
	public static void transrate(InputStream in, OutputStream out,
			boolean inClose, boolean outClose) throws IOException {
		try {
			byte buff[] = new byte[4096];
			int n = 0;
			while ((n = in.read(buff)) >= 0) {
				out.write(buff, 0, n);
			}
		} finally {
			out.flush();
			if (inClose) in.close();
			if (outClose) out.close();
		}
	}
	public static void readFull(InputStream in, 
			byte[] buff, int off, int len,
			boolean inClose) throws IOException {
		try {
			while (len > 0) {
				int n = in.read(buff,off,len);
				if (n == -1) throw new IOException("EOF");
				off = off + n;
				len = len - n;
			}
		} finally {
			if (inClose) in.close();
		}
	}
	public static void transrate(RandomAccessFile in, OutputStream out,
			boolean inClose, boolean outClose) throws IOException {
		try {
			byte buff[] = new byte[4096];
			int n = 0;
			while ((n = in.read(buff)) >= 0) {
				out.write(buff, 0, n);
			}
		} finally {
			if (inClose) in.close();
			if (outClose) out.close();
		}
	}
	public static void readFull(RandomAccessFile in, 
			byte[] buff, int off, int len,
			boolean inClose) throws IOException {
		try {
			while (len > 0) {
				int n = in.read(buff,off,len);
				if (n == -1) throw new IOException("EOF");
				off = off + n;
				len = len - n;
			}
		} finally {
			if (inClose) in.close();
		}
	}
	public static String getExt(String fname) {
		int idx = fname.lastIndexOf('.');
		return (idx>0) ? fname.substring(idx) : "";
	}

/*--- for GAE
	public static String[][] getCSVFile(File file) throws IOException {
		String data = IOUtil.getFile(file);
		String[] lines = data.split("\\n");
		ArrayList list = new ArrayList(lines.length);
		for (int i=0; i<lines.length; i++) {
			String line = lines[i].trim();
			if (line.length() > 0 && line.charAt(0) != '#') {
				list.add(line.split(","));
			}
		}

		String[][] db = new String[list.size()][];
		list.toArray(db);
		return db;
	}
	public static void putCSVFile(File file, String[][] db) throws IOException {
		StringBuffer sbuf = new StringBuffer(1024);
		for (int i=0; i<db.length; i++) {
			if (db[i] != null) {
				for (int j=0; j<db[i].length; j++) {
					if (j>0) sbuf.append(',');
					sbuf.append(db[i][j]);
				}
				sbuf.append('\n');
			}
		}
		IOUtil.putFile(file, sbuf.toString());
	}
---*/
	
	/**
???????
</XMP>
a//b     -> a/b
a/b/../c -> a/c
a/b/..   -> a/
/../a    -> /a
a/./b    -> a/b
../a     -> ../a
./a      -> a
a/       -> a
a/b/.    -> a/b
</XMP>
	 */

	public static String formalPageName(String pageName) {
		if (pageName == null) return null;
		if ("/".equals(pageName)) return pageName;
		pageName = pageName.replaceFirst("/+$","");
		if (pageName.endsWith("/.")) pageName = pageName.substring(0,pageName.length()-2);

		if (pageName.indexOf("//")==-1 && pageName.indexOf("./")==-1
			&& !pageName.endsWith("/.") && !pageName.endsWith("/..") ){
				return pageName;
		}

		ArrayList<String> names = new ArrayList<String>(16);
		fpn(pageName, 1, names);

		StringBuffer sbuf = new StringBuffer(pageName.length());
		for (int i=names.size()-1; i>=0; i--) {
			String name = names.get(i);
			if (name != null) {
				sbuf.append('/');
				sbuf.append(name);
			}
		}
		return sbuf.toString();
	}

	// 再帰関数
	private static int fpn(String pageName, int pos, ArrayList<String> names) {
		String name = null;
		int parentCount = 0;
		int end = pageName.indexOf('/',pos);
		if (end == -1) {
			name = pageName.substring(pos);
		} else {
			name = pageName.substring(pos, end);
			parentCount = fpn(pageName, end+1, names);
		}

		if (name.length() == 0 || ".".equals(name)) return parentCount;
		if ("..".equals(name)) return parentCount+1;
		if (parentCount > 0) return parentCount-1;

		names.add(name);
		return 0;
	}

	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.writeObject(obj);
		out.flush();
		return bout.toByteArray();
	}
	public static Object deserialize(byte[] data)
			throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		ObjectInputStream in = new ObjectInputStream(bin);
		return in.readObject();
	}



	public static void main(String[] args) {
		System.out.println(formalPageName(args[0]));
	}


}
