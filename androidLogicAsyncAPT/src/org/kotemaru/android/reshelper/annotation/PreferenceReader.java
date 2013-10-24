package org.kotemaru.android.reshelper.annotation;

import java.lang.annotation.Retention;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import org.kotemaru.apthelper.annotation.ProcessorGenerate;
import org.kotemaru.android.reshelper.apt.ResourceReaderHelper;

@ProcessorGenerate(
		template="PreferenceReader.vm",  // Velocityのテンプレートファイル名。
		path=".",                   // 出力先パッケージへの相対パス。
		suffix="PreferenceReader",       // 出力クラス名に追加する文字列。
		helper=ResourceReaderHelper.class,  // Velocityに $helper で渡されるクラス
		options={ResourceReaderHelper.RESOURCE_PATH}   // APTのオプション
)

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface PreferenceReader {
	String xml();
}
