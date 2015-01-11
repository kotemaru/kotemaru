package org.kotemaru.android.handlerhelper.apt;

import java.util.Collection;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.kotemaru.android.handlerhelper.annotation.Handling;
import org.kotemaru.android.handlerhelper.rt.ThreadManager;

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
	public int getRetry() {
		return handling.retry();
	}
	public int getInterval() {
		return handling.interval();
	}
	public float getIntervalRate() {
		return handling.intervalRate();
	}
	
	public boolean isTask() {
		return handling != null;
	}
	public boolean isThread(String key) {
		if (handling == null) return false;
		String thread = handling.thread();
		return thread.toString().equals(key);
	}
	public String getThreadName() {
		if (handling == null) return "";
		String thread = handling.thread();
		if (ThreadManager.UI.equals(thread)) return "ThreadManager.UI";
		if (ThreadManager.WORKER.equals(thread)) return "ThreadManager.WORKER";
		if (ThreadManager.NETWORK.equals(thread)) return "ThreadManager.NETWORK";
		return "\""+thread+"\"";
	}

	public String getParams() {
		ExecutableElement d = this.elem;
		Collection<? extends VariableElement> params = d.getParameters();
		if (params.size() == 0) return "";
		StringBuffer sbuf = new StringBuffer(params.size()*20);
		int n = 0;
		for (VariableElement param : params)  {
			String type = param.asType().toString();
			if (++n == params.size()) {
				type = type.replaceFirst("\\[\\]", "...");
			}
			sbuf.append("final ");
			sbuf.append(type);
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

