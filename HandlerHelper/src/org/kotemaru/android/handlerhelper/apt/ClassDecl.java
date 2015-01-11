package org.kotemaru.android.handlerhelper.apt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.kotemaru.android.handlerhelper.rt.OnHandlingErrorListener;

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
	public boolean hasErrorHandler() {
		TypeElement listenerIfElem = env.getElementUtils().getTypeElement(OnHandlingErrorListener.class.getCanonicalName());
		List<? extends TypeMirror> types = typeElem.getInterfaces();
		for (TypeMirror type : types) {
			if (env.getTypeUtils().isAssignable(type, listenerIfElem.asType())) return true;
		}
		return false;
	}

	public Set<String> getMethodNameSet() {
		Set<String> names = new HashSet<String>();
		List<MethodDecl> list = getMethods();
		for (MethodDecl md : list) {
			if (md.isTask()) {
				names.add(md.getName());
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
