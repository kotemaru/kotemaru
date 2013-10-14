package sample.apt;

import org.kotemaru.apthelper.AptUtil;

import javax.lang.model.element.VariableElement;
import javax.lang.model.element.TypeElement;


public class AutoBeanHelper extends AptUtil {
	TypeElement classDecl;

	public AutoBeanHelper(TypeElement classDecl) {
		this.classDecl = classDecl;
	}

	public FieldHelper getFieldHelper(VariableElement decl) {
		return new FieldHelper(classDecl, decl);
	}
}

