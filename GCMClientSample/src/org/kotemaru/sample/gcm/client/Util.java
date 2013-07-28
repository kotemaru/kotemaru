package org.kotemaru.sample.gcm.client;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

/**
 * ユーティリティ。GCMの本質とは無関係。
 * @author @kotemaru.org
 */
public class Util {

	/**
	 * (非同期)HTTPのGETリクエスト発行。色々手抜き。
	 * @param uri HTTPのURI
	 * @return タスク。
	 */
	public static AsyncTask<String, Void, String> doGetAsync(String uri) {
		AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... params) {
				return Util.doGet(params[0]);
			}
		};
		task.execute(uri);
		return task;
	}

	/**
	 * HTTPのGETリクエスト発行。
	 * @param uri HTTPのURI
	 * @return HTTP取得本文
	 */
	public static String doGet(String uri) {
		HttpGet request = new HttpGet(uri);
		HttpClient httpClient = new DefaultHttpClient();
		try {
			String body = httpClient.execute(request, new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status != 200) {
						throw new RuntimeException("HTTP error: " + status);
					}
					return EntityUtils.toString(response.getEntity(), "UTF-8");
				}
			});
			return body;
		} catch (ClientProtocolException e) {
			Log.e("doGet", e.toString());
			throw new RuntimeException(e);
		} catch (IOException e) {
			Log.e("doGet", e.toString());
			throw new RuntimeException(e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
}
