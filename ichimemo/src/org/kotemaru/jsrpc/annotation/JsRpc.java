package org.kotemaru.jsrpc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import org.kotemaru.apthelper.annotation.ProcessorGenerate;

import org.kotemaru.jsrpc.apt.JavaScriptStubHelper;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@ProcessorGenerate(template="JavaScriptStub.vm",suffix=".js",
		isResource=true,helper=JavaScriptStubHelper.class)

public @interface JsRpc {
}
