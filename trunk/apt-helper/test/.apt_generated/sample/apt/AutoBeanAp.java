package sample.apt;

import org.apache.velocity.VelocityContext;
import java.lang.annotation.Annotation;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.declaration.TypeDeclaration;
import org.kotemaru.apthelper.*;
import org.kotemaru.apthelper.annotation.*;

public class AutoBeanAp extends ApBase
{
	public AutoBeanAp(AnnotationProcessorEnvironment env) {
		super(env);
	}

	protected boolean processClass(TypeDeclaration classDecl) throws Exception {
		Annotation anno = classDecl.getAnnotation(sample.annotation.AutoBean.class);
		if(anno == null) return false;

		ProcessorGenerate pgAnno =
			sample.annotation.AutoBean.class.getAnnotation(ProcessorGenerate.class);

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
			name = "AutoBean.vm";
		}
		String pkg = "sample.annotation";
		return pkg.replace('.', '/') +'/'+name;
	}


}
