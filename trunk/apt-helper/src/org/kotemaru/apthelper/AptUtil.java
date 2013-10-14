package org.kotemaru.apthelper;

import java.io.IOException;
import java.util.*;

//import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

//import org.mozilla.javascript.NativeJavaPackage;

//import com.sun.mirror.type.*;
//import com.sun.mirror.declaration.Element*;
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


public class AptUtil {

	public AptUtil(TypeElement classDecl) {
		// nop.
	}
	public AptUtil() {
		// nop.
	}


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
	
	public static  boolean isPublic(Element d)  {
		Collection<Modifier> mods = d.getModifiers();
		return mods.contains(Modifier.PUBLIC);
	}
	public static  boolean isStatic(Element d)  {
		Collection<Modifier> mods = d.getModifiers();
		return mods.contains(Modifier.STATIC);
	}

	public static  boolean isAbstract(Element d)  {
		Collection<Modifier> mods = d.getModifiers();
		return mods.contains(Modifier.ABSTRACT);
	}

	public static String getModifiers(Element d, Modifier ignore) {
		Collection<Modifier> mods = d.getModifiers();
		if (mods.size() == 0) return "";
		StringBuffer sbuf = new StringBuffer(mods.size()*20);
		for (Modifier mod : mods)  {
			if (!mod.equals(ignore)) sbuf.append(mod);
			sbuf.append(' ');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}
	
	public static List<VariableElement> getFields(TypeElement classDecl) {
		return ElementFilter.fieldsIn(classDecl.getEnclosedElements());
	}
	public static List<ExecutableElement> getMethods(TypeElement classDecl) {
		return ElementFilter.methodsIn(classDecl.getEnclosedElements());
	}

	public static String getParams(ExecutableElement d) {
		Collection<? extends VariableElement> params = d.getParameters();
		if (params.size() == 0) return "";
		StringBuffer sbuf = new StringBuffer(params.size()*20);
		for (VariableElement param : params)  {
			sbuf.append(param.asType());
			sbuf.append(' ');
			sbuf.append(param.getSimpleName());
			sbuf.append(',');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}
	public static String getArguments(ExecutableElement d) {
		Collection<? extends VariableElement> params = d.getParameters();
		if (params.size() == 0) return "";
		StringBuffer sbuf = new StringBuffer(params.size()*20);
		for (VariableElement param : params)  {
			sbuf.append(param.getSimpleName());
			sbuf.append(',');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}

	public static String getThrows(ExecutableElement d) {
		Collection<? extends TypeMirror> params = d.getThrownTypes();
		if (params.size() == 0) return "";
		StringBuffer sbuf = new StringBuffer(params.size()*20);
		sbuf.append("throws ");
		for (TypeMirror param : params)  {
			sbuf.append(param.toString());
			sbuf.append(',');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}

	public static String getCaptalName( String name ) {
		return name.substring(0,1).toUpperCase() + name.substring(1);
	}
	
	public static String getPackageName(TypeElement classDecl) {
		String fullName = classDecl.getQualifiedName().toString();
		int pos = fullName.lastIndexOf('.');
		if (pos == -1) return null;
		return fullName.substring(0, pos);
	}

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

		StringBuffer sbuf = new StringBuffer();
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
