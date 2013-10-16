package org.kotemaru.apthelper;

import java.io.PrintWriter;
import java.util.Set;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.kotemaru.apthelper.annotation.ProcessorGenerate;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.lang.model.SourceVersion;


/**
 * 注釈 ProcessorGenerate の処理クラス。
 * <br>- processor.vm に ProcessorGenerate の設定を適用して注釈処理クラスのソースを生成する。
 * <br>- 生成した注釈処理クラスの名前を META-INF/services/javax.annotation.processing.Processor に一覧出力する。
 * @author kotemaru.org
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("org.kotemaru.apthelper.annotation.ProcessorGenerate")
public class AptHelperProcessor extends ApBase {
	private static final String APT_PATH = "../apt";
	private static final String AP_SUFFIX = "Ap";
	private static final String PROCESSOR_VM = "processor.vm";
	private static final String SERVICE_FILE = "META-INF/services/javax.annotation.processing.Processor";

	public AptHelperProcessor() {
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		List<TypeElement> list = processClasses(annotations, roundEnv);
		try {
			generateService(list);
		} catch (Throwable t) {
			error(t);
		}
		return true;
	}

	/**
	 * processor.vm に ProcessorGenerate の設定を適用して注釈処理クラスのソースを生成する。
	 * <br>- 注釈処理クラス名は注釈クラス名+"Ap"
	 * <br>- パッケージは注釈クラスから相対で "../apt" となる。
	 */
	@Override
	protected boolean processClass(TypeElement classDecl) throws Exception {
		ProcessorGenerate pgAnno = classDecl.getAnnotation(ProcessorGenerate.class);
		if (pgAnno == null) return false;

		VelocityContext context = new VelocityContext();
		context.put("masterClassDecl", classDecl);
		context.put("annotation", pgAnno);
		context.put("helper", new AptUtil(classDecl));

		String pkgName = AptUtil.getPackageName(classDecl, APT_PATH);
		String clsName = classDecl.getSimpleName() + AP_SUFFIX;
		String templ = getResourceName(PROCESSOR_VM);

		applyTemplate(context, pkgName, clsName, templ);
		return true;
	}

	/**
	 * 生成した注釈処理クラスの一覧生成。
	 * <br>- 出力先は META-INF/services/javax.annotation.processing.Processor
	 * <br>- javac はこれを見て注釈処理クラスを判定する。
	 * @param list
	 * @return
	 * @throws Exception
	 */
	protected boolean generateService(List<TypeElement> list) throws Exception {
		if (list.size() == 0) return false;

		Filer filer = environment.getFiler();
		FileObject file = filer.createResource(StandardLocation.SOURCE_OUTPUT,
				"", SERVICE_FILE, (Element[]) null);
		PrintWriter writer = new PrintWriter(file.openWriter());

		for (TypeElement classDecl : list) {
			String pkgName = AptUtil.getPackageName(classDecl, APT_PATH);
			String clsName = classDecl.getSimpleName() + AP_SUFFIX;
			writer.println(pkgName+"."+clsName);
		}
		
		writer.close();
		return true;
	}

	protected String getResourceName(String name) {
		if (name.startsWith("/")) return name;
		String pkg = this.getClass().getPackage().getName().replace('.', '/');
		return pkg + '/' + name;
	}

}
