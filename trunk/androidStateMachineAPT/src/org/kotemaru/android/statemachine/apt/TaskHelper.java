package org.kotemaru.android.statemachine.apt;

import java.util.Collection;

import org.kotemaru.android.statemachine.annotation.*;
import org.kotemaru.apthelper.AptUtil;

import com.sun.mirror.declaration.ExecutableDeclaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;


public class TaskHelper {
	private MethodDeclaration decl;
	private Task task;
	private Logic logic;

	public TaskHelper(TypeDeclaration classDecl, MethodDeclaration decl) {
		this.decl = decl;
		this.task = decl.getAnnotation(Task.class);
		this.logic = classDecl.getAnnotation(Logic.class);
	}

	public boolean isTask() {
		return task != null;
	}
	public boolean isParallelTask() {
		return hasOption("parallel");
	}
	public boolean isUITask() {
		return hasOption("UI");
	}
	public boolean hasOption(String key) {
		if (task == null) return false;
		String[] options = task.value();
		for (int i=0; i<options.length; i++) {
			if ("UI".equals(options[i])) return true;
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
		ExecutableDeclaration d = this.decl;
		Collection<ParameterDeclaration> params = d.getParameters();
		if (params.size() == 0) return "";
		StringBuffer sbuf = new StringBuffer(params.size()*20);
		for (ParameterDeclaration param : params)  {
			sbuf.append("final ");
			sbuf.append(param.getType());
			sbuf.append(' ');
			sbuf.append(param.getSimpleName());
			sbuf.append(',');
		}
		sbuf.setLength(sbuf.length()-1);
		return sbuf.toString();
	}
	public String getArguments() {
		return AptUtil.getArguments(decl);
	}

	
}

