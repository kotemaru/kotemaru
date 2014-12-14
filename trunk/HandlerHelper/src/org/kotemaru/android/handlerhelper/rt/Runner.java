package org.kotemaru.android.handlerhelper.rt;

public abstract class Runner implements Runnable {
	private String method;
	private Object[] arguments;
	private int retryCount = 0;
	private long executeTime = -1;

	public Runner() {
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
		return "Runner[" + method + "(" + arguments + ")]";
	}
	public String toTraceString(Runner caller) {
		StringBuilder sbuf = new StringBuilder(200);
		sbuf.append(Integer.toHexString(System.identityHashCode(caller)))
				.append("->")
				.append(Integer.toHexString(System.identityHashCode(this)))
				.append(':').append(method).append('(');
		for (Object arg : arguments) {
			if (arg != arguments[0]) sbuf.append(',');
			sbuf.append(arg.toString());
		}
		sbuf.append(')');
		return sbuf.toString();
	}
}
