package org.kotemaru.apthelper;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;


import org.apache.velocity.VelocityContext;
import org.kotemaru.apthelper.annotation.ProcessorGenerate;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.apt.Messager;
import com.sun.mirror.declaration.TypeDeclaration;

public class AptHelperProcessor extends ApBase {
	private static final String APT_PATH = "../apt";
	private static final String AP_SUFFIX = "Ap";
	private static final String FACTORY_NAME = "ApFactory";
	private static final String PROCESSOR_VM = "processor.vm";
	private static final String FACTORY_VM = "factory.vm";
	private static final String SERVICE_FILE =
		"META-INF/services/com.sun.mirror.apt.AnnotationProcessorFactory";


	public AptHelperProcessor(AnnotationProcessorEnvironment env) {
		super(env);
	}


	@Override
	public void process() {
		List<TypeDeclaration> list = new ArrayList<TypeDeclaration>();
		for (TypeDeclaration classDecl : environment.getTypeDeclarations())  {
			try {
				boolean isProcess = processClass(classDecl);
				if (isProcess) list.add(classDecl);
			} catch (Throwable t)  {
				error(t);
			}
		}

		try {
			generateFactory(list);
			generateService(list);
		} catch (Throwable t)  {
			error(t);
		}

	}
	protected boolean processClass(TypeDeclaration classDecl) throws Exception {
		ProcessorGenerate pgAnno = classDecl.getAnnotation(ProcessorGenerate.class);
		if(pgAnno == null) return false;

		VelocityContext context = initVelocity();
		context.put("masterClassDecl", classDecl);
		context.put("annotation", pgAnno);
		context.put("helper", new AptUtil(classDecl));

		String pkgName = getPackageName(classDecl, APT_PATH);
		String clsName = classDecl.getSimpleName() + AP_SUFFIX;
		String templ = getResourceName(PROCESSOR_VM);

		applyTemplate(context, pkgName, clsName, templ);
		//applyTemplate(context, pkgName, clsName, this.getClass(), PROCESSOR_VM);
		return true;
	}

	protected boolean generateFactory(List<TypeDeclaration> list) throws Exception {
		if(list.size() == 0) return false;
		TypeDeclaration classDecl = list.get(0);

		VelocityContext context = initVelocity();
		context.put("masterClassDecls", list);
		context.put("annotationPackageName",
			classDecl.getPackage().getQualifiedName());

		String pkgName = AptUtil.getPackageName(classDecl, APT_PATH);
		String templ = getResourceName(FACTORY_VM);

		applyTemplate(context,pkgName, FACTORY_NAME, templ);
		//applyTemplate(context, pkgName, FACTORY_NAME, this.getClass(), FACTORY_VM);
		return true;
	}
	
	protected boolean generateService(List<TypeDeclaration> list) throws Exception {
		if(list.size() == 0) return false;
		TypeDeclaration classDecl = list.get(0);

		String pkgName = AptUtil.getPackageName(classDecl, APT_PATH);
		String factoryName = pkgName+"."+FACTORY_NAME;

		Filer filer = environment.getFiler();
		PrintWriter writer = filer.createTextFile(
			Filer.Location.SOURCE_TREE, // eclipseのバグ？apt_genからMETA-INFがclassesにコピーされない。
			//Filer.Location.CLASS_TREE,
			"", 
			new File(SERVICE_FILE),
			"utf-8"
		);
		writer.write(factoryName);
		writer.close();
		return true;
	}

	protected String getResourceName(String name) {
		if (name.startsWith("/")) return name;
		String pkg = this.getClass().getPackage().getName().replace('.', '/');
		return pkg +'/'+name;
	}

}
