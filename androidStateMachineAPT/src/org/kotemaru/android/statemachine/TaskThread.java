package org.kotemaru.android.statemachine;

import java.io.Serializable;
import java.util.LinkedList;

import android.os.AsyncTask;
import android.util.Log;

public class TaskThread implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "LogicAsync";

	private LinkedList<Task> queue = new LinkedList<Task>();
	private transient Driver driver = new Driver(this);
	private boolean isAlive = true;


	public TaskThread() {
		driver.execute();
	}
	public synchronized void stop() {
		queue.clear();
		isAlive = false;
		this.notifyAll();
	}
	public synchronized void autoStop() {
		isAlive = false;
		this.notifyAll();
	}
	
	public synchronized boolean hasNext() {
		return !queue.isEmpty();
	}

	public synchronized Task next() {
		while (queue.isEmpty()) {
			if (! isAlive) return null;
			try {this.wait(3000);}catch(Exception e){}
		}
		if (! isAlive) return null;
		return queue.poll();
	}
	
	public synchronized void addTask(Task task) {
		queue.add(task);
		this.notifyAll();
	}

	private static class Driver extends AsyncTask<Void, Task, Void> {

		private TaskThread parent;

		public Driver(TaskThread parent) {
			super();
			this.parent = parent;
		}

		@Override
		protected Void doInBackground(Void... params) {
			Task task = null;
			while ((task=parent.next()) != null) {
				if (task.getThreadType() == Task.UI) {
					this.publishProgress(task);
				} else {
					doRunTask(task);
				}
			}
			return null;
		}


		@Override
		protected void onProgressUpdate(Task... progress) {
			Task task = progress[0];
			doRunTask(task);
		}

		@Override
		protected void onPostExecute(Void result) {
			// non operate.
		}
		
		private void doRunTask(Task task) {
			try {
				if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG,"run:"+task.toString());
				task.run();
				if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG,"done:"+task.toString());
			} catch (Throwable t) {
				Log.e(TAG,"fail:"+task.toString(), t);
			}
		}

	}


}
