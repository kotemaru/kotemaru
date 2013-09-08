package org.kotemaru.android.statemachine.sample;

import java.io.Serializable;
import java.util.List;

import org.kotemaru.android.statemachine.annotation.State;
import org.kotemaru.android.statemachine.annotation.StateMachine;


@StateMachine
public class MyStateMachine implements Serializable {
	private static final long serialVersionUID = 1L;

	MyStateMachineDriver driver = new MyStateMachineDriver(this);

	@State
	public void doListAction() {
		try {
			List<String> list = null; //net.getList();
			driver.doListActionFinish(list);
		} catch (Exception e) {
			driver.doListActionError(e);
		}
	}

	@State("UI")
	public void doListActionFinish(List<String> list) {

	}

	@State("UI")
	public void doListActionError(Exception e) {

	}
}
