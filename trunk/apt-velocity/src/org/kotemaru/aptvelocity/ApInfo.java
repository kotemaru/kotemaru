package org.kotemaru.aptvelocity;


import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.TypeDeclaration;

public class ApInfo {
	private Class annotation;
	private Class processor;

	public ApInfo(Class anno, Class proc) {
		setAnnotation(anno);
		setProcessor(proc);
	}

	public Class getAnnotation() {
		return annotation;
	}
	public void setAnnotation(Class annotation) {
		this.annotation = annotation;
	}
	public Class getProcessor() {
		return processor;
	}
	public void setProcessor(Class processor) {
		this.processor = processor;
	}


	public String getSimpleName() {
		return annotation.getSimpleName();
	}
	public String getName() {
		return annotation.getName();
	}
	public TypeDeclaration getTypeDeclaration(AnnotationProcessorEnvironment env) {
		return env.getTypeDeclaration(annotation.getName());
	}


}
