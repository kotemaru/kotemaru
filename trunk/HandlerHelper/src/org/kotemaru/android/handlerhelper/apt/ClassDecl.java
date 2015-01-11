package org.kotemaru.android.handlerhelper.apt;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.kotemaru.android.handlerhelper.annotation.DelegateHandlerClass;
import org.kotemaru.android.handlerhelper.rt.OnDelegateHandlerErrorListener;

public class ClassDecl extends AptUtil {
	private TypeElement typeElem;
	private ProcessingEnvironment env;

	// private DelegateHandlerClass annotation;

	public ClassDecl(TypeElement typeElem, ProcessingEnvironment env) {
		this.typeElem = typeElem;
		this.env = env;
		// this.annotation = typeElem.getAnnotation(DelegateHandlerClass.class);
	}

	public List<MethodDecl> getMethods() {
		List<ExecutableElement> list = getMethods(typeElem);
		List<MethodDecl> resList = new ArrayList<MethodDecl>(list.size());
		for (ExecutableElement elem : list) {
			resList.add(new MethodDecl(typeElem, elem));
		}
		return resList;
	}

	public boolean hasErrorHandler() {
		TypeElement listenerIfElem = env.getElementUtils().getTypeElement(
				OnDelegateHandlerErrorListener.class.getCanonicalName());
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

	public CharSequence getImplements() {
		List<AnnotationValue> impls = getAnnoValues(typeElem, DelegateHandlerClass.class, "implement");
		if (impls == null || impls.size() == 0) return "";
		StringBuilder sbuf = new StringBuilder("implements ");
		for (AnnotationValue impl : impls) {
			if (impls.get(0) != impl) sbuf.append(" ,");
			DeclaredType type = (DeclaredType) impl.getValue();
			sbuf.append(((TypeElement) type.asElement()).getQualifiedName());
		}
		return sbuf;
	}
	public List<AnnotationValue> getAnnoValues(Element element, Class<? extends Annotation> annotationClass, String key) {
		AnnotationMirror annotationMirror = findAnnotationMirror(element, annotationClass);
		Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
			if (key.equals(entry.getKey().getSimpleName().toString())) {
				AnnotationValue annotationValue = entry.getValue();
				@SuppressWarnings("unchecked")
				List<AnnotationValue> classes = (List<AnnotationValue>) annotationValue.getValue();
				return classes;
			}
		}
		return null;
	}

	public AnnotationMirror findAnnotationMirror(Element annotatedElement, Class<? extends Annotation> annotationClass) {
		List<? extends AnnotationMirror> annotationMirrors = annotatedElement.getAnnotationMirrors();
		for (AnnotationMirror annotationMirror : annotationMirrors) {
			TypeElement annotationElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
			if (isAnnotation(annotationElement, annotationClass)) {
				return annotationMirror;
			}
		}
		return null;
	}
	public boolean isAnnotation(TypeElement annotation, Class<? extends Annotation> annotationClass) {
		return annotation.getQualifiedName().toString().equals(annotationClass.getName());
	}
}
