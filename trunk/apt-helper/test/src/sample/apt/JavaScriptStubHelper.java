package sample.apt;

import org.kotemaru.apthelper.AptUtil;

import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;


public class JavaScriptStubHelper extends AptUtil {
	TypeDeclaration classDecl;

	public JavaScriptStubHelper(TypeDeclaration classDecl) {
		this.classDecl = classDecl;
	}

}

