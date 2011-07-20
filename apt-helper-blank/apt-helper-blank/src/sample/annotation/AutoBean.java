package sample.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import org.kotemaru.apthelper.annotation.ProcessorGenerate;

import sample.apt.AutoBeanHelper;

@ProcessorGenerate(
	template="AutoBean.vm", // Velocityのテンプレートファイル名。
	path="../autobean",  // 出力先パッケージへの相対パス。
	suffix="Bean",       // 出力クラス名に追加する文字列。
	helper=AutoBeanHelper.class  // Velocityに $helper で渡されるクラス
)

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface AutoBean {
	boolean setter() default true;
	boolean getter() default true;
}
