/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.util.json;
import java.io.*;
import java.lang.reflect.*;
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
;

String ::= /^"([^"]|\\[\\"bfnrt]|\\u[0-9a-fA-F]{4})*"$/;
Number ::= /^-?[0-9]+([.][0-9]+)?([eE][-+]?[0-9]+)?$/;
*/

public class JSONSerializer {
	private OutputStream out;

	public JSONSerializer() {
	}
	public JSONSerializer(OutputStream out) {
		this.out = out;
	}

	public void serialize(Object obj, OutputStream out) throws IOException  {
		if (out instanceof BufferedOutputStream) {
			this.out = out;
		} else {
			this.out = new BufferedOutputStream(out);
		}
		sObject(obj);
		this.out.flush();
	}
	public String getString(Map obj, String charset) throws IOException  {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		serialize(obj, bout);
		return bout.toString(charset);
	}

	public void write(Object obj) throws IOException  {
		sObject(obj);
	}

	public void sObject(Object obj)  throws IOException {
		if (obj instanceof Map) {
			sMap((Map) obj);
		} else if (obj instanceof List) {
			sArray((List)obj);
		} else if (obj instanceof String) {
			sString((String)obj);
		} else if (obj instanceof Number) {
			sNumber((Number)obj);
		} else if (obj instanceof Boolean) {
			sBoolean((Boolean)obj);
		} else if (obj == null) {
			append("null");
		} else {
			//LOG.error("JSONSerializer: Unknown object "+obj.getClass());
			//throw new RuntimeException("Unknown object "+obj.getClass());
			sBean(obj);
		}
	}

	public void sArray(List obj)  throws IOException {
		append('[');
		for (int i=0; i<obj.size(); i++) {
			Object val = obj.get(i);
			if (i > 0) append(',');
			sObject(val);
		}
		append(']');
	}

	public void sMap(Map obj) throws IOException  {
		append('{');
		Iterator<Map.Entry> ite = obj.entrySet().iterator();
		int i = 0;
		while(ite.hasNext()){
			Map.Entry ent = ite.next();
			Object key = ent.getKey();
			Object val = ent.getValue();

			if (i > 0) append(',');
			sString(key.toString());
			append(':');
			sObject(val);
			i++;
		}
		append('}');
	}

	public void sString(String str) throws IOException  {
		append('"');
		for (int i=0; i<str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '"') {
				append('\\','"');
			} else if (' ' <= ch && ch <= 127) {
				append(ch);
			} else if (ch == '\n') {
				append('\\','n');
			} else if (ch == '\r') {
				append('\\','r');
			} else {
				append('\\','u');
				append(toHexChar((ch>>12)&0x0f));
				append(toHexChar((ch>>8) &0x0f));
				append(toHexChar((ch>>4) &0x0f));
				append(toHexChar( ch     &0x0f));
			}
		}
		append('"');
	}
	public char toHexChar(int n) throws IOException  {
		if (n <= 9) return (char)('0'+n);
		return (char)('a'+(n-10));
	}

	public void sNumber(Number num)  throws IOException {
		append(num.toString());
	}

	public void sBoolean(Boolean bool) throws IOException  {
		append(bool.toString());
	}



	public void append(String s)  throws IOException {
		out.write(s.getBytes("UTF-8"));
	}

	public void append(char c1) throws IOException  {
		out.write(c1);
	}
	public void append(char c1, char c2) throws IOException {
		out.write(c1);
		out.write(c2);
	}
	
	
	public void sBean(Object obj)  throws IOException {
		int count = 0;
		append('{');
		Method[] methods = obj.getClass().getMethods();
		for (int i=0; i<methods.length; i++) {
			String mname = methods[i].getName();
			// 引数が無く、Objectのメソッドで無く、get[A-Z]で始まるもの。
			if (methods[i].getParameterTypes().length == 0
				&& methods[i].getDeclaringClass() != Object.class
				&& mname.length()>3
				&& mname.startsWith("get")
				&& Character.isUpperCase(mname.charAt(3))
			) {
				if (count++>0) append(',');
				
				String key = mname.substring(3).toLowerCase();
				sString(key.toString());
				append(':');
				try {
					sObject(methods[i].invoke(obj));
				} catch (IOException e) {
					throw e;
				} catch (Exception e) {
					throw new IOException(e);
				}
			}
		}
		append('}');
	}

}

