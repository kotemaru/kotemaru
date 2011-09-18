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


/**
Main ::= {Define}...;
Define ::= Identifier "::=" {Expr| RegexpToken | Extension}";";
Expr ::= Or;
Or ::= And ["|" And]...;
And ::= Repert [Repert]...;
Repert ::= Not ["..."];
Not ::= ["!"] Atom;
Atom ::= Literal | Identifier | Block | OmitBlock;
Block ::= "{" Expr "}";
OmitBlock ::= "[" Expr "]";

RegexpToken ::= "/正規表現/" ["skipped"];
Extension ::= "$" Identifier {"." Identifier}... "(" [Literal] ")" ["skipped"] ;

Comment ::= javaスタイルコメント
Literal ::= javaスタイル文字列
Identifier ::= javaスタイル識別子
*/

public class BnfParser extends ParserBase {

	private static final Expr OK = new Expr.LiteralToken("OK");
	private static final boolean isDebug = true;

	private HashMap defineMap = new HashMap();
	private List skippedList = new ArrayList();

	public BnfParser(Source src) throws IOException {
		super(src);
	}
	public HashMap getDefineMap() {
		return defineMap;
	}
	public List getSkippedList() {
		return skippedList;
	}

	// Main ::= {Define | Comment}...;
	public Expr parse() {

		while (!source.isEOF()) {
			if (pDefine() == null) {
				skipSpace();
				if (source.next() == -1) break;
				return null;
			}
		}
		return OK;
	}


	// Define ::= Identifier "::=" {Expr| RegexpToken | Extension}";";
	public Expr pDefine() {
		int smark = source.mark();

		String name = pIdentifier();
		if(pToken("::=") == null) return rollback(smark);
		Expr expr = pExpr();
		if(expr == null) expr = pRegexpToken();
		if(expr == null) expr = pExtension();
		if(expr == null) return rollback(smark);
		if(pToken(";") == null) return rollback(smark);

		if (defineMap.get(name) != null) {
			throw new RuntimeException("Duplicate define "+name);
		}
		expr.setName(name);
		defineMap.put(name, expr);
		if (expr.isSkipped()) skippedList.add(expr);
		return expr;
	}

	// Expr ::= Or;
	private Expr pExpr() {
		return pOr();
	}

	// Or ::= And ["|" And]...;
	private Expr pOr() {
		int smark = source.mark();

		Expr.OrExpr thisExpr = new Expr.OrExpr();
		Expr expr = pAnd();
		if(expr == null) return rollback(smark);
		thisExpr.add(expr);

		while (pToken("|") != null) {
			expr = pAnd();
			if(expr == null) return rollback(smark);
			thisExpr.add(expr);
		}

		if (thisExpr.size() == 1) return thisExpr.get(0);
		return thisExpr;
	}

	// And ::= Repert [Repert]...;
	public Expr pAnd() {
		int smark = source.mark();

		Expr.AndExpr thisExpr = new Expr.AndExpr();
		Expr expr = pRepeat();
		if(expr == null) return rollback(smark);
		thisExpr.add(expr);

		while ((expr = pRepeat()) != null) {
			thisExpr.add(expr);
		}

		if (thisExpr.size() == 1) return thisExpr.get(0);
		return thisExpr;
	}


	// Repert ::= Not ["..."];
	public Expr pRepeat() {
		int smark = source.mark();

		Expr atom = pNot();
		if (atom == null) return rollback(smark);
		if (pToken("...") == null) return atom;

		if (atom instanceof Expr.Block) {
			((Expr.Block)atom).setMaxOccers(Integer.MAX_VALUE);
			return atom;
		} else {
			return new Expr.Block(1, Integer.MAX_VALUE, atom);
		}
	}

	// Not ::= ["!"] Atom;
	private Expr pNot() {
		if (pToken("!") == null) return pAtom();
		Expr atom = pAtom();
		if (atom == null) return null;
		return new Expr.NotExpr(atom);
	}

	// Atom ::= Literal | Identifier | OmitBlock | Block;
	private Expr pAtom() {
		Expr expr;
		if ((expr = pLiteralToken())    != null) return expr;
		if ((expr = pIdentifierRefer()) != null) return expr;
		if ((expr = pOmitBlock())       != null) return expr;
		if ((expr = pBlock())           != null) return expr;
		return null;
	}

	// OmitBlock ::= "[" Expr "]";
	public Expr pOmitBlock() {
		int smark = source.mark();

		if (pToken("[") == null) return rollback(smark);
		Expr expr = pExpr();
		if (pToken("]") == null) return rollback(smark);
		return new Expr.Block(0, 1, expr);
	}

	// Block ::= "{" Expr "}";
	public Expr pBlock() {
		int smark = source.mark();

		if (pToken("{") == null) return rollback(smark);
		Expr expr = pExpr();
		if (pToken("}") == null) return rollback(smark);
		return new Expr.Block(1, 1, expr);
	}

	private Expr pLiteralToken() {
		String s = pLiteral();
		if (s == null) return null;
		return new Expr.LiteralToken(s);
	}
	private Expr pIdentifierRefer() {
		String s = pIdentifier();
		if (s == null) return null;
		return new Expr.IdentifierRefer(s);
	}

	// RegexpToken ::= "/正規表現/" ["skipped"];
	public Expr pRegexpToken() {
		int mark = source.mark();
		String regexp = pRegexp();
		if (regexp == null) return rollback(mark);
		boolean isSkipped = (pToken("skipped") != null);
		return new Expr.RegexpToken(regexp, isSkipped);
	}

	// Extension ::= "$" Identifier {"." Identifier}... "(" [Literal] ")";
	public Expr pExtension() {
		int mark = source.mark();

		if (pToken("$") == null) return rollback(mark);
		String path = pIdentifier();
		if (path == null) return rollback(mark, "$ID");
		while (pToken(".") != null) {
			String id = pIdentifier();
			if (id == null) return rollback(mark, "$ID");
			path = path + "." + id;
		}

		if (pToken("(") == null) return rollback(mark, "(");
		String arg = pLiteral();
		if (pToken(")") == null) return rollback(mark, ")");

		boolean isSkipped = (pToken("skipped") != null);
		if ("Tokenizer".equals(path)) {
			return new Expr.TokenizerToken(isSkipped);
		}
		return new Expr.Extension(path, arg, isSkipped);
	}


//---------------------------------------------------------------
// Tokens
	// Literal ::= // "文字列"
	private String pLiteral() {
		skipSpace();
		int smark = source.mark();
		String result = null; 

		int begin = source.mark();
		int ch = source.next();
		if (ch != '"') return rollbackStr(smark, source.substring(smark, source.mark()+1));
		ch = source.next();
		while (ch != '"' && !source.isEOF()) {
			// TODO: if (ch == '&')  
			ch = source.next();
		}
		int end = source.mark();
		if (ch != '"') return rollbackStr(smark, "");
		result = source.substring(begin+1, end-1);
		return result;
	}


	// Identifier ::= // "[a-zA-Z_][a-zA-Z0-9_]*"
	private String pIdentifier() {
		skipSpace();
		int smark = source.mark();

		int begin = source.mark();
		int ch = source.next();
		if (!isAlpha(ch) && ch != '_') return rollbackStr(smark, "");

		ch = source.next();
		while (!source.isEOF() && 
			(isAlpha(ch) || isNumber(ch) || ch == '_')) {
			ch = source.next();
		}
		source.prev();
		int end = source.mark();
		String name = source.substring(begin, end);
		return name;
	}

	// Regexp ::= /正規表現/
	private final String pRegexp() {
		skipSpace();
		int smark = source.mark();
		if (source.next() != '/') return rollbackStr(smark, "");

		StringBuffer sbuf = new StringBuffer(20);
		if (subLiteral('/', sbuf) == null) rollbackStr(smark, "missing /.");
		return sbuf.toString();
	}
	private final Object subLiteral(int tarminator, StringBuffer sbuf) {
		int pos = 0;
		int ch = -1;
		while (!source.isEOF()) {
			ch = source.next();
			if (ch == tarminator) return sbuf;
			if (ch == '\\') {
				sbuf.append((char)ch);
				ch = source.next();
			}
			sbuf.append((char)ch);
		}
		return null;
	}

	// Comment ::= javaスタイルコメント
	private final String pComment() {
		int smark = source.mark();

		int ch0 = source.next();
		int ch1 = source.next();
		if (!(ch0 == '/' && (ch1 == '/' || ch1 == '*'))) {
			return rollbackStr(smark, "");
		}

		if (ch1 == '/') {
			while (!source.isEOF() && source.next() != '\n');
		} else {
			int pos = source.indexOf("*/");
			if (pos == -1) return (String) rollbackStr(smark, "Not found '*/'");
			source.next(pos+2);
		}
		return source.substring(smark, source.mark());
	}

	protected void skipSpace() {
		while (pSpace() || pComment() != null);
	}

	private Expr rollback(int smark) {
		return rollback(smark, "");
	}
	private Expr rollback(int smark, String msg) {
		super.rollbackStr(smark, msg);
		return null;
	}

}


