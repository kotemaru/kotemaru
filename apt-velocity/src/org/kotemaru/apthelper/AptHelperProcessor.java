package org.kotemaru.apthelper;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.kotemaru.apthelper.annotation.ProcessorGenerate;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.declaration.TypeDeclaration;

public class AptHelperProcessor implements AnnotationProcessor {
	protected AnnotationProcessorEnvironment environment;

	public AptHelperProcessor(AnnotationProcessorEnvironment env) {
		this.environment = env;
	}


	@Override
	public void process() {
		List<TypeDeclaration> list = new ArrayList<TypeDeclaration>();
		for (TypeDeclaration classDecl : environment.getTypeDeclarations())  {
			try {
				boolean isProcess = processClass(classDecl);
				if (isProcess) list.add(classDecl); 
			} catch (Exception e)  {
				e.printStackTrace();
			}
		}

		try {
			generateFactory(list);
		} catch (Exception e)  {
			e.printStackTrace();
		}
		
	}
	protected boolean processClass(TypeDeclaration classDecl) throws Exception {
		ProcessorGenerate pgAnno = classDecl.getAnnotation(ProcessorGenerate.class);
		if(pgAnno == null) return false;

		VelocityContext context = initVelocity();
		context.put("masterClassDecl", classDecl);
		context.put("annotation", pgAnno);
		//context.put("util", new AptUtil());

		String pkgName = applyPkgPath(classDecl.getPackage().getQualifiedName(), "../apt");
		String baseName = classDecl.getSimpleName();

		applyTemplate(context,pkgName,baseName+"Ap", "processor.vm");
		//applyTemplate(context,pkgName,baseName+"ApFactory", "factory.vm");
		
		return true;
	}
	protected boolean generateFactory(List<TypeDeclaration> list) throws Exception {
		if(list.size() == 0) return false;
		TypeDeclaration classDecl = list.get(0);
		
		VelocityContext context = initVelocity();
		context.put("masterClassDecls", list);
		String pkgName = classDecl.getPackage().getQualifiedName().replaceFirst("[^.]*$", "apt");
		String baseName = classDecl.getSimpleName();

		applyTemplate(context,pkgName,"ApFactory", "factory.vm");
		return true;
	}

	protected VelocityContext initVelocity() {
		Velocity.setProperty("resource.loader","class");
		Velocity.setProperty("class.resource.loader.class",
							ClasspathResourceLoader.class.getName());
		Velocity.init();
		VelocityContext context = new VelocityContext();
		return context;
	}

	protected String getResourceName(String name) {
		if (name.startsWith("/")) return name;
		String pkg = this.getClass().getPackage().getName().replace('.', '/');
		return pkg +'/'+name;
	}
	public void applyTemplate(VelocityContext context,
			String pkgName, String clsName, String templ) throws Exception {
		context.put("packageName", pkgName);
		context.put("className", clsName);

		Template template = Velocity.getTemplate(getResourceName(templ));
		Filer filer = environment.getFiler();
		PrintWriter writer = filer.createSourceFile(pkgName+'.'+clsName);
		template.merge(context, writer);
		writer.close();
	}
	protected String applyPkgPath(String orgPkg, String path) {
		if (path.equals(".")) return orgPkg;
		if (path.startsWith("/")) {
			return path.replace('/', '.').substring(1);
		}

		path = orgPkg.replace('.', '/') +"/"+ path;
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(path.split("/")));
		for (int i=0; i<list.size();) {
			String cur = list.get(i);
			if (cur.equals(".")) {
				list.remove(i);
			} else if (cur.equals("..")) {
				i--;
				list.remove(i);
				list.remove(i);
			} else {
				i++;
			}
		}

		StringBuffer sbuf = new StringBuffer();
		for (int i=0; i<list.size(); i++) {
			sbuf.append(".");
			sbuf.append(list.get(i));
		}
		return sbuf.toString().substring(1);
	}

}
