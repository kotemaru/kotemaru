package org.kotemaru.apthelper;

import javax.lang.model.element.TypeElement;

public interface ClassProcessor {
	boolean processClass(TypeElement classDecl) throws Exception;
}
