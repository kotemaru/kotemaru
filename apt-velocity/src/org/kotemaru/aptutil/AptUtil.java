package org.kotemaru.aptutil;

import java.util.Collection;
import java.io.IOException;
import java.io.PrintWriter;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.ConstructorDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.util.DeclarationVisitor;
import com.sun.mirror.util.DeclarationVisitors;
import com.sun.mirror.util.SimpleDeclarationVisitor;
import com.sun.mirror.type.TypeMirror;

import com.sun.mirror.util.*;
import com.sun.mirror.type.*;
import com.sun.mirror.declaration.*;

import org.kotemaru.aptutil.test.annotation.*;
import java.util.*;

public class AptUtil {
	public static String apply(String templ, Map map) {
		Iterator<Map.Entry<String,String>> ite = map.entrySet().iterator();
		while(ite.hasNext()){
			Map.Entry<String,String> ent = (Map.Entry<String,String>) ite.next();
			String key = "[$][{]"+ent.getKey()+"[}]";
			String value = ent.getValue();
			templ = templ.replaceAll(key, value);
		}
		return templ;
	}
	public static String apply(String templ, Declaration d) {
		return apply(templ, d, null);
	}

	public static String apply(String templ, Declaration d, Modifier ignore) {
		templ = templ.replaceAll("[$][{]modifiers[}]", getModifiers(d, ignore));
		templ = templ.replaceAll("[$][{]name[}]", d.getSimpleName());
		templ = templ.replaceAll("[$][{]captalName[}]", getCaptalName(d.getSimpleName()));

		if (d instanceof FieldDeclaration) {
			FieldDeclaration decl = (FieldDeclaration) d;
			templ = templ.replaceAll("[$][{]type[}]", decl.getType().toString());
		}
		if (d instanceof MethodDeclaration) {
			MethodDeclaration decl = (MethodDeclaration) d;
			templ = templ.replaceAll("[$][{]returnType[}]", decl.getReturnType().toString());
		}
		if (d instanceof ExecutableDeclaration) {
			ExecutableDeclaration decl = (ExecutableDeclaration) d;
			templ = templ.replaceAll("[$][{]parameters[}]",  getParams(decl));
			templ = templ.replaceAll("[$][{]arguments[}]",  getArguments(decl));
			templ = templ.replaceAll("[$][{]throws[}]", getThrows(decl));
		}
		return templ;
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

}
