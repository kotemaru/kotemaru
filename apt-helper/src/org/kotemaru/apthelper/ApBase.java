package org.kotemaru.apthelper;

//import java.util.ArrayList;
//import java.util.Arrays;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.apt.Messager;
import com.sun.mirror.declaration.TypeDeclaration;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.app.Velocity;
import org.kotemaru.util.IOUtil;

public abstract class ApBase implements AnnotationProcessor {
	protected AnnotationProcessorEnvironment environment;

	public ApBase(AnnotationProcessorEnvironment env) {
		this.environment = env;
	}

	public AnnotationProcessorEnvironment getEnvironment() {
		return environment;
	}

	@Override
	public void process() {
		for (TypeDeclaration classDecl : environment.getTypeDeclarations())  {
			try {
				processClass(classDecl);
			} catch (Throwable t)  {
				error(t);
			}
		}
	}

	protected abstract boolean processClass(TypeDeclaration classDecl) throws Exception;


	protected VelocityContext initVelocity() {
/*
 		Velocity.setProperty("resource.loader","class");
		Velocity.setProperty("class.resource.loader.class",
							ClasspathResourceLoader.class.getName());
		Velocity.init();
*/
		VelocityContext context = new VelocityContext();
		return context;
	}


	public void applyTemplate(VelocityContext context,
			String pkgName, String clsName, String templ) throws Exception {
		context.put("packageName", pkgName);
		context.put("className", clsName);

		Template template = Velocity.getTemplate(templ);
		Filer filer = environment.getFiler();
		PrintWriter writer = filer.createSourceFile(pkgName+'.'+clsName);
		template.merge(context, writer);
		writer.close();
	}
	
	public void applyTemplate(VelocityContext context,
			String pkgName, String clsName, Class baseTempl, String templ) throws Exception {
		context.put("packageName", pkgName);
		context.put("className", clsName);

		String template = IOUtil.getResource(baseTempl, templ);
		Filer filer = environment.getFiler();
		PrintWriter writer = filer.createSourceFile(pkgName+'.'+clsName);
		Velocity.evaluate(context, writer, templ, template);
		writer.close();
	}

	protected Object getHelper(TypeDeclaration classDecl, Class helperCls) throws Exception {
		Constructor creator = helperCls.getConstructor(TypeDeclaration.class);
		Object helper = creator.newInstance(classDecl);
		return helper;
	}

	protected String getPackageName(TypeDeclaration classDecl, String path) {
		return AptUtil.getPackageName(classDecl, path);
	}

	protected void error(Throwable t){
		Messager messager = environment.getMessager();
		messager.printError(t.toString());
		t.printStackTrace();
	}

}