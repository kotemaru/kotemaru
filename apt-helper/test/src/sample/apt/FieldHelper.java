package sample.apt;

import sample.annotation.Attrs;
import sample.annotation.AutoBean;

import javax.lang.model.element.VariableElement;
import javax.lang.model.element.TypeElement;


public class FieldHelper {
	//private FieldDeclaration decl;
	private Attrs attrs;
	private AutoBean autoBean;

	public FieldHelper(TypeElement classDecl, VariableElement decl) {
		//this.decl = decl;
		this.attrs = decl.getAnnotation(Attrs.class);
		this.autoBean = classDecl.getAnnotation(AutoBean.class);
	}

	public boolean hasGetter() {
		if (attrs != null) return attrs.getter();
		return autoBean.getter();
	}
	public boolean hasSetter() {
		if (attrs != null) return attrs.setter();
		return autoBean.setter();
	}
}

