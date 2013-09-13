package org.kotemaru.android.logicasync;

import java.io.Serializable;


public abstract class Task implements Runnable, Serializable {
	private static final long serialVersionUID = 1L;

	public static int NORMAL   = 0;
	public static int UI       = 1;
	public static int PARALLEL = 2;

	private String method;
	private Object[] arguments;
	private String[] options;
	private int threadType = NORMAL;
	
	public Task(String[] options, String method, Object... args) {
		this.setOptions(options);
		this.setMethod(method);
		this.setArguments(args);
	}
	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Object[] getArguments() {
		return arguments;
	}
	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}
	public String[] getOptions() {
		return options;
	}
	public void setOptions(String[] options) {
		this.options = options;
	}
	public int getThreadType() {
		return threadType;
	}
	public void setThreadType(int threadType) {
		this.threadType = threadType;
	}
	
	public String toString() {
		return "Task["+method+"("+arguments+")]";
	}
	
	
}
