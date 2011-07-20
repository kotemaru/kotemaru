package org.kotemaru.apthelper;

import java.util.*;

import org.kotemaru.apthelper.annotation.ProcessorGenerate;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;

public class AptHelperFactory implements AnnotationProcessorFactory {
	private static final String TYPE_NAME = ProcessorGenerate.class.getName();
	private static final List OPTIONS = new ArrayList(0);
	private static final List TYPES = new ArrayList(1);
	static {
		TYPES.add(TYPE_NAME);
	}
	public Collection supportedOptions() {
		return OPTIONS;
	}

	public Collection<String> supportedAnnotationTypes() {
		return TYPES;
	}

	public AnnotationProcessor getProcessorFor(Set atds, AnnotationProcessorEnvironment env) {
		if (atds.contains(env.getTypeDeclaration(TYPE_NAME))) {
			return new AptHelperProcessor(env);
		}
		return AnnotationProcessors.NO_OP;
	}

}
