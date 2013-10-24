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
	private int retryCount = 0;
	private long executeTime = -1;
	
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
	
	public int getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public long getExecuteTime() {
		return executeTime;
	}
	public void setExecuteTime(long executeTime) {
		this.executeTime = executeTime;
	}
	
	public String toString() {
		return "Task["+method+"("+arguments+")]";
	}
	
}
