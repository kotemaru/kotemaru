package test.apt;

import java.io.PrintWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import java.lang.annotation.Annotation;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.declaration.TypeDeclaration;
import org.kotemaru.apthelper.AptUtil;

public class TestAnnoAp implements AnnotationProcessor
{
	protected AnnotationProcessorEnvironment environment;

	public TestAnnoAp(AnnotationProcessorEnvironment env) {
		this.environment = env;
	}

	@Override
	public void process() {
		for (TypeDeclaration classDecl : environment.getTypeDeclarations())  {
			try {
				processClass(classDecl);
			} catch (Exception e)  {
				e.printStackTrace();
			}
		}
	}

	protected void processClass(TypeDeclaration classDecl) throws Exception {
		Annotation anno = classDecl.getAnnotation(test.master.TestAnno.class);
		if(anno == null) return;

		VelocityContext context = initVelocity();
		context.put("masterClassDecl", classDecl);
		context.put("annotation", anno);
		context.put("util", new AptUtil());

		String pkgName = classDecl.getPackage().getQualifiedName();
		String baseName = classDecl.getSimpleName();

		applyTemplate(context,pkgName,baseName+"Impl",
				"TestAnno.vm");
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

		Template template = Velocity.getTemplate(templ);
		Filer filer = environment.getFiler();
		PrintWriter writer = filer.createSourceFile(pkgName+'.'+clsName);
		template.merge(context, writer);
		writer.close();
	}


}
