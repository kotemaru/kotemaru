package org.kotemaru.android.handlerhelper.sample;

import java.io.Serializable;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.kotemaru.android.handlerhelper.annotation.HandlerHelper;
import org.kotemaru.android.handlerhelper.annotation.Handling;

import android.os.Looper;
 
@HandlerHelper
public class MyLogic implements Serializable {
	private static final long serialVersionUID = 1L;

	public MyLogicHandler handler = new MyLogicHandler(this, Looper.getMainLooper());
	private UIAction uiAction;
 
	public MyLogic(UIAction uiAction) {
		this.uiAction = uiAction;
	}
	
	@Handling(delay=1)
	public void doGetHtml(String url) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		try {
			String html = httpClient.execute(request, new BasicResponseHandler());
			//handler.doGetHtmlFinish(html);
		} catch (Exception e) {
			//handler.doGetHtmlError(e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	@Handling(thread=Handling.Thread.UI)
	public void doGetHtmlFinish(String html) {
		uiAction.updateView(html);
	}

	@Handling(thread=Handling.Thread.UI)
	public void doGetHtmlError(Exception e) {
		uiAction.errorDialog(e.getMessage());
	}
}
