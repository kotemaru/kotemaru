package org.kotemaru.android.logicasync;

import java.io.Serializable;
import java.util.LinkedList;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class TaskThread implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "LogicAsync";

	private LinkedList<Task> queue = new LinkedList<Task>();
	private transient Driver driver = new Driver(this);
	private boolean isAlive = true;


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public TaskThread() {
		if(Build.VERSION.SDK_INT >= 11){
			driver.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			driver.execute();
		}
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
		Task task;
		while ((task=takeCurrentTask()) == null) {
			task = queue.peekFirst();
			if (task == null) { // empty
				if (! isAlive) return null;
				try {this.wait(3000);}catch(Exception e){}
			} else {
				long waitTime = task.getExecuteTime() - System.currentTimeMillis();
				if (waitTime > 0) {
					try {this.wait(waitTime);}catch(Exception e){}
				}
			}
		}
		return task;
	}
	private Task takeCurrentTask() {
		long curTime = System.currentTimeMillis();
		for (int i=0; i<queue.size(); i++) {
			if (curTime > queue.get(i).getExecuteTime()) {
				return queue.remove(i);
			}
		}
		return null;
	}
	
	
	public synchronized void addTask(Task task) {
		long execTime = task.getExecuteTime();
		int idx = -1;
		for (int i=0; i<queue.size(); i++) {
			if (execTime > queue.get(i).getExecuteTime()) {
				idx = i;
				break;
			}
		}
		if (idx >= 0) {
			queue.add(idx, task);
		} else {
			queue.add(task);
		}
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
			debugLog("run",task);
			do {
				try {
					task.run();
					debugLog("done",task);
				} catch (RetryException ex) {
					if (retryTask(task, ex)) {
						continue;
					}
				} catch (Throwable t) {
					Log.e(TAG,"fail:"+task.toString(), t);
				}
			} while (false);
		}
		private boolean retryTask(Task task, RetryException ex) {
			if (task.getRetryCount() >= ex.getRetryCount()) {
				Log.e(TAG,"fail:retry over:"+task.toString(), ex);
				return false;
			}
			task.setRetryCount(task.getRetryCount()+1);

			if (ex.getDelay() > 0) {
				debugLog("retry-delay",task);
				task.setExecuteTime(System.currentTimeMillis()+ex.getDelay());
				parent.addTask(task);
				return false;
			} else {
				debugLog("retry",task);
				return true;
			}
		}

		
		private void debugLog(String state, Task task) {
			if (!Log.isLoggable(TAG, Log.DEBUG)) return;
			Log.d(TAG,state+":"+task.toString());
		}
			
	}


}
