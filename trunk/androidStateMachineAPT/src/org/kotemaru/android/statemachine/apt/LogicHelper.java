package org.kotemaru.android.statemachine.apt;

import org.kotemaru.android.statemachine.annotation.*;
import org.kotemaru.apthelper.AptUtil;

import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;


public class LogicHelper extends AptUtil {
	TypeDeclaration classDecl;

	public LogicHelper(TypeDeclaration classDecl) {
		this.classDecl = classDecl;
	}

	public TaskHelper getStateHelper(MethodDeclaration decl) {
		return new TaskHelper(classDecl, decl);
	}
}

