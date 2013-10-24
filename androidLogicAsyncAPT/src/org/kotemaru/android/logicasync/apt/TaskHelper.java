package org.kotemaru.android.logicasync.apt;

import java.util.Collection;

import org.kotemaru.android.logicasync.annotation.*;
import org.kotemaru.apthelper.AptUtil;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public class TaskHelper {
	private ExecutableElement decl;
	private Task task;
	private Logic logic;

	public TaskHelper(TypeElement classDecl, ExecutableElement decl) {
		this.decl = decl;
		this.task = decl.getAnnotation(Task.class);
		this.logic = classDecl.getAnnotation(Logic.class);
	}

	public boolean isTask() {
		return task != null;
	}
	public boolean hasOption(String key) {
		if (task == null) return false;
		String[] options = task.value();
		for (int i=0; i<options.length; i++) {
			if (key.equals(options[i])) return true;
		}
		return false;
	}

	public String getOptions() {
		if (task == null) return null;
		String[] options = task.value();
		if (options == null) return null;
		StringBuilder sbuf = new StringBuilder("new String[]{");
		for (int i=0; i<options.length; i++) {
			sbuf.append("\""+options[i]+"\",");
		}
		sbuf.append("}");
		return sbuf.toString();
	}
	public String getParams() {
		ExecutableElement d = this.decl;
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
		String args = AptUtil.getArguments(decl);
		if (hasCamma && !args.isEmpty()) args = ", " + args;
		return args;
	}
	public String getArguments() {
		return getArguments(false);
	}

	
}

