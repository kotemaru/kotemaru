package org.kotemaru.apthelper;

//import java.util.ArrayList;
//import java.util.Arrays;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.Properties;

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

public abstract class ClassProcessorBase implements ClassProcessor {
	protected AnnotationProcessorEnvironment environment;

	public ClassProcessorBase(AnnotationProcessorEnvironment env) {
		this.environment = env;
	}

	public AnnotationProcessorEnvironment getEnvironment() {
		return environment;
	}


	protected VelocityContext initVelocity() {

		Velocity.setProperty("runtime.log.logsystem.class","");
		Velocity.setProperty("resource.loader","class");
		Velocity.setProperty("class.resource.loader.class",
							ClasspathResourceLoader.class.getName());
		Velocity.init();

		VelocityContext context = new VelocityContext();
		return context;
	}


	public void applyTemplate(VelocityContext context,
			String pkgName, String clsName, String templ) throws Exception {
		context.put("packageName", pkgName);
		context.put("className", clsName);

		Template template;
		try {
			template = Velocity.getTemplate("/"+templ);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
				"Template "+templ+" not found. "
				+e.toString());
		}
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