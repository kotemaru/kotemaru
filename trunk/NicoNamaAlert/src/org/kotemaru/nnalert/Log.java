package org.kotemaru.nnalert;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	public static final int DEBUG = 0;
	public static final int INFO = 1;
	public static final int WARN = 2;
	public static final int ERROR = 3;

	public static int logLevel = INFO;
	private static PrintStream out = System.out;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss ");
	
	public static void d(String msg) {
		if (logLevel > DEBUG) return;
		out.print(dateFormat.format(new Date()));
		out.print("DEBUG: ");
		out.println(msg);
	}

	public static void i(String msg) {
		if (logLevel > INFO) return;
		out.print(dateFormat.format(new Date()));
		out.print("INFO : ");
		out.println(msg);
	}

	public static void w(String msg) {
		if (logLevel > WARN) return;
		out.print(dateFormat.format(new Date()));
		out.print("WARN : ");
		out.println(msg);
	}

	public static void e(String msg) {
		out.print(dateFormat.format(new Date()));
		out.print("ERROR: ");
		out.println(msg);
	}

	public static void e(String msg, Throwable t) {
		out.print(dateFormat.format(new Date()));
		out.print("ERROR: ");
		out.print(msg);
		t.printStackTrace(out);
	}
}
