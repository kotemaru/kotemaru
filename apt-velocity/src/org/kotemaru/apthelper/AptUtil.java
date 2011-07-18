package org.kotemaru.apthelper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.mozilla.javascript.NativeJavaPackage;

import com.sun.mirror.type.*;
import com.sun.mirror.declaration.*;

public class AptUtil {

	public AptUtil(TypeDeclaration classDecl) {
		// nop.
	}


	public static  boolean isPrivate(Declaration d)  {
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

	public static  boolean isAbstract(Declaration d)  {
		Collection<Modifier> mods = d.getModifiers();
		for (Modifier mod : mods)  {
			if (Modifier.ABSTRACT.equals(mod))  {
				return true;
			}
		}
		return false;
	}

	public static String getModifiers(Declaration d, Modifier ignore) {
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

	public static String getParams(ExecutableDeclaration d) {
		Collection<ParameterDeclaration> params = d.getParameters();
		if (params.size() == 0) return "";
		StringBuffer sbuf = new StringBuffer(params.size()*20);
		for (ParameterDeclaration param : params)  {
			sbuf.append(param.getType());
			sbuf.append(' ');
			sbuf.append(param.getSimpleName());
			sbuf.append(',');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}
	public static String getArguments(ExecutableDeclaration d) {
		Collection<ParameterDeclaration> params = d.getParameters();
		if (params.size() == 0) return "";
		StringBuffer sbuf = new StringBuffer(params.size()*20);
		for (ParameterDeclaration param : params)  {
			sbuf.append(param.getSimpleName());
			sbuf.append(',');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}

	public static String getThrows(ExecutableDeclaration d) {
		Collection<ReferenceType> params = d.getThrownTypes();
		if (params.size() == 0) return "";
		StringBuffer sbuf = new StringBuffer(params.size()*20);
		sbuf.append("throws ");
		for (ReferenceType param : params)  {
			sbuf.append(param.toString());
			sbuf.append(',');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}

	public static String getCaptalName( String name ) {
		return name.substring(0,1).toUpperCase() + name.substring(1);
	}

	public static String getPackageName(TypeDeclaration classDecl, String path) {
		String orgPkg = classDecl.getPackage().getQualifiedName();
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

}
