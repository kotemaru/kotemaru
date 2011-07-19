package sample.apt;

import sample.annotation.Attrs;
import sample.annotation.AutoBean;

import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;


public class FieldHelper {
	//private FieldDeclaration decl;
	private Attrs attrs;
	private AutoBean autoBean;

	public FieldHelper(TypeDeclaration classDecl, FieldDeclaration decl) {
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

