package org.kotemaru.android.handlerhelper.apt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;


public class ClassDecl extends AptUtil {
	private TypeElement typeElem;
	@SuppressWarnings("unused")
	private ProcessingEnvironment env;
	private ExecutableElement exceptionHandlerMethod;

	public ClassDecl(TypeElement typeElem,  ProcessingEnvironment env) {
		this.typeElem = typeElem;
		this.env = env;
	}

	public List<MethodDecl> getMethods() {
		List<ExecutableElement> list = getMethods(typeElem);
		List<MethodDecl> resList = new ArrayList<MethodDecl>(list.size());
		for (ExecutableElement elem : list) {
			resList.add(new MethodDecl(typeElem, elem));
		}
		return resList;
	}
	public ExecutableElement getExceptionMethod() {
		return exceptionHandlerMethod;
	}

	public Set<String> getMethodNameSet() {
		Set<String> names = new HashSet<String>();
		List<MethodDecl> list = getMethods();
		for (MethodDecl md : list) {
			if (md.isTask()) {
				names.add(md.getName());
			}
			if (md.isException()) {
				//TODO:exceptionHandlerMethod = elem;
			}
		}
		return names;
	}

	public Class<?> impl() {
		//Element HandleType = env.getElementUtils().getTypeElement(Handling.class.getName());
		//TypeMirror actionType = HandleType.asType();
		return null;
	}


}
