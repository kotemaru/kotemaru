package org.kotemaru.android.statemachine;

import java.io.Serializable;
import java.util.LinkedList;

import android.os.AsyncTask;

public class StateMachineDriver implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int RUNNABLE = 1;
	public static final int DONE = 2;
	public static final int FAIL = 3;

	private LinkedList<StateTask> queue = new LinkedList<StateTask>();
	private transient Driver driver = new Driver(this);
	private int state = -1;
	private int subState = -1;
	private boolean isAlive = true;



	public StateMachineDriver() {
		driver.execute();
	}
	public synchronized void _stop() {
		isAlive = false;
		this.notifyAll();
	}
	public synchronized int _getState() {
		return this.state;
	}
	public synchronized int _getSubState() {
		return this.subState;
	}
	public synchronized void _setState(int state, int subState) {
		this.state = state;
		this.subState = state;
	}
	
	public synchronized boolean _hasNext() {
		return !queue.isEmpty();
	}

	public synchronized StateTask _next() {
		while (queue.isEmpty()) {
			if (! isAlive) return null;
			try {this.wait(3000);}catch(Exception e){}
		}
		if (! isAlive) return null;
		return queue.poll();
	}
	
	public synchronized void _addTask(StateTask task) {
		queue.add(task);
		this.notifyAll();
	}

	private static class Driver extends AsyncTask<Void, StateTask, Void> {

		private StateMachineDriver parent;

		public Driver(StateMachineDriver parent) {
			super();
			this.parent = parent;
		}

		@Override
		protected Void doInBackground(Void... params) {
			StateTask task = null;
			while ((task=parent._next()) != null) {
				parent._setState(task.getState(), RUNNABLE);
				if (task.getThreadType() == StateTask.UI) {
					this.publishProgress(task);
				} else {
					doRunTask(task);
				}
			}
			return null;
		}


		@Override
		protected void onProgressUpdate(StateTask... progress) {
			StateTask task = progress[0];
			doRunTask(task);
		}

		@Override
		protected void onPostExecute(Void result) {
			// non operate.
		}
		
		private void doRunTask(StateTask task) {
			try {
				task.run();
				parent._setState(task.getState(), DONE);
			} catch (Throwable t) {
				parent._setState(task.getState(), FAIL);
			}
		}

	}


}
