package org.kotemaru.blog.converter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tool {
	public static String getMonth(Blog blog) {
		return zeroSuf(blog.getDate().getMonth()+1, 2);
	}
	
	public static String yyyymmdd(Date date) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
		return fmt.format(date);
	}

	public static String zeroSuf(int val, int len) {
		String str = "000000000000000000000000"+val;
		return str.substring(str.length()-len);
	}
	
}
