package org.kotemaru.aptvelocity;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

import org.mozilla.javascript.*;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;

public class JavaScriptApFactory implements AnnotationProcessorFactory {


	public Collection supportedOptions() {
		System.out.println("-->supportedOptions");
		List list = new ArrayList();
		//list.add("-Ascript");
		return list;
	}

	public Collection<String> supportedAnnotationTypes() {
		System.out.println("-->supportedAnnotationTypes");
		List list = new ArrayList();
		list.add("*");
		return list;

	}

	public AnnotationProcessor getProcessorFor(Set atds, AnnotationProcessorEnvironment env) {
System.out.println("-->"+env.getOptions());
		String jsFile = env.getOptions().get("-AScript");
		System.out.println("-->"+jsFile);
		jsFile="test/autoBean.js";
		try {
			Script script = compile(jsFile);
			return new JavaScriptAp(script, env);
		} catch (IOException e) {
			e.printStackTrace();
			return AnnotationProcessors.NO_OP;
		}
	}

	public static Script compile(String fname) throws IOException  {
		Reader reader = new FileReader(fname);
		Context cx = Context.enter();
		try {
			Script script = cx.compileReader(reader, fname, 1, null);
			return script;
		} finally {
			Context.exit();
			reader.close();
		}
	}
}
