package sample.apt;

import org.kotemaru.apthelper.AptUtil;

import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;


public class AutoBeanHelper extends AptUtil {
	TypeDeclaration classDecl;

	public AutoBeanHelper(TypeDeclaration classDecl) {
		this.classDecl = classDecl;
	}

	public FieldHelper getFieldHelper(FieldDeclaration decl) {
		return new FieldHelper(classDecl, decl);
	}
}

