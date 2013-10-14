package sample.apt;

import org.kotemaru.apthelper.AptUtil;

import javax.lang.model.element.VariableElement;
import javax.lang.model.element.TypeElement;

public class JavaScriptStubHelper extends AptUtil {
	TypeElement classDecl;

	public JavaScriptStubHelper(TypeElement classDecl) {
		this.classDecl = classDecl;
	}

}

