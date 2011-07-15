package org.kotemaru.aptutil;

import java.util.Set;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

public class AutoBeanApFactory implements AnnotationProcessorFactory {
	private static final String PKG	= "kotemaru.autobean.annotation.";
	private static final String AUTO_BEAN	= PKG+"AutoBean";

	private Collection supportedAnnotationTypes = Arrays.asList(
		AUTO_BEAN
	);
	private Collection supportedOptions = Collections.emptySet();

	public Collection supportedAnnotationTypes() {
		return supportedAnnotationTypes;
	}

	public Collection supportedOptions() {
		return supportedOptions;
	}

	public AnnotationProcessor getProcessorFor(Set atds, AnnotationProcessorEnvironment env) {
		if (atds.contains(env.getTypeDeclaration(AUTO_BEAN))) {
			return new AutoBeanAp(env);
		} else {
			return AnnotationProcessors.NO_OP;
		}
	}
}
