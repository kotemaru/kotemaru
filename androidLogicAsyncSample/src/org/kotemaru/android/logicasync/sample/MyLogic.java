package org.kotemaru.android.logicasync.sample;

import java.io.Serializable;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.kotemaru.android.logicasync.annotation.Logic;
import org.kotemaru.android.logicasync.annotation.Task;


@Logic
public class MyLogic implements Serializable {
	private static final long serialVersionUID = 1L;

	public MyLogicAsync async = new MyLogicAsync(this);
	private UIAction uiAction;

	public MyLogic(UIAction uiAction) {
		this.uiAction = uiAction;
	}
	

	@Task
	public void doGetHtml(String url) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		try {
			String html = httpClient.execute(request, new BasicResponseHandler());
			async.doGetHtmlFinish(html);
		} catch (Exception e) {
			async.doGetHtmlError(e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	@Task("UI")
	public void doGetHtmlFinish(String html) {
		uiAction.updateView(html);
	}

	@Task("UI")
	public void doGetHtmlError(Exception e) {
		uiAction.errorDialog(e.getMessage());
	}
}
