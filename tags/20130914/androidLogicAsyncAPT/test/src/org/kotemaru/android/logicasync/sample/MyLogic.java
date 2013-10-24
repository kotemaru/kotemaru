package org.kotemaru.android.logicasync.sample;

import java.io.Serializable;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.kotemaru.android.logicasync.annotation.Logic;
import org.kotemaru.android.logicasync.annotation.Task;


import android.app.Application;


@Logic
public class MyLogic implements Serializable {
	private static final long serialVersionUID = 1L;

	public MyLogicAsync async = new MyLogicAsync(this);

	
	@Task
	public void doListAction() {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://");
		try {
			String result = httpClient.execute(request, new BasicResponseHandler());
			async.doListActionFinish(list);
		} catch (Exception e) {
			async.doListActionError(e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
	@Task("parallel")
	public void doListParallelAction() {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://");
		try {
			String result = httpClient.execute(request, new BasicResponseHandler());
			async.doListActionFinish(list);
		} catch (Exception e) {
			async.doListActionError(e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	@Task("UI")
	public void doListActionFinish(List<String> list) {
	}

	@Task("UI")
	public void doListActionError(Exception e) {
	}
}
