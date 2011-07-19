package sample.apt;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.lang.reflect.*;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;

public class ApFactory implements AnnotationProcessorFactory {
	private static final String AP_SUFFIX = "Ap";

	private static final List OPTIONS = new ArrayList(0);
	private static final List<String> TYPES = Arrays.asList(new String[]{
		sample.annotation.AutoBean.class.getName(),
	});

	public Collection supportedOptions() {
		return OPTIONS;
	}

	public Collection<String> supportedAnnotationTypes() {
		return TYPES;
	}

	public AnnotationProcessor getProcessorFor(Set atds, AnnotationProcessorEnvironment env) {
		for(String type : TYPES) {
			try {
				if (atds.contains(env.getTypeDeclaration(type))) {
					return createProcessor(type, env);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return AnnotationProcessors.NO_OP;
	}

	private AnnotationProcessor createProcessor(String type, AnnotationProcessorEnvironment env)
				throws Exception
	{
		int idx = type.lastIndexOf('.');
		String pkg = type.substring(0,idx);
		String simpleName = type.substring(idx+1);

		String apname = pkg.replaceFirst("[^.]*$","apt")+"."+simpleName+AP_SUFFIX;
		Class apCls = Class.forName(apname);
		Constructor constractor = apCls.getConstructor(AnnotationProcessorEnvironment.class);
		return (AnnotationProcessor) constractor.newInstance(env);
	}


}
