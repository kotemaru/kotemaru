package org.kotemaru.android.logicasync.apt;

import org.kotemaru.apthelper.AptUtil;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.ExecutableElement;


public class LogicHelper extends AptUtil {
	TypeElement classDecl;

	public LogicHelper(TypeElement classDecl) {
		this.classDecl = classDecl;
	}

	public TaskHelper getStateHelper(ExecutableElement decl) {
		return new TaskHelper(classDecl, decl);
	}
}

