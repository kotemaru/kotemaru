package test.master;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import org.kotemaru.apthelper.annotation.ProcessorGenerate;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@ProcessorGenerate(template="TestAnno.vm",suffix="Impl")
public @interface TestAnno {
	boolean setter() default true;
	boolean getter() default true;
}
