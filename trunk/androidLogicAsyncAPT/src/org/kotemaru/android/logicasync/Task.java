package org.kotemaru.android.logicasync;

import java.io.Serializable;


public abstract class Task implements Runnable, Serializable {
	private static final long serialVersionUID = 1L;

	public static int NORMAL   = 0;
	public static int UI       = 1;
	public static int PARALLEL = 2;

	private String method;
	private Object[] arguments;
	private int threadType = NORMAL;
	
	public Task() {
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
	public void setArguments(Object... arguments) {
		this.arguments = arguments;
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
