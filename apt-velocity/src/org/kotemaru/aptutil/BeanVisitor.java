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
import java.util.*;

public class BeanVisitor extends SimpleDeclarationVisitor {
	private static final String TMPL_HEADER =
		 "// BeanVisitor generated.\n"
		+"package ${packageName};\n"
		+"public class ${className}\n"
		+" extends ${coreClassName}\n"
		+"{\n";

	private static final String TMPL_GETTER =
		"    public ${type} get${captalName}() {return this.${name};}\n";
	private static final String TMPL_SETTER =
		"    public void set${captalName}(${type} ${name}) {this.${name} = ${name};}\n";
	private static final String TMPL_ABSTRACT_METHOD =
		 "    ${modifiers} ${returnType} ${name}(${parameters}){\n"
		+"        throw new java.lang.UnsupportedOperationException(\"${name}\");\n"
		+"    }\n";

	private static final String TMPL_FOOTER =
		"}\n";

	private AutoBean generatorAnno;
	private PrintWriter writer;
	protected BeanVisitor(AutoBean anno, PrintWriter writer) {
		this.generatorAnno = anno;
		this.writer = writer;
	}

	public static void process(TypeDeclaration coreClassDecl,
				AnnotationProcessorEnvironment env) throws IOException {
		AutoBean anno = (AutoBean) coreClassDecl.getAnnotation(AutoBean.class);
		String fullClassName = anno.bean();
		if (fullClassName == null) return;

		String pkgName = coreClassDecl.getPackage().getQualifiedName();
		String className = fullClassName;
		int pos = fullClassName.lastIndexOf('.');
		if (pos > 0) {
			pkgName   = fullClassName.substring(0,pos);
			className = fullClassName.substring(pos+1);
		}

		//String interfaceName = anno.api();
		String coreClassName = coreClassDecl.getQualifiedName();


		Filer filer = env.getFiler();
		PrintWriter writer = filer.createSourceFile(pkgName+"."+className);
		BeanVisitor visitor = new BeanVisitor(anno, writer);

		Map map = new HashMap();
		map.put("coreClassName",coreClassName);
		//map.put("interfaceName",interfaceName);
		map.put("className",className);
		map.put("packageName",pkgName);

		visitor.header(map);

		DeclarationVisitor scanner =
			DeclarationVisitors.getSourceOrderDeclarationScanner(visitor, DeclarationVisitors.NO_OP);
		coreClassDecl.accept(scanner);

		visitor.footer(map);
		writer.close();
	}

	public void header(Map map) {
		writer.write(AptUtil.apply(TMPL_HEADER, map));
	}
	public void footer(Map map) {
		writer.write(AptUtil.apply(TMPL_FOOTER, map));
	}


	public void visitFieldDeclaration(FieldDeclaration d) {
		if (AptUtil.isPrivate(d)) return;
		if (generatorAnno.getter()) writer.write(AptUtil.apply(TMPL_GETTER, d));
		if (generatorAnno.setter()) writer.write(AptUtil.apply(TMPL_SETTER, d));
	}

	public void visitMethodDeclaration(MethodDeclaration d) {
		if (AptUtil.isPrivate(d)) return;
		if (!AptUtil.isAbstract(d)) return;
		writer.write(AptUtil.apply(TMPL_ABSTRACT_METHOD, d, Modifier.ABSTRACT));
	}

}
