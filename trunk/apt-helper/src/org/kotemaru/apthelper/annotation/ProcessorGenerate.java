package org.kotemaru.apthelper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import org.kotemaru.apthelper.AptUtil;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProcessorGenerate {
	String template();
	String path() default ".";
	String suffix() ;
	Class helper() default AptUtil.class;
	boolean isResource() default false;
	String[] options() default {};
}
