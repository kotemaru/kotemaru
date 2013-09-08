package org.kotemaru.android.statemachine;

import java.io.Serializable;


public abstract class StateTask implements Runnable, Serializable {
	private static final long serialVersionUID = 1L;

	public static final int UI = 0;
	
	private int threadType = 0;
	private int state = 0;
	private String stateStr = "";
	private String[] options;

	
	public StateTask(int state,String stateStr, String[] options) {
		this.setState(state);
		this.setStateStr(stateStr);
		this.setOptions(options);
	}
	public int getThreadType() {
		return threadType;
	}

	public void setThreadType(int threadType) {
		this.threadType = threadType;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String[] getOptions() {
		return options;
	}
	public void setOptions(String[] options) {
		this.options = options;
	}
	public String getStateStr() {
		return stateStr;
	}
	public void setStateStr(String stateStr) {
		this.stateStr = stateStr;
	}

	
}
