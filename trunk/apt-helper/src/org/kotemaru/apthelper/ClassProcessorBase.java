package org.kotemaru.apthelper;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import org.apache.velocity.VelocityContext;
import org.kotemaru.apthelper.annotation.ProcessorGenerate;


/**
 * processor.vm で生成される注釈処理クラスの基底クラス。
 * 
 * @author kotemaru.org
 */
public abstract class ClassProcessorBase extends ApBase {
	protected ProcessingEnvironment environment;

	public ClassProcessorBase() {
	}
	
	protected boolean processClass(TypeElement classDecl, Class annoClass) throws Exception {
		Annotation anno = classDecl.getAnnotation(annoClass);
		if(anno == null) return false;

		ProcessorGenerate pgAnno = 
				(ProcessorGenerate) annoClass.getAnnotation(ProcessorGenerate.class);

		VelocityContext context = new VelocityContext();
		context.put("masterClassDecl", classDecl);
		context.put("annotation", anno);
		context.put("environment", environment);

		Object helper   = getHelper(classDecl, pgAnno.helper());
		context.put("helper", helper);

		String pkgName = AptUtil.getPackageName(classDecl, pgAnno.path());
		String clsName = classDecl.getSimpleName() + pgAnno.suffix();
		String templ = getResourceName(pgAnno, annoClass);

		if (pgAnno.isResource()) {
			applyTemplateText(context, pkgName, clsName, templ);
		} else {
			applyTemplate(context, pkgName, clsName, templ);
		}
		return true;
	}

	/**
	 * Velocityテンプレート名(リソース名)取得。
	 * <br>- ProcessorGenerate.template の定義。
	 * <br>- "/"で始まる場合はフルパスとしてまま返す。
	 * <br>- 未定義の場合は、注釈クラス名に拡張子".vm"を付けたもの。
	 * <br>- パッケージは注釈クラスに同じ。
	 * @param pgAnno
	 * @param annoClass
	 * @return
	 */
	private String getResourceName(ProcessorGenerate pgAnno, Class annoClass) {
		String name = pgAnno.template();
		if (name.startsWith("/")) return name;
		if (name.length() == 0) {
			name = annoClass.getSimpleName()+".vm";
		}
		String pkg = annoClass.getPackage().getName();
		return pkg.replace('.', '/') +'/'+name;
	}

}