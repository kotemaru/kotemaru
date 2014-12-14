package org.kotemaru.android.handlerhelper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@Documented
public @interface Handling {
	public enum Thread {
		WORKER,
		UI
	};
	Thread thread() default Thread.WORKER;
	boolean exception() default false;
	int delay() default 0;
}
