/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.util.jsg;

import java.io.* ;
import java.util.* ;
import java.lang.reflect.* ;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;


public class BnfDriver extends ParserBase {

	public BnfDriver(BnfParser bnfParser, Source source, Tokenizer tokenizer) throws IOException {
		super(source);
		this.defineMap = bnfParser.getDefineMap();
		this.tokenizer = tokenizer;

		List list = bnfParser.getSkippedList();
		this.skippedExprs = (Expr[]) list.toArray(new Expr[list.size()]);
	}
	
	private Output output = new Output();
	private HashMap defineMap;
	private Expr[]  skippedExprs;
	private Tokenizer tokenizer;
	private Token currentToken = null;
	public Stack debugStack = new Stack();
	private int lastSkipPos = -1;

	public Object parse() {
		Expr root = getExpr("Main");
		print("<Main>");
		Object rv = root.eval(this);
		print("</Main>");

		int[] mark = mark();
		skipTokens();
		if (!source.isEOF()) return null;
		rollback(mark, "");
		return rv;
	}
	public String getString() {
		return output.getString();
	}

	public Expr getExpr(String name) {
		return (Expr)defineMap.get(name);
	}

	public void skipTokens() {
		if (lastSkipPos == source.mark()) return;
		boolean isLoop = true;
		while (isLoop) {
			isLoop = false;
			for (int i=0; i<skippedExprs.length; i++) {
				Object rv = skippedExprs[i].eval(this);

				if (rv != null) isLoop = true;
			}
		}
		lastSkipPos = source.mark();
	}

	public int[] mark() {
		return new int[]{source.mark(), output.mark()};
	}

	public Object rollback(int[] mark) {
		return rollback(mark, "");
	}
	public Object rollback(int[] mark, Object msg) {
		if (isDebug()) {
			lastRollback = new RuntimeException("rollback: "
					+source.getDebugString(source.mark(), msg));
			lastRollback.printStackTrace();
		}
		rollbackMessage = msg;

		source.rollback(mark[0]);
		output.rollback(mark[1]);
		return null;
	}

	public void println(String s) {
		output.println(s);
	}
	public void print(String s) {
		output.print(s);
	}
	public void printTag(String name, String data) {
		printStartTag(name);
		if (data.indexOf('<') >= 0 || data.indexOf('&') >= 0) {
			printCData(data);
		} else {
			output.print(data);
		}
		printEndTag(name);
	}
	public void printTag(String name, String data, int pos) {
		output.print('<');
		output.print(name);
		output.print(" p='"+pos+"'");
		output.print('>');
		if (data.indexOf('<') >= 0 || data.indexOf('&') >= 0) {
			printCData(data);
		} else {
			output.print(data);
		}
		printEndTag(name);
	}
	public void printStartTag(String name) {
		output.print('<');
		output.print(name);
		output.print('>');
	}
	public void printEndTag(String name) {
		output.print("</");
		output.print(name);
		output.print('>');
	}
	public void printCData(String data) {
		if (data.indexOf("]]>") >= 0) {
			data = data.replaceAll("&","&amp;").replaceAll("<","&lt;");
			output.print(data);
		} else {
			output.print("<![CDATA[");
			output.print(data);
			output.print("]]>");
		}
	}

	public String getCurrentString() {
		return source.getCurrentString();
	}

	public Token tokenize() {
		if (currentToken == null
			|| currentToken.getPosition() != source.mark()) {
			currentToken = tokenizer.tokenize(this);
		}
		return currentToken;
	}
	public void nextCurrentToken() {
		source.next(currentToken.getRawLength());
		currentToken = null;
	}
}




