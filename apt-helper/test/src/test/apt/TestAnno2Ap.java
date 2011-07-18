package test.apt;

import java.io.PrintWriter;
import java.util.*;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import java.lang.annotation.Annotation;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.declaration.TypeDeclaration;
import org.kotemaru.apthelper.*;
import org.kotemaru.apthelper.annotation.*;

public class TestAnno2Ap extends ApBase
{
	public TestAnno2Ap(AnnotationProcessorEnvironment env) {
		super(env);
	}

	protected boolean processClass(TypeDeclaration classDecl) throws Exception {
		Annotation anno = classDecl.getAnnotation(test.master.TestAnno2.class);
		if(anno == null) return false;

		ProcessorGenerate pgAnno =
			test.master.TestAnno2.class.getAnnotation(ProcessorGenerate.class);

		VelocityContext context = initVelocity();
		context.put("masterClassDecl", classDecl);
		context.put("annotation", anno);

		Object helper   = getHelper(classDecl, pgAnno.helper());
		context.put("helper", helper);

		String pkgName = getPackageName(classDecl, pgAnno.path());
		String clsName = classDecl.getSimpleName() + pgAnno.suffix();
		String templ = getResourceName(pgAnno.template());

		applyTemplate(context, pkgName, clsName, templ);
		return true;
	}

	private String getResourceName(String name) {
		if (name.startsWith("/")) return name;
		if (name.length() == 0) {
			name = "TestAnno2.vm";
		}
		String pkg = "test.master";
		return pkg.replace('.', '/') +'/'+name;
	}


}
