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
import java.util.regex.*;


public interface Expr {
	public Object eval(BnfDriver driver);
	public void setName(String name);
	public String getName();
	public boolean isSkipped();
	public boolean isToken();


	public static abstract class BaseExpr implements Expr {
		private String name = null;
		protected boolean isSkipped = false;
		public void setName(String name) {this.name = name;}
		public String getName() {return name;}
		public boolean isSkipped() {return isSkipped;}
		public boolean isToken() {return false;}
	}

	public static abstract class ListExpr extends BaseExpr {
		ArrayList<Expr> list = new ArrayList<Expr>();
		public ListExpr(){}
		public void add(Expr expr) {list.add(expr);}
		public Expr get(int idx) {return (Expr) list.get(idx);}
		public int size(){return list.size();}

		public String toString(String pipe) {
			String s = list.get(0).toString();
			for (int i=1; i<list.size(); i++) {
				s = s + pipe + list.get(i);
			}
			return s;
		}

	}

	public static class OrExpr extends ListExpr {
		public OrExpr(){}
		public String toString() {return toString(" | ");}

		public Object eval(BnfDriver driver) {
			int[] mark = driver.mark();
			for (int i=0; i<list.size(); i++) {
				Expr expr = list.get(i);
				if (expr.isToken() && !expr.isSkipped()) driver.skipTokens();
				Object rv = expr.eval(driver);
				if (rv != null) {
					return rv;
				}
			}
			return driver.rollback(mark, this);
		}
	}

	public static class AndExpr extends ListExpr {
		public AndExpr(){}
		public String toString() {return toString(" ");}

		public Object eval(BnfDriver driver) {
			int[] mark = driver.mark();
			Object rv = null;
			for (int i=0; i<list.size(); i++) {
				Expr expr = list.get(i);
				if (expr.isToken() && !expr.isSkipped()) driver.skipTokens();
				rv = expr.eval(driver);
				if (rv == null) return driver.rollback(mark, this);
			}
			return rv;
		}
	}

	public static class NotExpr extends BaseExpr {
		Expr atom;
		public NotExpr(Expr atom){
			this.atom = atom;
		}
		public String toString() {return "!"+atom;}

		public Object eval(BnfDriver driver) {
			if (atom.isToken() && !atom.isSkipped()) driver.skipTokens();

			int[] mark = driver.mark();
			Object rv = atom.eval(driver);
			if (rv != null) return driver.rollback(mark, this);
			return "OK";
		}

	}

	public static class Block extends BaseExpr {
		int minOccers;
		int maxOccers;
		Expr expr;
		public Block(int min, int max, Expr expr){
			minOccers = min;
			maxOccers = max;
			this.expr = expr;
		}
		public void setMaxOccers(int max) {maxOccers = max;}
		public String toString() {
			String s = (minOccers == 0) ? "["+expr+"]" : "{"+expr+"}";
			if (maxOccers > 2) s = s +"...";
			return s;
		}

		public Object eval(BnfDriver driver) {
			if (expr.isToken() && !expr.isSkipped()) driver.skipTokens();

			int[] mark = driver.mark();

			int cnt = 0;
			Object rv = expr.eval(driver);
			while (rv != null) {
				cnt++;
				if (cnt >= maxOccers) break;
				rv = expr.eval(driver);
			}
			if (cnt < minOccers) return driver.rollback(mark, this); 
			return "OK";
		}
	}
	public static class IdentifierRefer extends BaseExpr {
		String identfier;
		public IdentifierRefer(String tkn) {
			identfier = tkn;
		}
		public String toString() {
			return identfier;
		}
		public Object eval(BnfDriver driver) {
			Expr expr = driver.getExpr(identfier);
			if (expr == null) {
				throw new RuntimeException("Not found define "+identfier);
			}
			driver.debugStack.push(identfier);
			try {
				if (expr.isToken() && !expr.isSkipped()) driver.skipTokens();

				int[] mark = driver.mark();
				if (!expr.isToken()) driver.printStartTag(identfier);
				Object rv = expr.eval(driver);
				if (rv == null) return driver.rollback(mark, this);
				if (!expr.isToken()) driver.printEndTag(identfier);
//System.out.println("=>match:"+this+":"+driver.debugStack);
				return rv;
			} finally {
				driver.debugStack.pop();
			}
		}
	}

//----------------------------------------------------------------------
// Tokens 

	public static class LiteralToken extends BaseExpr {
		String literal;
		public LiteralToken(String tkn) {
			literal = tkn;
		}
		public String toString() {return '"'+literal+'"';}
		public boolean isToken() {return true;}

		public Object eval(BnfDriver driver) {

			int[] mark = driver.mark();
			if (driver.pToken(literal) == null) return driver.rollback(mark, this);
			driver.printCData(literal);
//System.out.println("=>match:"+this+" => "+literal+":"+mark[0]+":"+driver.mark()[0]+":"+driver.debugStack);
			return literal;
		}
	}

	public static class RegexpToken extends BaseExpr {
		String regex;
		Matcher matcher;
		public RegexpToken(String patt, boolean skipped) {
			regex = patt;
			isSkipped = skipped;

			if (!patt.startsWith("^")) patt = "^"+patt;
			matcher = Pattern.compile(patt).matcher("");
		}
		public String toString() {return "/"+regex+"/";}
		public boolean isToken() {return true;}

		public Object eval(BnfDriver driver) {

			int[] mark = driver.mark();
			String curStr = driver.getCurrentString();
			matcher.reset(curStr);
			if (!matcher.find()) return  driver.rollback(mark, this);
			String token = curStr.substring(0, matcher.end());
			driver.next(matcher.end());
			driver.printTag(getName(), token, mark[0]);
//System.out.println("=>match:"+this+" => "+token+":"+driver.debugStack);
			return token;
		}
	}

	public static class TokenizerToken extends BaseExpr {
		public TokenizerToken(boolean skipped) {
			isSkipped = skipped;
		}
		public String toString() {return "$Tokenizer()";}
		public boolean isToken() {return true;}

		public Object eval(BnfDriver driver) {

			int[] mark = driver.mark();
			Token tkn = driver.tokenize();
			if (tkn == null) return null;
			if (!tkn.isType(getName())) return driver.rollback(mark, this);
			driver.nextCurrentToken();
			driver.printTag(getName(), tkn.getValue(), mark[0]);
//System.out.println("=>match:"+this+" => "+tkn+":"+driver.debugStack);
			return tkn;
		}
	}

	public static class Extension extends BaseExpr {
		private static final Class[] TYPE = {BnfDriver.class, String.class};
		Method method;
		String path;
		String arg;
		public Extension(String path, String arg, boolean skipped) {
			this.path = path;
			this.arg = arg;
			this.isSkipped = skipped;
			try {
				int idx = path.lastIndexOf('.');
				String cname = path.substring(0, idx);
				String mname = path.substring(idx+1);
				Class cls = Class.forName(cname);
				method = cls.getMethod(mname, TYPE);
			} catch (Exception e) {
//System.out.println(e.toString());
				throw new RuntimeException(e);
			}
		}
		public String toString() {return "$"+path+'('+arg+')';}
		public boolean isToken() {return true;}

		public Object eval(BnfDriver driver) {

			int[] mark = driver.mark();
			try {
				Object rv = method.invoke(null, new Object[]{driver, arg});
				if (rv == null) return driver.rollback(mark, this);
//System.out.println("=>match:"+this+" => "+rv+":"+driver.debugStack);
				return rv;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}


}

