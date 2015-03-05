package org.kotemaru.android.sample.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.kotemaru.android.delegatehandler.annotation.GenerateDelegateHandler;
import org.kotemaru.android.delegatehandler.annotation.Handle;
import org.kotemaru.android.delegatehandler.rt.OnDelegateHandlerErrorListener;
import org.kotemaru.android.fw.FwControllerBase;
import org.kotemaru.android.fw.thread.ThreadManager;
import org.kotemaru.android.sample.MyApplication;
import org.kotemaru.android.sample.model.Sample2Model;
import org.kotemaru.android.sample.model.Sample2Model.Blog;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@GenerateDelegateHandler
public class Sample2Controller extends FwControllerBase<MyApplication> implements OnDelegateHandlerErrorListener {
	private static final String TAG = Sample2Controller.class.getSimpleName();
	public final Sample2ControllerHandler mHandler;
	public final Sample2Model mModel;
	public final HttpClient mHttpClient = new DefaultHttpClient();

	public Sample2Controller(MyApplication app) {
		super(app);
		mHandler = new Sample2ControllerHandler(this, app.getThreadManager());
		mModel = app.getModel().getSample2Model();
	}

	@Handle(thread = ThreadManager.NETWORK, retry = 1, interval = 1000)
	public void loadListData(String url) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);
		HttpResponse response = mHttpClient.execute(request);
		Log.d(TAG,"response code="+response.getStatusLine());

		InputStream in = response.getEntity().getContent();
		try {
			Reader reader = new InputStreamReader(in, "UTF8");
			Type type = new TypeToken<ArrayList<Blog>>() {
			}.getType();
			ArrayList<Blog> list = new Gson().fromJson(reader, type);
			mModel.writeLock();
			try {
				mModel.setBlogList(list);
			} finally {
				mModel.writeUnlock();
			}
		} finally {
			in.close();
		}
		mApplication.updateCurrentActivity();
	}


	@Override
	@Handle(thread = ThreadManager.WORKER)
	public void onDelegateHandlerError(Throwable t, String method, Object... args) {
		Log.e("onDelegateHandlerError",t.getMessage(),t);
		mModel.getDialogModel().setError(t);
		mApplication.updateCurrentActivity();
	}

}
