package org.kotemaru.android.handlerhelper.apt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * Velocity を利用してソースを生成する注釈処理の基底クラス。
 * <br>- 基本的にクラスの注釈を処理する。
 * <br>- メソッド/フィールド単位の注釈はクラスの処理内で行う。
 * <br>- 継承するクラスは以下の注釈を必要とする。
 * <br>- - @SupportedSourceVersion(SourceVersion.RELEASE_6)
 * <br>- - @SupportedAnnotationTypes("処理する注釈クラス名（フル）")
 * @author kotemaru.org
 */
public abstract class ApBase extends AbstractProcessor  {
	protected ProcessingEnvironment environment;

	public ApBase() {
	}
	
	/**
	 * 初期化処理。
	 * <br>- 環境を貰い、Velocityを初期化する。
	 * <br>- 注意事項：Eclipse の注釈処理のバグで jar を差し替えるとVelocityが jar 内の
	 *   リソースを参照出来なくなる。=> Eclipse再起動が必要。
	 */
	@Override
	public void init(ProcessingEnvironment env) {
		super.init(env);
		this.environment = env;
		Velocity.setProperty("runtime.log.logsystem.class","");
 		Velocity.setProperty("resource.loader","class");
		Velocity.setProperty("class.resource.loader.class",
							ClasspathResourceLoader.class.getName());
		Velocity.init();
	}
	
	
	/**
	 * 注釈処理。
	 * <br>- 戻り値が良く解って無いが生成ソースに対してさらに注釈処理が必要な場合、true を返すっぽい。
	 * <br>- とりあえず、処理りた注釈があれば true を返して置く。
	 * <br>- - ２周目は annotations.isEmpty() になるので。
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		//System.out.println("-------------->"+annotations.toString());
		if (annotations.isEmpty()) return true;
		List<TypeElement> list = processClasses(annotations, roundEnv);
		return list.isEmpty();
		//return false;
	}
	
	/**
	 * 注釈処理。
	 * <br>- 引数で貰ったクラスをすべて処理して見ようとする。
	 * @param annotations
	 * @param roundEnv
	 * @return 処理できたクラスのリスト。
	 */
	protected List<TypeElement> processClasses(
			Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv
	) {
		log(annotations.toString());

		List<TypeElement> list = new ArrayList<TypeElement>();
		for (TypeElement anno : annotations) {
			Set<? extends Element> classes = roundEnv.getElementsAnnotatedWith(anno);
			for (Element elem : classes) {
				try {
					if (elem instanceof TypeElement) {
						TypeElement classDecl = (TypeElement) elem;
						boolean isProcess = processClass(classDecl);
						if (isProcess) {
							list.add(classDecl);
						}
					}
				} catch (Throwable t) {
					error(t);
				}
			}
		}
		return list;
	}
	
	/**
	 * クラス１個の注釈処理。
	 * @param classDecl 対象クラス
	 * @return true=処理した。
	 * @throws Exception
	 */
	protected abstract boolean processClass(TypeElement classDecl) throws Exception;

	
	/**
	 * Velocity の実行（Javaソース用）。出力先はパッケージ名とクラス名から自動生成。
	 * @param context VelocityContext
	 * @param pkgName パッケージ名
	 * @param clsName クラス名
	 * @param templ  Velocityテンプレート名。
	 * @throws Exception
	 */
	protected void applyTemplate(VelocityContext context,
			String pkgName, String clsName, String templ) throws Exception {
		context.put("packageName", pkgName);
		context.put("className", clsName);
		context.put("environment", environment);

		Template template = getVelocityTemplate(templ);
		Filer filer = environment.getFiler();
		JavaFileObject file = filer.createSourceFile(pkgName+'.'+clsName);
		PrintWriter writer = new PrintWriter(file.openWriter());
		template.merge(context, writer);
		writer.close();
	}
	
	/**
	 * Velocity の実行(リソース用）。出力先はパッケージ名とリソース名から自動生成。
	 * @param context VelocityContext
	 * @param pkgName パッケージ名
	 * @param resName リソース名
	 * @param templ  Velocityテンプレート名。
	 * @throws Exception
	 */
	public void applyTemplateText(VelocityContext context,
			String pkgName, String resName, String templ) throws Exception {
		context.put("packageName", pkgName);
		context.put("resourceName", resName);
		context.put("environment", environment);

		Template template = getVelocityTemplate(templ);

		String resFullPath = pkgName.replace('.', '/')+"/"+resName;
		Filer filer = environment.getFiler();
		FileObject file = filer.createResource(StandardLocation.SOURCE_OUTPUT
				, "", resFullPath, (Element[])null);
		PrintWriter writer = new PrintWriter(file.openWriter());
		template.merge(context, writer);
		writer.close();
	}
	
	/**
	 * Velocityテンプレート取得。
	 * @param templ テンプレート名。（リソースのパス名）
	 * @return
	 */
	protected Template getVelocityTemplate(String templ) {
		try {
			return Velocity.getTemplate("/"+templ);
		} catch (Exception e) {
			// - 注意事項：Eclipse の注釈処理のバグで jar を差し替えるとVelocityが jar 内の
			//   リソースを参照出来なくなる。=> Eclipse再起動が必要。
			// ここに来る。
			error(e);
			throw new RuntimeException(
				"Template "+templ+" not found. "
				+e.toString());
		}
	}
		
	/**
	 * ヘルパークラス取得。
	 * <br>- 以下の３パターンのコンストラクタを試して最初に見つかったもの。
	 * <br>- - Constructor(TypeElement, ProcessingEnvironment)
	 * <br>- - Constructor(TypeElement)
	 * <br>- - Constructor()
	 * @param classDecl 注釈の定義されたクラス情報。
	 * @param helperCls ヘルパークラス。
	 * @return ヘルパークラスのインスタンス。
	 * @throws Exception
	 */
	protected Object getHelper(TypeElement classDecl, Class<?> helperCls) throws Exception {
		try {
			Constructor<?> creator = helperCls.getConstructor(TypeElement.class, ProcessingEnvironment.class);
			return creator.newInstance(classDecl, environment);
		} catch (NoSuchMethodException e) {}
		
		try {
			Constructor<?> creator = helperCls.getConstructor(TypeElement.class);
			return creator.newInstance(classDecl);
		} catch (NoSuchMethodException e) {}
		
		Constructor<?> creator = helperCls.getConstructor();
		return creator.newInstance();
	}

	/**
	 * エラーログ。スタックのフルダンプ。
	 * @param t 発生した例外
	 */
	protected void error(Throwable t){
		Messager messager = environment.getMessager();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		messager.printMessage(Kind.ERROR, sw.toString());
	}
	
	/**
	 * ただのログ。
	 * @param msg
	 */
	protected void log(String msg){
		Messager messager = environment.getMessager();
		messager.printMessage(Kind.OTHER, msg);
	}
	
}