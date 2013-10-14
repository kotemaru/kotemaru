package org.kotemaru.apthelper;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.app.Velocity;
import org.kotemaru.util.IOUtil;

public abstract class ClassProcessorBase extends AbstractProcessor {
	protected ProcessingEnvironment environment;

	public ClassProcessorBase() {
	}
	@Override
	public void init(ProcessingEnvironment env) {
		this.environment = env;
		Velocity.setProperty("runtime.log.logsystem.class","");
		Velocity.setProperty("resource.loader","class");
		Velocity.setProperty("class.resource.loader.class",
							ClasspathResourceLoader.class.getName());
		Velocity.init();
	}

	public ProcessingEnvironment getEnvironment() {
		return environment;
	}
	
	protected VelocityContext initVelocity() {
		VelocityContext context = new VelocityContext();
		return context;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {

		boolean res = false;
		for (TypeElement anno : annotations) {
			Set<? extends Element> classes = roundEnv.getElementsAnnotatedWith(anno);
			for (Element elem : classes) {
				try {
					if (elem instanceof TypeElement) {
						TypeElement classDecl = (TypeElement) elem;
						boolean isProcess = processClass(classDecl);
						res = res || isProcess;
					}
				} catch (Throwable t) {
					error(t);
				}
			}
		}
		return res;
	}
	protected abstract boolean processClass(TypeElement classDecl)
			throws Exception;


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
	
	public void applyTemplateText(VelocityContext context,
			String pkgName, String resName, String templ) throws Exception {
		context.put("packageName", pkgName);
		context.put("resourceName", resName);

		String resFullPath = pkgName.replace('.', '/')+"/"+resName;
		
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
		FileObject file = filer.createResource(StandardLocation.SOURCE_OUTPUT
				, "", resFullPath, (Element[])null);
		PrintWriter writer = new PrintWriter(file.openWriter());
		template.merge(context, writer);
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