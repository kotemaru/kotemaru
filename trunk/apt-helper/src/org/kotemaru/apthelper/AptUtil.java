package org.kotemaru.apthelper;

import java.io.IOException;
import java.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/**
 * 注釈処理の為の各種ユーティリティ。
 * <br>- helper の基底クラスとして使える。
 * @author kotemaru.org
 */
public class AptUtil {

	public AptUtil(TypeElement classDecl, ProcessingEnvironment env) {
		// nop.
	}
	public AptUtil(TypeElement classDecl) {
		// nop.
	}
	public AptUtil() {
		// nop.
	}

	/**
	 * 要素の宣言が private なら ture を返す。
	 * @param d
	 * @return
	 */
	public static  boolean isPrivate(Element d)  {
		Collection<Modifier> mods = d.getModifiers();
		for (Modifier mod : mods)  {
			if (Modifier.PRIVATE.equals(mod))  {
				return true;
			} else if (Modifier.PROTECTED.equals(mod))  {
				return false;
			} else if (Modifier.PUBLIC.equals(mod))  {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 要素の宣言が public なら ture を返す。
	 * @param d
	 * @return
	 */
	public static  boolean isPublic(Element d)  {
		Collection<Modifier> mods = d.getModifiers();
		return mods.contains(Modifier.PUBLIC);
	}
	/**
	 * 要素の宣言が static なら ture を返す。
	 * @param d
	 * @return
	 */
	public static  boolean isStatic(Element d)  {
		Collection<Modifier> mods = d.getModifiers();
		return mods.contains(Modifier.STATIC);
	}

	/**
	 * 要素の宣言が abstract なら ture を返す。
	 * @param d
	 * @return
	 */
	public static  boolean isAbstract(Element d)  {
		Collection<Modifier> mods = d.getModifiers();
		return mods.contains(Modifier.ABSTRACT);
	}

	/**
	 * 要素の修飾子を空白区切りの文字列で返す。
	 * @param d
	 * @param ignore 除外する修飾子
	 * @return
	 */
	public static String getModifiers(Element d, Modifier ignore) {
		Collection<Modifier> mods = d.getModifiers();
		if (mods.size() == 0) return "";
		StringBuilder sbuf = new StringBuilder(mods.size()*20);
		for (Modifier mod : mods)  {
			if (!mod.equals(ignore)) sbuf.append(mod);
			sbuf.append(' ');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}
	

	/**
	 * フィールド要素の一覧を返す。
	 * @param classDecl クラス定義
	 * @return
	 */
	public static List<VariableElement> getFields(TypeElement classDecl) {
		return ElementFilter.fieldsIn(classDecl.getEnclosedElements());
	}
	/**
	 * メソッド要素の一覧を返す。
	 * @param classDecl クラス要素
	 * @return
	 */
	public static List<ExecutableElement> getMethods(TypeElement classDecl) {
		return ElementFilter.methodsIn(classDecl.getEnclosedElements());
	}

	/**
	 * メソッドの引数宣言を文字列で返す。
	 * <br>- "型 引数名,…"
	 * @param d メソッド要素
	 * @return
	 */
	public static String getParams(ExecutableElement d) {
		Collection<? extends VariableElement> params = d.getParameters();
		if (params.size() == 0) return "";
		StringBuilder sbuf = new StringBuilder(params.size()*20);
		for (VariableElement param : params)  {
			sbuf.append(param.asType());
			sbuf.append(' ');
			sbuf.append(param.getSimpleName());
			sbuf.append(',');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}
	
	/**
	 * メソッドの引数を文字列で返す。
	 * <br>- "引数名,…"
	 * @param d メソッド要素
	 * @return
	 */
	public static String getArguments(ExecutableElement d) {
		Collection<? extends VariableElement> params = d.getParameters();
		if (params.size() == 0) return "";
		StringBuilder sbuf = new StringBuilder(params.size()*20);
		for (VariableElement param : params)  {
			sbuf.append(param.getSimpleName());
			sbuf.append(',');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}

	/**
	 * メソッドのthrows宣言を文字列で返す。
	 * <br>- "throws 例外,…"
	 * @param d メソッド要素
	 * @return
	 */
	public static String getThrows(ExecutableElement d) {
		Collection<? extends TypeMirror> params = d.getThrownTypes();
		if (params.size() == 0) return "";
		StringBuilder sbuf = new StringBuilder(params.size()*20);
		sbuf.append("throws ");
		for (TypeMirror param : params)  {
			sbuf.append(param.toString());
			sbuf.append(',');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}

	/**
	 * 名前の先頭一文字を英大文字に変換する。
	 * @param name
	 * @return
	 */
	public static String getCaptalName( String name ) {
		return name.substring(0,1).toUpperCase() + name.substring(1);
	}
	
	/**
	 * クラスのパッケージ名を返す。
	 * @param classDecl クラス要素
	 * @return
	 */
	public static String getPackageName(TypeElement classDecl) {
		String fullName = classDecl.getQualifiedName().toString();
		int pos = fullName.lastIndexOf('.');
		if (pos == -1) return null;
		return fullName.substring(0, pos);
	}

	/**
	 * クラスのパッケージからの相対パッケージ名を返す。
	 * <br>- "." は現在地。
	 * <br>- "../abc" は兄弟パッケージ "abc"
	 * @param classDecl このクラスのパッケージが現在地。
	 * @param path 相対パス
	 * @return
	 */
	public static String getPackageName(TypeElement classDecl, String path) {
		String orgPkg = getPackageName(classDecl);
		if (path.equals(".")) return orgPkg;
		if (path.startsWith("/")) {
			return path.replace('/', '.').substring(1);
		}

		path = orgPkg.replace('.', '/') +"/"+ path;
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(path.split("/")));
		for (int i=0; i<list.size();) {
			String cur = list.get(i);
			if (cur.equals(".")) {
				list.remove(i);
			} else if (cur.equals("..")) {
				i--;
				list.remove(i);
				list.remove(i);
			} else {
				i++;
			}
		}

		StringBuilder sbuf = new StringBuilder();
		for (int i=0; i<list.size(); i++) {
			sbuf.append(".");
			sbuf.append(list.get(i));
		}
		return sbuf.toString().substring(1);
	}

	public static List<String> getClassesInPackage(String pkg) throws IOException {
		List<String> result = new ArrayList<String>();
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		JavaFileManager fm = compiler.getStandardFileManager(
		    new DiagnosticCollector<JavaFileObject>(), null, null);

		// 一覧に含めるオブジェクト種別。以下はクラスのみを含める。
		Set<JavaFileObject.Kind> kind = new HashSet<JavaFileObject.Kind>();
		kind.add(JavaFileObject.Kind.SOURCE);
		kind.add(JavaFileObject.Kind.CLASS);

		Iterable<JavaFileObject> list =
			fm.list(StandardLocation.ANNOTATION_PROCESSOR_PATH  , pkg, kind, false);
		for ( JavaFileObject item : list) {
		   result.add(pkg+"."+item.getName().replaceFirst("[.]class$",""));
		}
		return result;
	}

	
	
}
