/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.util.jsg;

import java.io.* ;
import java.util.* ;
import java.util.regex.* ;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;


public class Source {
	protected String matter;
	protected int current;
	protected int max;

	protected String currentMatter = null;
	protected int currentMatterPos = -1;


	public Source(String str) {
		matter = str;
		current = 0;
	}


	public int mark() {
		return current;
	}
	public int max() {
		return max;
	}
	public void rollback(int mark) {
		current = mark;
	}
	public void next(int count) {
		current = current + count;
		if (current >= matter.length()) current = matter.length();
		max = (current>max) ? current : max;
	}
	public int next() {
		max = (current>max) ? current : max;
		if (current >= matter.length()) {
			current++;
			return (int)-1;
		}
		return matter.charAt(current++);
	}
	public int prev() {
		if (current >= matter.length()) {
			current--;
			return (int)-1;
		}
		return matter.charAt(--current);
	}
	public String substring(int begin, int end) {
		if (end > matter.length()) end = matter.length();
		if (begin >= end) return "";
		return matter.substring(begin, end);
	}
	public boolean isEOF() {
		return current >= matter.length();
	}

	public String match(String patt) {
		Pattern p = Pattern.compile(patt);
		String curSub = matter.substring(current);
		Matcher m = p.matcher(matter);
		boolean b = m.find();
		if (!b || m.start() != 0) return null;
		return curSub.substring(m.start(), m.end());
	}
	public  int indexOf(String str) {
		int pos = matter.indexOf(str, current);
		if (pos == -1) return -1;
		return pos - current;
	}
	public String getCurrentString() {
		if (currentMatter == null || current != currentMatterPos) {
			currentMatter = matter.substring(current);
			currentMatterPos = current;
		}
		return currentMatter;
	}

	public String getDebugString() {
		return getDebugString(current, "");
	}
	public String getDebugString(int pos, Object msg) {
		int begin = matter.lastIndexOf('\n', pos)+1;
		int end   = matter.indexOf('\n', pos+1);
		if (begin == -1) begin = 0;
		if (end   == -1) end = matter.length();
		int lno = 1;
		for (int i=0; i<pos && i<matter.length(); i++) {
			if (matter.charAt(i) == '\n') lno++; 
		}

		String str = "line "+lno+": "+msg+"\n";
		str = str + matter.substring(begin, end)+"\n";

		String spc = "";
		if (begin<pos && pos<matter.length()) {
			spc = matter.substring(begin, pos);
		}
		spc = spc.replaceAll("[^\\s]"," ");
		str = str + spc + '^';
		return str;
	}
}
