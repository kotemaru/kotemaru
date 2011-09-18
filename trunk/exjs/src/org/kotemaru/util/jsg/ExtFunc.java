/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.util.jsg;

import java.util.*;


public class ExtFunc {

	public String jstr(String text) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append('"');
		for (int i=0; i<text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == '"') {
				sbuf.append("\\\"");
			} else if (ch == '\n') {
				sbuf.append("\\n");
			} else if (ch == '\r') {
				sbuf.append("\\r");
			} else if (ch == '\t') {
				sbuf.append("\\t");
			} else {
				sbuf.append(ch);
			}
		}
		sbuf.append('"');
		return sbuf.toString();
	}
}

