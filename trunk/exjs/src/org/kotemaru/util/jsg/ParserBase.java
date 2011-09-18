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


public abstract class ParserBase {
	protected RuntimeException lastRollback = null;
	protected Object rollbackMessage = null;
	protected Source source = null;

	protected ParserBase(Source src) {
		source = src;
	}

	protected String rollbackStr(int mark, Object msg) {
		if (isDebug()) {
			lastRollback = new RuntimeException("rollback: "+msg);
			lastRollback.printStackTrace();
		}
		rollbackMessage = msg;
		source.rollback(mark);
		return null;
	}

	boolean isDebug = false;
	public void setDebug(boolean b) {
		isDebug = b;
	}
	protected boolean isDebug() {
		return isDebug;
	}

	protected String pToken(String patt) {
		skipSpace();
		int smark = source.mark();

		int begin = source.mark();
		int len = patt.length();
		int n = 0;
		int ch = source.next();
		while (n<len && ch == patt.charAt(n)) {
			if (ch == '\\') ch = source.next();
			ch = source.next();
			n++;
		}
		if (n != len) {
			String msg = "Unmatch token '"+patt+"' != '"
				+source.substring(begin, source.mark())+"'\n"+source.getDebugString(); 
			return rollbackStr(smark, msg);
		}
		source.prev();
		return patt;
	}

	public void next(int len) {
		source.next(len);
	}

	protected void skipSpace() {
		pSpace();
		if (source.isEOF()) return;
	}
	protected boolean pSpace() {
		int ch = source.next();
		if (!(ch==' ' || ch=='\t' || ch=='\n' || ch=='\r')) {
			source.prev();
			return false;
		}
		ch = source.next();
		while (ch==' ' || ch=='\t' || ch=='\n' || ch=='\r') {
			ch = source.next();
		}
		if (!source.isEOF()) source.prev();
		return true;
	}

	protected boolean isAlpha(int ch) {
		return ('a'<=ch && ch<='z') || ('A'<=ch && ch<='Z');
	}
	protected boolean isNumber(int ch) {
		return ('0'<=ch && ch<='9');
	}

	protected String pToken(String p1,String p2) {
		String s = null;
		if ((s=pToken(p1)) != null) return s;
		if ((s=pToken(p2)) != null) return s;
		return s;
	}
	protected String pToken(String p1,String p2,String p3) {
		String s = null;
		if ((s=pToken(p1)) != null) return s;
		if ((s=pToken(p2)) != null) return s;
		if ((s=pToken(p3)) != null) return s;
		return s;
	}
	protected String pToken(String p1,String p2,String p3,String p4) {
		String s = null;
		if ((s=pToken(p1)) != null) return s;
		if ((s=pToken(p2)) != null) return s;
		if ((s=pToken(p3)) != null) return s;
		if ((s=pToken(p4)) != null) return s;
		return s;
	}


	public String getDebugString() {
		return source.getDebugString(source.max(), rollbackMessage);
	}
	public RuntimeException getLastRollback() {
		return this.lastRollback;
	}

}


