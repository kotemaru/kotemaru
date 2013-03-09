/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.util.json;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

/**
Object ::= "{" [Members] "}";
Members ::= Pair ["," Pair]...;
Pair ::= String ":" Value;
Array ::= "[" [Elements] "]";
Elements :: = Value ["," Value]...;
Value ::= 
	  String
	| Number
	| Object
	| Array
	| "true"
	| "false"
	| "null"
	| "undefined"
;

String ::= /^"([^"]|\\[\\"bfnrt]|\\u[0-9a-fA-F]{4})*"$/;
Number ::= /^-?[0-9]+([.][0-9]+)?([eE][-+]?[0-9]+)?$/;
*/

public class JSONParser {
	private static final int TT_NUMBER = StreamTokenizer.TT_NUMBER;
	private static final int TT_STRING = (int) '"';
	private static final int TT_WORD   = StreamTokenizer.TT_WORD;
	private static final int TT_TRUE   = 1000003;
	private static final int TT_FALSE  = 1000004;
	private static final int TT_NULL   = 1000005;

	
	private String stringValue;
	//private double numberValue;
	private Reader reader;
	private int lineNumber = 1;
	
	public JSONParser() {
	}
	public JSONParser(Reader reader) {
		this.reader = reader;
	}

	public Map parse(String data) {
		return parse(new StringReader(data));
	}
	public List parseArray(String data) {
		this.reader = new StringReader(data);
		return pArray(nextToken(), null);
	}

	public Map parse(Reader reader) {
		this.reader = reader;
		return pObject(nextToken(), null);
	}
	

	public Map pObject(int tkn, Map scope) {
		boolean hasParentheses = ('(' == tkn);
		if (hasParentheses) tkn = nextToken();
		if ('{' != tkn) error("{", tkn);
		Map obj = new HashMap();
		tkn = nextToken();
		if ('}' != tkn) {
			pPair(tkn, obj);
			tkn = nextToken();
		}
		while (',' == tkn) {
			pPair(nextToken(), obj);
			tkn = nextToken();
		}
		if ('}' != tkn) error("}", tkn);
		if (hasParentheses && nextToken() != ')') error("Not found ')'");
		return obj;
	}

	public void pPair(int tkn, Map scope) {
		if (TT_STRING != tkn && TT_WORD != tkn) error("Not property name", tkn);
		String name = stringValue;
		if (':' != nextToken()) error(":", tkn);
		Object val = pValue(scope);
		scope.put(name, val);
	}

	public Object pValue(Map scope)  {
		return pValue(nextToken(), scope);
	}
	public Object pValue(int tkn, Map scope)  {
		if (tkn == TT_STRING) return stringValue;
		if (tkn == TT_NUMBER) return makeNumber(stringValue);
		if (tkn == TT_NULL)   return null;
		if (tkn == TT_TRUE)   return Boolean.TRUE;
		if (tkn == TT_FALSE)  return Boolean.FALSE;
		if (tkn == '{') return pObject(tkn, scope);
		if (tkn == '[') return pArray(tkn, scope);
		error("Value", tkn);
		return null;
	}
	public Object makeNumber(String str) {
		if (str.indexOf('.') >= 0 ||
			str.indexOf('e') >= 0 || str.indexOf('E') >= 0 ) {
			return Double.valueOf(str);
		} else {
			return Long.valueOf(str);
		}
	}

	public List pArray(int tkn, Map scope)  {
		if ('[' != tkn) error("[", tkn);
		List array = new ArrayList();
		tkn = nextToken();
		if (']' == tkn) return array;
		int idx = 0;
		array.add(pValue(tkn, scope));
		tkn = nextToken();
		while (',' == tkn) {
			array.add(pValue(scope));
			tkn = nextToken();
		}
		if (']' != tkn) error("]", tkn);
		return array;
	}

	public StringBuffer tokenBuff = new StringBuffer();
	public StringBuffer hexBuff = new StringBuffer(4);
	public final int nextToken() {
		try {
			stringValue = null;

			int ch = read();
			while (isSpace(ch)) ch = read();
			if (ch == -1) return -1;

			if (ch == '{' || ch == '}' 
				|| ch == '[' || ch == ']'
				|| ch == ':' || ch == ','
			) {
				return ch;
			}

			tokenBuff.setLength(0);
			if (isLatin(ch)) {
				while (isLatin(ch) || isDigit(ch)) {
					tokenBuff.append((char)ch);
					ch = read();
				}
				unread(ch);
				stringValue = tokenBuff.toString();

				if ("null".equals(stringValue)) return TT_NULL;
				if ("undefined".equals(stringValue)) return TT_NULL;
				if ("true".equals(stringValue)) return TT_TRUE;
				if ("false".equals(stringValue)) return TT_FALSE;
				//error("Bad keyword "+stringValue);
				return TT_WORD;
			}

			if (ch == '-' || isDigit(ch)) {
				while (isDigit(ch) || ch == '.' || ch == '+' 
					|| ch == '-' || ch == 'e' || ch == 'E') {
					tokenBuff.append((char)ch);
					ch = read();
				}
				stringValue = tokenBuff.toString();
				//numberValue = Double.parseDouble(stringValue);
				unread(ch);
				return TT_NUMBER;
			}

			if (ch == '"' || ch == '\'') {
				int keyCh = ch;
				ch = read();
				while (ch != keyCh) {
					if (ch == '\\') {
						ch = read();
						if (ch == '\\') ch = '\\';
						else if (ch == 'n') ch = '\n';
						else if (ch == 'r') ch = '\r';
						else if (ch == 't') ch = '\t';
						else if (ch == 'f') ch = '\f';
						else if (ch == 'b') ch = '\b';
						else if (ch == 'u') {
							hexBuff.setLength(0);
							hexBuff.append((char)read());
							hexBuff.append((char)read());
							hexBuff.append((char)read());
							hexBuff.append((char)read());
							ch = (char)Integer.parseInt(hexBuff.toString(),16);
						}
					}
					tokenBuff.append((char)ch);
					if (tokenBuff.length() > 4096000) {
						error("String too long. limit 4M.");
					}
					ch = read();
				}
				stringValue = tokenBuff.toString();
				return TT_STRING;
			}

			error("Unknown token", ch);
			return -1;
		} catch (IOException e) {
			error(e.toString());
			return -1;
		}
	}


	public int unreadChar = -1;
	public int read() throws IOException {
		if (unreadChar >= 0) {
			int ch = unreadChar;
			unreadChar = -1;
			return ch;
		}
		int ch0 = reader.read();
		if (ch0 == '\n') lineNumber++;
		return ch0;
	}
	public void unread(int ch) {
		if (unreadChar >= 0) {
			error("Duplicate unread");
		}
		unreadChar = (int)ch;
	}

	public boolean isSpace(int ch) {
		return ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t';
	}
	public boolean isLatin(int ch) {
		return ('a' <= ch && ch <= 'z')
			|| ('A' <= ch && ch <= 'Z')
			|| ch == '_' || ch == '$';
	}
	public boolean isDigit(int ch) {
		return ('0' <= ch && ch <= '9');
	}

	public void error() {
		error("");
	}
	public void error(String msg) {
		throw new RuntimeException(lineNumber+":"+msg);
	}
	public void error(String key, int val) {
		throw new RuntimeException(lineNumber+":"+"Not "+key+" "+stringValue+"("+Integer.toString(val,16)+")");
	}
}






