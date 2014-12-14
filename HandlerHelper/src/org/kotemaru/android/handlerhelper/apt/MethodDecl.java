package org.kotemaru.android.handlerhelper.apt;

import java.util.Collection;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.kotemaru.android.handlerhelper.annotation.Handling;

public class MethodDecl {
	private ExecutableElement elem;
	private Handling handling;
	//private HandlingClass handlingClass;

	public MethodDecl(TypeElement classDecl, ExecutableElement decl) {
		this.elem = decl;
		this.handling = decl.getAnnotation(Handling.class);
	}
	public ExecutableElement getElem() {
		return elem;
	}
	public String getName() {
		return elem.getSimpleName().toString();
	}
	public int getDelay() {
		return handling.delay();
	}
	
	public boolean isTask() {
		return handling != null;
	}
	public boolean isThread(String key) {
		if (handling == null) return false;
		Handling.Thread thread = handling.thread();
		return thread.toString().equals(key);
	}
	public boolean isException() {
		if (handling == null) return false;
		return handling.exception();
	}

	public String getParams() {
		ExecutableElement d = this.elem;
		Collection<? extends VariableElement> params = d.getParameters();
		if (params.size() == 0) return "";
		StringBuffer sbuf = new StringBuffer(params.size()*20);
		for (VariableElement param : params)  {
			sbuf.append("final ");
			sbuf.append(param.asType());
			sbuf.append(' ');
			sbuf.append(param.getSimpleName());
			sbuf.append(',');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}
	public String getArguments(boolean hasCamma) {
		String args = AptUtil.getArguments(elem);
		if (hasCamma && !args.isEmpty()) args = ", " + args;
		return args;
	}
	public String getArguments() {
		return getArguments(false);
	}

	
}

