/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.aptvelocity;
import java.io.*;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
//import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.mozilla.javascript.*;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.declaration.TypeDeclaration;

/**
 * JavaScriptロール管理
 */
public class JavaScriptAp implements AnnotationProcessor {
	protected AnnotationProcessorEnvironment environment;
	private final Script script;

	public JavaScriptAp(Script script, AnnotationProcessorEnvironment env) {
		this.script = script;
		this.environment = env;
	}
	public AnnotationProcessorEnvironment getEnvironment() {
		return environment;
	}

	public void process() {
		for (TypeDeclaration classDecl : environment.getSpecifiedTypeDeclarations())  {
			try {
				processClass(classDecl);
			} catch (Exception e)  {
				e.printStackTrace();
			}
		}
	}

	protected void processClass(TypeDeclaration classDecl) throws Exception {
		Context cx = Context.enter();
		try {
			Scriptable scope = cx.initStandardObjects();
			script.exec(cx, scope);
			Function func =	(Function) scope.get("processClass", scope);
			if (func == null) {
				throw new RuntimeException("Not found processClass().");
			}
			func.call(cx, scope, func, new Object[]{classDecl, this});
		} finally {
			Context.exit();
		}
	}

	public void applyTemplate(VelocityContext context,
						String pkgName, String clsName, String templ) throws Exception {
		// Apply template
		TargetClassInfo info = new TargetClassInfo(pkgName, clsName, templ);
		Template template = Velocity.getTemplate(info.getTemplateName());
		Filer filer = environment.getFiler();
		PrintWriter writer = filer.createSourceFile(info.getName());
		template.merge(context, writer);
		writer.close();
	}

	public VelocityContext initVelocity() {
		//Velocity.setProperty("resource.loader","class");
		//Velocity.setProperty("class.resource.loader.class",
		//					ClasspathResourceLoader.class.getName());

		Velocity.init();
		VelocityContext context = new VelocityContext();
		return context;
	}


}
