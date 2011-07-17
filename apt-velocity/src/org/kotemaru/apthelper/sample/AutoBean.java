package org.kotemaru.apthelper.sample;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import org.kotemaru.apthelper.annotation.ProcessorGenerate;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@ProcessorGenerate(template="AutoBean.vm", suffix="Bean")
public @interface AutoBean {
	boolean setter() default true;
	boolean getter() default true;
}
