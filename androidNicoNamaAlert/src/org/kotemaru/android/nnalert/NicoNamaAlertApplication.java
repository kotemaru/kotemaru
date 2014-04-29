package org.kotemaru.android.nnalert;

import java.util.HashMap;

import android.app.Application;
import android.os.AsyncTask;

public class NicoNamaAlertApplication extends Application {

	private HashMap<String, AsyncTask<?, ?, ?>> asyncTasks;

	@Override
	public void onCreate() {
		asyncTasks = new HashMap<String, AsyncTask<?, ?, ?>>();

	}

	public AsyncTask<?, ?, ?> getAsyncTask(String id) {
		// Ignore synchronized. Calling from UI Thread.
		return asyncTasks.get(id);
	}
	public String putAsyncTask(AsyncTask<?, ?, ?> task) {
		// Ignore synchronized. Calling from UI Thread.
		String id = "" + System.identityHashCode(task);
		asyncTasks.put(id, task);
		return id;
	}
	public void removeAsyncTask(AsyncTask<?, ?, ?> task) {
		// Ignore synchronized. Calling from UI Thread.
		String id = "" + System.identityHashCode(task);
		removeAsyncTask(id);
	}

	public void removeAsyncTask(String id) {
		// Ignore synchronized. Calling from UI Thread.
		asyncTasks.remove(id);
	}
}
