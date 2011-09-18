/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.util.jsg;

import java.io.* ;
import java.util.* ;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

public class Output {
	private char[] buffer = new char[1024];
	private int length = 0;

	public Output() {
	}

	public String getString(){
		return new String(buffer, 0, length);
	}
	public char[] getBuffer(){
		return buffer;
	}
	public int getLength(){
		return length;
	}

	public int mark(){
		return length;
	}
	public void rollback(int mark) {
		length = mark;
	}
	public void print(char ch) {
		if (length >= buffer.length) expand();
		buffer[length++] = ch;
	}
	public void print(String s) {
		if (length + s.length() > buffer.length) expand();
		s.getChars(0, s.length(), buffer, length);
		length = length + s.length();
	}
	private void expand() {
		char[] tmp = new char[buffer.length*2];
		System.arraycopy(buffer, 0, tmp, 0, length);
		buffer = tmp;
	}

	public void println(String s) {
		print(s);
		print("\n");
	}

}



