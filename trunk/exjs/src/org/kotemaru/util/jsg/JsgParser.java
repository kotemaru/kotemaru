/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.util.jsg;

import java.io.* ;
import java.util.* ;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

/**
Main ::= [Properties] {Define}...;

Properties ::= "$PROPERTIES" JavaCode "$END";

Define ::=
	"$DEFINE" "(" DefineName ["(" CallParam ["," CallParam]... ")"] ")"
		[Statement]... 
	"$END";
DefineName ::= Identfier;
Identfier ::= // "[a-z][a-zA-Z0-9_]*"

Statement ::=
	For | If | Switch | Value | Variable | Call | JavaCode | Commnet | Error;

For ::= "$FOR" "(" XPath ")" [Statement]... "$END";
If ::= "$IF" "(" XPath ")" [Statement]... [$ELSE [Statement]...] "$END";
Switch ::= "$SWITCH" "(" XPath ")" Case... [Default] "$END"
  Case ::= "$CASE" "(" XPath ")" [Statement]... "$END"
  Default ::= "$DEFAULT" [Statement]... "$END"
Value ::= "$(" XPath ")";
Variable ::= "$VAR" "(" CallParam ")";
Error ::= "$ERROR" "(" XPath ["," XPath] ")";
Call ::= "$" DefineName "(" CallParam ["," CallParam]... ")";
CallParam ::= Identfier "=" XPath;

Comment ::= /^#.*\r?\n/;
XPath ::= XPath文字列;
JavaCode :: javaコード文字列;
*/

public class JsgParser extends ParserBase {

	private static final Result OK = new Result();

	private Output output;

	public JsgParser(Source src) throws IOException {
		super(src);
		output = new Output();
	}

	public String getString() {
		return output.getString();
	}


	// main ::= {Define | Comment}...;
	public Result parse() {
		Properties props = pProperties();

		println("<xsl:stylesheet version='1.0'");
		Iterator ite = props.keySet().iterator();
		while(ite.hasNext()) {
			String key = (String) ite.next();
			if (key.startsWith("xmlns:")) {
				println(key+"='"+props.get(key)+"'");
			}
		}

		println(" xmlns:to='xalan://jsg.ExtFunc'");
		println(" extension-element-prefixes='to'");
		println(">");

		println("<xalan:component xmlns:xalan='http://xml.apache.org/xalan'");
		println("    prefix='to' functions='jstr' >");
		println("    <xalan:script lang='javaclass' src='xalan://jsg.ExtFunc'/>");
		println("</xalan:component>");

		println("<xsl:output method='text'/>");
		println("<xsl:template match='/'>");
		println("	<xsl:call-template name='main'/>");
		println("</xsl:template>");

		while (!source.isEOF()) {
			if (pDefine() == null) {
				skipSpace();
				if (source.next() == -1) break;
				return null;
			}
		}

		println("</xsl:stylesheet>");
		return OK;
	}

	// Properties ::= "$PROPERTIES" JavaCode "$END";
	private Properties pProperties()  {
		int smark = source.mark();
		int omark = output.mark();

		Properties props = new Properties();
		props.setProperty("xmlns:xsl","http://www.w3.org/1999/XSL/Transform");

		if(pToken("$PROPERTIES") == null) {
			rollback(smark, omark);
			return null;
		}
		String code = pJavaCode();
		if (code != null) {
			try {
				props.load(new StringBufferInputStream(code));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		if(pToken("$END") == null) {
			rollback(smark, omark);
			return null;
		}
		return props;
	}

	// Define ::= "$DEFINE" "(" DefineName ["(" CallParam ["," CallParam]... ")"]  ")" [Statement]... "$END";
	private Result pDefine()  {
		int smark = source.mark();
		int omark = output.mark();
		if(pToken("$DEFINE") == null) return rollback(smark, omark);
		if(pToken("(") == null) return rollback(smark, omark);
		String name = pIdentfier();
		if(name == null) return rollback(smark, omark);

		println("<xsl:template name='"+name+"'>");

		if(pToken("(") != null) {
			while (pCallParam("xsl:param") != null) {
				if (pToken(",") == null) break;
			}
			if(pToken(")") == null) return rollback(smark, omark);
		}
		if(pToken(")") == null) return rollback(smark, omark);

		while (pStatement() != null);

		println("</xsl:template>");
		if(pToken("$END") == null) return rollback(smark, omark);

		return OK;
	}

	// Statement ::= For | If | Value | Call | JavaCode | Commnet;
	private Result pStatement() {

		if (pFor()     != null) return OK;
		if (pIf()      != null) return OK;
		if (pSwitch()  != null) return OK;
		if (pValue()   != null) return OK;
		if (pVariable()!= null) return OK;
		if (pCall()    != null) return OK;
		if (pError()   != null) return OK;

		String comment = pComment();
		if (comment != null) {
			println("<!-- comment -->");
			return OK;
		}

		String javaCode = pJavaCode();
		if (javaCode != null) {
			javaCode = javaCode.replaceAll("&","&amp;").replaceAll("<","&lt;");
			print(javaCode);
			return OK;
		}
		return null;
	}

	// For ::= "$FOR" "(" XPath ")" [Statement]... "$END";
	private Result pFor() {
		int smark = source.mark();
		int omark = output.mark();

		if(pToken("$FOR") == null) return rollback(smark, omark);
		if(pToken("(") == null) return rollback(smark, omark);
		String xpath = pXPath();
		if(xpath == null) return rollback(smark, omark);
		if(pToken(")") == null) return rollback(smark, omark);

		println("<xsl:for-each select='"+xpath+"'>");
		while (pStatement() != null);
		println("</xsl:for-each>");

		if(pToken("$END") == null) return rollback(smark, omark);
	
		return OK;
	}

	// If ::= "$IF" "(" XPath ")" [Statement]... [$ELSE [Statement]...] "$END";
	private Result pIf() {
		int smark = source.mark();
		int omark = output.mark();

		if(pToken("$IF") == null) return rollback(smark, omark);
		if(pToken("(") == null) return rollback(smark, omark);
		String xpath = pXPath();
		if(xpath == null) return rollback(smark, omark);
		if(pToken(")") == null) return rollback(smark, omark);

		println("<xsl:choose>");
		println("<xsl:when test='"+xpath+"'>");
		while (pStatement() != null);
		println("</xsl:when>");

		if(pToken("$ELSE") != null) {
			println("<xsl:otherwise>");
			while (pStatement() != null);
			println("</xsl:otherwise>");
		}

		println("</xsl:choose>");
		if(pToken("$END") == null) return rollback(smark, omark);
	
		return OK;
	}

	// Switch ::= "$SWITCH" "(" XPath ")" Case... [Default] "$END"
	private Result pSwitch() {
		int smark = source.mark();
		int omark = output.mark();

		if(pToken("$SWITCH") == null) return rollback(smark, omark);
		if(pToken("(") == null) return rollback(smark, omark);
		String xpath = pXPath();
		if(xpath == null) return rollback(smark, omark);
		if(pToken(")") == null) return rollback(smark, omark);

		println("<xsl:choose>");
		while (pCase(xpath) != null);
		pDefault();
		println("</xsl:choose>");

		if(pToken("$END") == null) return rollback(smark, omark);
	
		return OK;
	}

	//  Case ::= "$CASE" "(" XPath ")" [Statement]... "$END"
	private Result pCase(String preXPath) {
		int smark = source.mark();
		int omark = output.mark();

		if(pToken("$CASE") == null) return rollback(smark, omark);
		if(pToken("(") == null) return rollback(smark, omark);
		String xpath = pXPath();
		if(xpath == null) return rollback(smark, omark);
		if(pToken(")") == null) return rollback(smark, omark);

		println("<xsl:when test='"+preXPath+"="+xpath+"'>");
		while (pStatement() != null);
		println("</xsl:when>");

		if(pToken("$END") == null) return rollback(smark, omark);
	
		return OK;
	}

	//  Default ::= "$DEFAULT" [Statement]... "$END"
	private Result pDefault() {
		int smark = source.mark();
		int omark = output.mark();

		if(pToken("$DEFAULT") == null) return rollback(smark, omark);

		println("<xsl:otherwise>");
		while (pStatement() != null);
		println("</xsl:otherwise>");

		if(pToken("$END") == null) return rollback(smark, omark);
	
		return OK;
	}

	//Value ::= "$(" XPath ")";
	private Result pValue() {
		int smark = source.mark();
		int omark = output.mark();

		if(pToken("$(") == null) return rollback(smark, omark);
		String xpath = pXPath();
		if(xpath == null) return rollback(smark, omark);
		if(pToken(")") == null) return rollback(smark, omark);

		print("<xsl:value-of select='"+xpath+"'/>");

		return OK;
	}

	//Variable ::= "$VAR" "(" CallParam ")";
	private Result pVariable() {
		int smark = source.mark();
		int omark = output.mark();

		if(pToken("$VAR") == null) return rollback(smark, omark);
		if(pToken("(") == null) return rollback(smark, omark);
		pCallParam("xsl:variable");
		if(pToken(")") == null) return rollback(smark, omark);
		return OK;
	}

	//Call ::= "$" DefineName "(" CallParam ["," CallParam]... ")";
	private Result pCall() {
		int smark = source.mark();
		int omark = output.mark();

		if(pToken("$") == null) return rollback(smark, omark);
		String name = pIdentfier();
		if(pToken("(") == null) return rollback(smark, omark);

		println("<xsl:call-template name='"+name+"'>");
		while (pCallParam("xsl:with-param") != null){
			if (pToken(",") == null) break;
		}
		println("</xsl:call-template>");

		if(pToken(")") == null) return rollback(smark, omark);
		return OK;
	}
	
	// CallParam ::= Identfier "=" XPath;
	private Result pCallParam(String tagName) {
		int smark = source.mark();
		int omark = output.mark();

		String name = pIdentfier();
		if(name == null) return rollback(smark, omark);
		if(pToken("=") == null) return rollback(smark, omark);
		String xpath = pXPath();
		if(xpath == null) return rollback(smark, omark);

		println("<"+tagName+" name='"+name+"' select='"+xpath+"'/>");
		return OK;
	}

	// Error ::= "$ERROR" "(" Identfier ")";
	private Result pError() {
		int smark = source.mark();
		int omark = output.mark();

		if(pToken("$ERROR") == null) return rollback(smark, omark);
		if(pToken("(") == null) return rollback(smark, omark);
		String message = pXPath();
		String token = null;
		if(pToken(",") != null) {
			token = pXPath();
		}
		if(pToken(")") == null) return rollback(smark, omark);

		println("<xsl:message terminate='yes'>"
				+"<xsl:text>" +message+"\nNear: </xsl:text>"
				+"<xsl:value-of select='"+token+"'/>"
				+"</xsl:message>");

		return OK;
	}

//---------------------------------------------------------------
// Tokens

	// XPath ::= // XPath
	private String pXPath() {
		int smark = source.mark();

		int begin = source.mark();
		int nest = 0;
		int ch = source.next();
		while (!source.isEOF() && !((ch == ')'||ch ==',') && nest == 0)) {
			if (ch == '(') nest++;
			if (ch == ')') nest--;
			if (ch == '\n') return rollbackStr(smark, "");
			ch = source.next();
		}
		if (source.isEOF()) return rollbackStr(smark, "");
		source.prev();
		int end = source.mark();
		String xpath = source.substring(begin, end);
		return xpath;
	}

	// Comment ::= // #.*\n
	private String pComment() {
		int smark = source.mark();

		int begin = source.mark();
		int ch = source.next();
		if (ch != '#') return rollbackStr(smark, "Unmatch comment '"+(char)ch+"'");
		ch = source.next();
		while (!source.isEOF() && ch != '\n') {
			ch = source.next();
		}
		int end = source.mark();
		return source.substring(begin+1, end);
	}

	// JavaCode :: // [^$]*
	private String pJavaCode() {
		int smark = source.mark();

		int begin = source.mark();
		int ch = source.next();
		if (ch == '$') return rollbackStr(smark, "");
		while (!source.isEOF()) {
			if (ch == '$') {
				if (source.next() != '$') {
					source.prev();
					break;
				}
			}
			ch = source.next();
		}
		if (source.isEOF()) return null;
		source.prev();
		int end = source.mark();
		String str = source.substring(begin, end);
		str.replaceAll("[$][$]", "$");
		return str;
	}

	// Identfier ::= // "[a-z][a-zA-Z0-9_]*"
	private String pIdentfier() {
		int smark = source.mark();

		skipSpace();
		int begin = source.mark();
		int ch = source.next();
		if (!('a' <= ch && ch <= 'z')) return rollbackStr(smark, "");

		ch = source.next();
		while (!source.isEOF() && isDegetAlpha(ch)) {
			ch = source.next();
		}
		source.prev();
		int end = source.mark();
		String name = source.substring(begin, end);
		return name;
	}

//----------------------------------------------------------------------


	private boolean isDegetAlpha(int ch) {
		return ('a' <= ch && ch <= 'z') 
			|| ('A' <= ch && ch <= 'Z') 
			|| ('0' <= ch && ch <= '9')
			|| ch == '_'; 
	}


	private Result rollback(int smark, int omark) {
		//lastRollback = new RuntimeException("rollback: ", lastRollback);
		lastRollback = new RuntimeException("rollback: ");
		source.rollback(smark);
		output.rollback(omark);
		return null;
	}


	private void println(String s) {
		output.println(s);
	}
	private void print(String s) {
		output.print(s);
	}

	protected void skipSpace() {
		while (pSpace() || pComment() != null);
	}

	public static  class Result {

	}

}



