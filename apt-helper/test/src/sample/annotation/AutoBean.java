package sample.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import org.kotemaru.apthelper.annotation.ProcessorGenerate;

import sample.apt.AutoBeanHelper;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@ProcessorGenerate(
		template="AutoBean.vm",
		path="../autobean",
		suffix="Bean",helper=AutoBeanHelper.class,
		options={"aaa","bbb"}
)

public @interface AutoBean {
	boolean setter() default true;
	boolean getter() default true;
}
