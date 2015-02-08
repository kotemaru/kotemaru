package org.kotemaru.blog.builder;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Tool {
	public static String getMonth(Blog blog) {
		return zeroSuf(blog.getDate().getMonth() + 1, 2);
	}

	public static List<String> parseCamma(String str) {
		String[] tags = str.split(",");
		List<String> list = new ArrayList<String>(tags.length);
		for (int i = 0; i < tags.length; i++) {
			list.add(tags[i].trim());
		}
		return list;
	}

	public static String yyyymmdd(Date date) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
		return fmt.format(date);
	}

	public static String zeroSuf(int val, int len) {
		String str = "000000000000000000000000" + val;
		return str.substring(str.length() - len);
	}

	public static String encode(String str) {
		try {
			return URLEncoder.encode(str, "MS932").replaceFirst("^%", "_")
					.replaceAll("%", "");
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}

	public static String escape(String str) {
		return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

	public static String rfc822(Date date) {
		SimpleDateFormat rfc822 = new SimpleDateFormat(
				"EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
		return rfc822.format(date);
	}

	public static void log(Object... msg) {
		for (Object m : msg) {
			System.out.print("" + m);
		}
		System.out.println();
	}

	public static String getThumbnail(BlogContext ctx, Blog blog) {
		log("getThumbnail=");
		File dir = blog.getFile().getParentFile();
		log("DIR=", dir.toString());
		String[] imgFiles = dir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jpg") || name.endsWith(".png");
			}
		});
		if (imgFiles.length == 0) return "";
		log("File=", imgFiles[0]);
		File root = ctx.getContentsRoot();
		int len = root.getAbsolutePath().replaceFirst("/$","").length();
		return dir.toString().substring(len)+"/"+imgFiles[0];
	}
}
