package org.kotemaru.jsrpc.apt;

import org.kotemaru.apthelper.AptUtil;

import com.sun.mirror.declaration.ExecutableDeclaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;


public class JavaScriptStubHelper extends AptUtil {
	TypeDeclaration classDecl;

	public JavaScriptStubHelper(TypeDeclaration classDecl) {
		this.classDecl = classDecl;
	}
	
	public String getArgumentsAfter(ExecutableDeclaration d) {
		String s = super.getArguments(d).trim();
		if (s.length() > 0) s = ","+s;
		return s;
	}
}

