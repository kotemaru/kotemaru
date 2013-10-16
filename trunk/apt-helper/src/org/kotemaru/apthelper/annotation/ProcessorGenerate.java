package org.kotemaru.apthelper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import org.kotemaru.apthelper.AptUtil;


/**
 * Velocity注釈処理(AnnotationProcessor)クラスを自動生成する注釈。
 * <br>- 注釈クラスにのみ指定可能。
 *  
 * @author kotemaru.org
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProcessorGenerate {
	/**
	 * Velocityテンプレート名（リソースパス）。
	 * <br>- クラスパスからリソースとして検索する。
	 * <br>- デフォルトは "注釈クラス名.vm"。パッケージは注釈クラスに同じ。
	 * <br>- "/"から始めれば絶対パスで指定となる。
	 */
	String template() default "";
	
	/**
	 * 注釈処理クラスを生成するパッケージ名の相対パス。
	 * <br>- 注釈クラスのパッケージからの相対パス。
	 * <br>- デフォルトは注釈クラスのパッケージ。
	 */
	String path() default ".";
	
	/**
	 * 注釈処理クラスのクラス名のサフィックス。
	 * <br>- 注釈クラス名にこのサフィックスを追加したクラス名となる。
	 */
	String suffix() default "Ap";

	/**
	 * Velocityの初期変数 $helper として渡されるユーザ定義クラス。
	 * <br>- デフォルトは {@link org.kotemaru.apthelper.AptUtil}
	 */
	Class helper() default AptUtil.class;

	/**
	 * Javaソース以外を出力する注釈処理の場合は true に設定。
	 */
	boolean isResource() default false;

	/**
	 * 注釈処理の解釈可能なオプション。
	 * <br>- javac の -A オプションで受け取れるオプション。
	 */
	String[] options() default {};
}
