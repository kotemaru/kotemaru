package org.kotemaru.apthelper;

import java.lang.reflect.Constructor;
import java.util.*;


import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;


public class MultiAp extends AbstractProcessor {
	private static final String APT_PATH = "../apt";
	private static final String AP_SUFFIX = "Ap";

	private Set<TypeElement> atds;
	private Set<ClassProcessor> cps = new HashSet<ClassProcessor>();
	private String packageName;
	private ProcessingEnvironment env;
	
	public MultiAp(Set<TypeElement> atds, 
			ProcessingEnvironment env,
			String packageName) {
		this.env = env;
		this.atds = atds;
		this.packageName = packageName;
		this.init();
	}
	private void init() {
		Velocity.setProperty("runtime.log.logsystem.class","");
		Velocity.setProperty("resource.loader","class");
		Velocity.setProperty("class.resource.loader.class",
							ClasspathResourceLoader.class.getName());
		Velocity.init();
		
		for (TypeElement annoDecl : atds) {
			String type = annoDecl.getQualifiedName().toString();
			if (type.startsWith(packageName)) {
				ClassProcessor cp = createProcessor(type);
				if (cp != null) {
					cps.add(cp);
				}
			}
		}
	}
	private ClassProcessor createProcessor(String type) {
		try {
			int idx = type.lastIndexOf('.');
			String pkg = type.substring(0, idx);
			String simpleName = type.substring(idx + 1);

			String apname = pkg.replaceFirst("[^.]*$", "apt") + "."
					+ simpleName + AP_SUFFIX;
			Class apCls = Class.forName(apname);
			Constructor constractor = apCls
					.getConstructor(ProcessingEnvironment.class);
			return (ClassProcessor) constractor.newInstance(env);
		} catch (Exception e) {
			return null;
		}
	}	

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		boolean res = false;
		for (TypeElement classDecl : annotations)  {
			for (ClassProcessor cp : cps) {
				try {
					res = res || cp.processClass(classDecl);
				} catch (Throwable t) {
					error(t);
				}
			}
		}
		return res;
	}
	
	protected void error(Throwable t){
		Messager messager = env.getMessager();
		messager.printMessage(Kind.ERROR, t.toString());
		t.printStackTrace();
	}
}
