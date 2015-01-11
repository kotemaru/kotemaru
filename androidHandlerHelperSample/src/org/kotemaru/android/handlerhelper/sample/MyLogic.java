package org.kotemaru.android.handlerhelper.sample;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.kotemaru.android.handlerhelper.annotation.DelegateHandlerClass;
import org.kotemaru.android.handlerhelper.annotation.Handling;
import org.kotemaru.android.handlerhelper.rt.DefaultThreadManager;
import org.kotemaru.android.handlerhelper.rt.OnHandlingErrorListener;
import org.kotemaru.android.handlerhelper.rt.ThreadManager;
 
@DelegateHandlerClass
public class MyLogic implements OnHandlingErrorListener {
	public static final boolean IS_TRACE = BuildConfig.DEBUG;

	public MyLogicHandler handler = new MyLogicHandler(this, DefaultThreadManager.getInstance());
	private UIAction uiAction;

	public MyLogic(UIAction uiAction) {
		this.uiAction = uiAction;
	}
	
	@Handling(thread=ThreadManager.NETWORK, retry=3)
	public void doGetHtml(String url) throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		try {
			String html = httpClient.execute(request, new BasicResponseHandler());
			handler.doGetHtmlFinish(html);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	@Handling(thread=ThreadManager.UI)
	public void doGetHtmlFinish(String html) {
		uiAction.updateView(html);
	}

	@Override
	@Handling(thread=ThreadManager.UI)
	public void onHandlingError(Throwable t, String methodName, Object... arguments) {
		uiAction.errorDialog(t.getMessage());
	}
}
