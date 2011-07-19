package sample.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import org.kotemaru.apthelper.annotation.ProcessorGenerate;

import sample.apt.AutoBeanHelper;

@ProcessorGenerate(
		template="SimpleAnno.vm", // Velocityのテンプレートファイル名。
		path=".",                 // 出力先パッケージへの相対パス。
		suffix="Impl"             // 出力クラス名に追加する文字列。
)

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface SimpleAnno {
	String name() default "default";
	String type() default "default";
	String value() default "default";
}
