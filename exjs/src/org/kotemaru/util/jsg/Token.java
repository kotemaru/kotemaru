/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.util.jsg;

public class Token {
	private String type;
	private String value;
	private boolean isSkipped;
	private int position = -1;

	public Token(String typeName, BnfDriver driver, String val, boolean skipped) {
		type = typeName;
		value = val;
		isSkipped = skipped;
		position = driver.mark()[0];
	}
	public Token(String typeName, BnfDriver driver, String val) {
		this(typeName, driver, val, false);
	}
	public boolean isSkipped() {
		return isSkipped;
	}
	public boolean isType(String typeName) {
		return typeName.equals(type);
	}
	public String getValue() {
		return value;
	}
	public int getRawLength() {
		return value.length();
	}
	public String toString() {
		return value;
	}
	public boolean equals(String patt) {
		return value.equals(patt);
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int pos) {
		position = pos;
	}
}
