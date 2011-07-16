package org.kotemaru.aptvelocity;

import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.declaration.TypeDeclaration;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.app.Velocity;

public abstract class ApBase implements AnnotationProcessor {
	protected AnnotationProcessorEnvironment environment;
	protected ApFactoryBase factory;

	public void init(ApFactoryBase fac, AnnotationProcessorEnvironment env) {
		this.factory = fac;
		this.environment = env;
	}

	public AnnotationProcessorEnvironment getEnvironment() {
		return environment;
	}
	public ApFactoryBase getFactory() {
		return factory;
	}


	public void process() {
		for (TypeDeclaration classDecl : environment.getTypeDeclarations())  {
			try {
				processClass(classDecl);
			} catch (Exception e)  {
				e.printStackTrace();
			}
		}
	}

	public abstract TargetClassInfo[] getTargetClassInfos(TypeDeclaration classDecl);
	public /*abstract*/ Map<String,Object> getProcessorContext(TypeDeclaration classDecl) {
		return null;
	}


	protected void processClass(TypeDeclaration classDecl) throws Exception {
		TargetClassInfo[] targetClassInfos = getTargetClassInfos(classDecl);
		if (targetClassInfos == null || targetClassInfos.length == 0) {
			return;
		}

		VelocityContext context = initVelocity();
		context.put("masterClassDecl", classDecl);
		context.put("annotation", getAnnotationMap(classDecl));
		context.put("processor", getProcessorContext(classDecl));
		context.put("util", new AptUtil());

		for (int i=0; i<targetClassInfos.length; i++) {
			TargetClassInfo info = targetClassInfos[i];
			context.put("targetClassInfo", info);

			// Apply template
			Template template = Velocity.getTemplate(getResourceName(info));
			Filer filer = environment.getFiler();
			PrintWriter writer = filer.createSourceFile(info.getName());
			template.merge(context, writer);
			writer.close();
		}
	}

	protected VelocityContext initVelocity() {
		Velocity.setProperty("resource.loader","class");
		Velocity.setProperty("class.resource.loader.class",
							ClasspathResourceLoader.class.getName());
		Velocity.init();
		VelocityContext context = new VelocityContext();
		return context;
	}
	protected Map<String,Object> getAnnotationMap(TypeDeclaration classDecl) {
		ApInfo[] infos = factory.getAnnotationInfos();
		Map<String,Object> map = new HashMap<String,Object>();
		for (int i=0; i<infos.length; i++) {
			Class<Annotation> cls = infos[i].getAnnotation();
			map.put(cls.getSimpleName(), classDecl.getAnnotation(cls));
		}
		return map;
	}

	protected String getResourceName(TargetClassInfo info) {
		String name = info.getTemplateName();
		if (name.startsWith("/")) return name;
		String pkg = this.getClass().getPackage().getName().replace('.', '/');
		return pkg +'/'+name;
	}
}