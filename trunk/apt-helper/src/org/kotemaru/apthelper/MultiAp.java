package org.kotemaru.apthelper;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.*;


import org.apache.velocity.VelocityContext;
import org.kotemaru.apthelper.annotation.ProcessorGenerate;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.apt.Messager;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

public class MultiAp implements AnnotationProcessor {
	private static final String APT_PATH = "../apt";
	private static final String AP_SUFFIX = "Ap";

	private Set<AnnotationTypeDeclaration> atds;
	private Set<ClassProcessor> cps = new HashSet<ClassProcessor>();
	private String packageName;
	private AnnotationProcessorEnvironment env;
	
	public MultiAp(Set<AnnotationTypeDeclaration> atds, 
			AnnotationProcessorEnvironment env,
			String packageName) {
		this.env = env;
		this.atds = atds;
		this.packageName = packageName;
		this.init();
	}
	private void init() {
		for (TypeDeclaration annoDecl : atds) {
			String type = annoDecl.getQualifiedName();
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
					.getConstructor(AnnotationProcessorEnvironment.class);
			return (ClassProcessor) constractor.newInstance(env);
		} catch (Exception e) {
			return null;
		}
	}	

	@Override
	public void process() {
		List<TypeDeclaration> list = new ArrayList<TypeDeclaration>();
		for (TypeDeclaration classDecl : env.getTypeDeclarations())  {
			for (ClassProcessor cp : cps) {
				try {
					cp.processClass(classDecl);
				} catch (Throwable t) {
					error(t);
				}
			}
		}
	}
	protected void error(Throwable t){
		Messager messager = env.getMessager();
		messager.printError(t.toString());
		t.printStackTrace();
	}


}
