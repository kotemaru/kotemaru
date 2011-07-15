package org.kotemaru.aptutil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.kotemaru.aptutil.test.annotation.AutoBean;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.util.DeclarationVisitor;
import com.sun.mirror.util.DeclarationVisitors;

public class BeanVelocity {

	public static void process(TypeDeclaration coreClassDecl,
			AnnotationProcessorEnvironment env) throws IOException {

		AutoBean anno = (AutoBean) coreClassDecl.getAnnotation(AutoBean.class);
		String fullClassName = anno.bean();
		if (fullClassName == null)
			return;

		String pkgName = coreClassDecl.getPackage().getQualifiedName();
		String className = fullClassName;
		int pos = fullClassName.lastIndexOf('.');
		if (pos > 0) {
			pkgName = fullClassName.substring(0, pos);
			className = fullClassName.substring(pos + 1);
		}

		// String interfaceName = anno.api();
		String coreClassName = coreClassDecl.getQualifiedName();

		Filer filer = env.getFiler();
		PrintWriter writer = filer.createSourceFile(pkgName + "." + className);


        Velocity.init();
        VelocityContext context = new VelocityContext();
		context.put("coreClassName", coreClassName);
		// map.put("interfaceName",interfaceName);
		context.put("className", className);
		context.put("packageName", pkgName);
		context.put("coreClassDecl", coreClassDecl);

		//Collection<FieldDeclaration> fields = coreClassDecl.getFields();

        Template template = Velocity.getTemplate("autoBean.vm");

//        BufferedWriter writer = writer = new BufferedWriter(
//            new OutputStreamWriter(System.out));

        template.merge(context, writer);
        writer.flush();
        writer.close();

	}

}
