package org.kotemaru.apthelper;

import com.sun.mirror.declaration.TypeDeclaration;

public interface ClassProcessor {
	boolean processClass(TypeDeclaration classDecl) throws Exception;
}
