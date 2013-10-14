package org.kotemaru.apthelper;

//import java.util.ArrayList;
//import java.util.Arrays;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.app.Velocity;
import org.kotemaru.util.IOUtil;

public abstract class ApBase extends AbstractProcessor  {
	protected ProcessingEnvironment environment;

	public ApBase() {
	}
	
	@Override
	public void init(ProcessingEnvironment env) {
		this.environment = env;
	}
	public ProcessingEnvironment getEnvironment() {
		return environment;
	}

	protected abstract boolean processClass(TypeElement classDecl) throws Exception;


	protected VelocityContext initVelocity() {
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

		Template template = Velocity.getTemplate(templ);
		Filer filer = environment.getFiler();
		JavaFileObject file = filer.createSourceFile(pkgName+'.'+clsName);
		PrintWriter writer = new PrintWriter(file.openWriter());
		template.merge(context, writer);
		writer.close();
	}
	
	public void applyTemplate(VelocityContext context,
			String pkgName, String clsName, Class baseTempl, String templ) throws Exception {
		context.put("packageName", pkgName);
		context.put("className", clsName);

		String template = IOUtil.getResource(baseTempl, templ);
		Filer filer = environment.getFiler();
		JavaFileObject file = filer.createSourceFile(pkgName+'.'+clsName);
		PrintWriter writer = new PrintWriter(file.openWriter());
		Velocity.evaluate(context, writer, templ, template);
		writer.close();
	}

	protected Object getHelper(TypeElement classDecl, Class helperCls) throws Exception {
		Constructor creator = helperCls.getConstructor(TypeElement.class);
		Object helper = creator.newInstance(classDecl);
		return helper;
	}

	protected String getPackageName(TypeElement classDecl, String path) {
		return AptUtil.getPackageName(classDecl, path);
	}

	protected void error(Throwable t){
		Messager messager = environment.getMessager();
		messager.printMessage(Kind.ERROR, t.toString());
		t.printStackTrace();
	}

}