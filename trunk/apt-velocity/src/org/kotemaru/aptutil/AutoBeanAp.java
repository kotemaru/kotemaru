package org.kotemaru.aptutil;

import java.util.Collection;
import java.io.IOException;
import java.io.PrintWriter;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.ConstructorDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.util.DeclarationVisitor;
import com.sun.mirror.util.DeclarationVisitors;
import com.sun.mirror.util.SimpleDeclarationVisitor;
import com.sun.mirror.type.TypeMirror;

import com.sun.mirror.util.*;
import com.sun.mirror.type.*;
import com.sun.mirror.declaration.*;

import org.kotemaru.aptutil.test.annotation.*;

public class AutoBeanAp implements AnnotationProcessor {
	private final AnnotationProcessorEnvironment env;

	AutoBeanAp(AnnotationProcessorEnvironment env)  {
		this.env = env;
	}

	public void process() {
		for (TypeDeclaration types : env.getSpecifiedTypeDeclarations())  {
			processType(types) ;
		}
	}

	private void processType (TypeDeclaration type)  {
		try {
			String className = type.getSimpleName();
			if (!className.endsWith("Core")) return;

			BeanVelocity.process(type, env);
		} catch (IOException e)  {
			e.printStackTrace();
		}
	}
}