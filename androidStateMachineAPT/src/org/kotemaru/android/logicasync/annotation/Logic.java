package org.kotemaru.android.logicasync.annotation;

import java.lang.annotation.Retention;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import org.kotemaru.apthelper.annotation.ProcessorGenerate;
import org.kotemaru.android.logicasync.apt.LogicHelper;

@ProcessorGenerate(
		template="LogicAsync.vm",  // Velocityのテンプレートファイル名。
		path=".",                   // 出力先パッケージへの相対パス。
		suffix="Async",       // 出力クラス名に追加する文字列。
		helper=LogicHelper.class  // Velocityに $helper で渡されるクラス
)

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Logic {
}
