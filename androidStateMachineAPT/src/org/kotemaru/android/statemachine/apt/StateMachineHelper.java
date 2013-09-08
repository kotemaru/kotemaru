package org.kotemaru.android.statemachine.apt;

import org.kotemaru.android.statemachine.annotation.*;
import org.kotemaru.apthelper.AptUtil;

import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;


public class StateMachineHelper extends AptUtil {
	TypeDeclaration classDecl;

	public StateMachineHelper(TypeDeclaration classDecl) {
		this.classDecl = classDecl;
	}

	public StateHelper getStateHelper(MethodDeclaration decl) {
		return new StateHelper(classDecl, decl);
	}
}

