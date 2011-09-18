/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.exjs;

import org.kotemaru.util.jsg.*;

public class JsTokenizer implements Tokenizer {

	static final String[] SIGNS = {
"!",
"!=" ,
"!==",
"%",
"%=",
"&",
"&&",
"&=",
"(",
")",
"*",
"*=",
"+",
"++",
"+=",
",",
"-",
"--",
"-=",
".",
"/",
"/=",
"//",
"/*",
":",
";",
"<",
"<<",
"<<=" ,
"<=",
"=",
"=",
"==",
"===",
">",
">=",
">>",
">>=",
">>>",
">>>=",
"?",
"[",
"]",
"^",
"^=",
"{",
"|",
"||",
"|=",
"}",
"~",
"<!--",
"-->",
	};

	static final String[] KEYWORDS = {
"break",
"case",
"catch",
"continue",
"default",
"delete",
"do",
"else",
"false",
"finally",
"for",
"function",
"if",
"in",
"instanceof",
"new",
"null",
"return",
"switch",
"this",
"throw",
"true",
"try",
"typeof",
"var",
"void",
"while",
"with",
"yield",
	};

	/**
	 * StringLiteral, Number, Identifier, MultiLineComment, Punctuator
	 * @return null is tokenize failed.
	 */
	public Token tokenize(BnfDriver driver) {
		String src = driver.getCurrentString();
		if (src.length() <= 0) return null;

		Token tkn = null;
		if ((tkn=pString(driver)) != null) return tkn;
		if ((tkn=pNumber(driver)) != null) return tkn;
		if ((tkn=pIdentifier(driver)) != null) return tkn;
		if ((tkn=pMultiLineComment(driver)) != null) return tkn;

		char ch = src.charAt(0);
		if (ch == '/') return null; // for regexp.

		// NOTE: need reverse sertch.
		for (int i=SIGNS.length-1; i>=0; i--) {
			if (src.startsWith(SIGNS[i])) {
				return new Token("Punctuator", driver, SIGNS[i]);
			}
		}
		return null;
	}

	/**
	 * StringLiteral ::= StringLiteralQuote | StringLiteralApos;
	 * StringLiteralQuote ::= /"([^\\"]|\\['"\\bfnrtv]|\\\r\n|\\0[0-9]*|\\x[0-9a-zA-Z]{2}|\\u[0-9a-zA-Z]{4})*"/;
	 * StringLiteralApos  ::= /'([^\\']|\\['"\\bfnrtv]|\\\r\n|\\0[0-9]*|\\x[0-9a-zA-Z]{2}|\\u[0-9a-zA-Z]{4})*'/;
	 */
	public static Token pString(BnfDriver driver) {
		String src = driver.getCurrentString();
		if (src.length() <= 1) return null;
		char quote = src.charAt(0);
		if (quote != '"' && quote != '\'') return null;

		int len = 1;
		char ch = src.charAt(len++);
		while (ch != quote) {
			if (ch == '\\') {
				ch = src.charAt(len++);
				switch (ch) {
				 case '0': len += 3; break;
				 case 'x': len += 2; break;
				 case 'u': len += 4; break;
				}
			}
			ch = src.charAt(len++);
		}
		String val = src.substring(0, len);
		return new Token("StringLiteral", driver, val);
	}

	/**
	 * DecimalLiteral ::= DecimalLiteral_0|DecimalLiteral_1|DecimalLiteral_2;
	 * DecimalLiteral_0   ::= /[0-9]+([eE]-?[0-9]+)?/;
	 * DecimalLiteral_1   ::= /[.][0-9]+([eE]-?[0-9]+)?/;
	 * DecimalLiteral_2   ::= /[0-9]+[.][0-9]*([eE]-?[0-9]+)?/;
	 */
	public static Token pNumber(BnfDriver driver) {
		String src = driver.getCurrentString();
		if (src.length() <= 0) return null;
		char ch = src.charAt(0);
		if (!(isDigit(ch) || ch == '.')) return null;

		int len = 1;
		if (ch == '.') {
			ch = src.charAt(len++);
			if (!isDigit(ch)) return null;
			while (isDigit(ch)) ch = src.charAt(len++);
		} else {
			while (isDigit(ch)) ch = src.charAt(len++);
			if (ch == '.') {
				ch = src.charAt(len++);
				while (isDigit(ch)) ch = src.charAt(len++);
			}
		}
		if (ch == 'e' || ch == 'E') {
			ch = src.charAt(len++);
			if (ch == '-') ch = src.charAt(len++);
			if (!isDigit(ch)) return null;
			while (isDigit(ch)) ch = src.charAt(len++);
		}

		String val = src.substring(0, len-1);
		return new Token("DecimalLiteral", driver, val);
	}

	/**
	 * Identifier ::= /[a-za-Z$_][a-za-Z$_0-9]* /
	 */
	public static Token pIdentifier(BnfDriver driver) {
		String src = driver.getCurrentString();
		if (src.length() <= 0) return null;
		char ch = src.charAt(0);
		if (!isLetter(ch)) return null;
 
		int len = 1;
		while (isLetter(ch) || isDigit(ch)) {
			ch = src.charAt(len++);
		}
		String val = src.substring(0, len-1);

		// NOTE: need reverse sertch.
		for (int i=KEYWORDS.length-1; i>=0; i--) {
			if (KEYWORDS[i].equals(val)) {
				return new Token("Keyword", driver, KEYWORDS[i]);
			}
		}	
		return new Token("Identifier", driver, val);
	}

	/**
	 * MultiLineComment = C style comment.
	 */
	public static Token pMultiLineComment(BnfDriver driver) {
		String src = driver.getCurrentString();
		if (!src.startsWith("/*")) return null;
		int len = src.indexOf("*/",2);
		if (len == -1) return null;
		String val = src.substring(0, len+2);
		return new Token("MultiLineComment", driver, val);
	}



	private static boolean isLetter(char ch) {
		return ('a'<= ch && ch <='z') || ('A'<= ch && ch <='Z') 
			|| ch == '$' || ch == '_' ;
	}
	private static boolean isDigit(char ch) {
		return ('0'<= ch && ch <='9');
	}
	private static boolean isHexDigit(char ch) {
		return isDigit(ch)
			|| ('a'<= ch && ch <='f') || ('A'<= ch && ch <='F'); 
	}

}

