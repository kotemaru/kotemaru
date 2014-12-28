package org.kotemaru.android.async.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		View hello = this.findViewById(R.id.hello);
		hello.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doSend();
			}
		});
	}
	AsyncHttpClient client = new AsyncHttpClient();

	protected void doSend() {
		try {
			//AsyncHttpClient client = new AsyncHttpClient();
			AsyncHttpPost request = new AsyncHttpPost("http://192.168.0.2/cgi-bin/log.sh");
			HttpEntity httpEntity = new StringEntity("Test data");
			request.setHttpEntity(httpEntity);
			
			client.execute(request, new AsyncHttpListenerBase(){
				@Override
				public void onResponseBody(HttpResponse httpResponse) {
					Log.e("DEBUG","->onResponseBody:"+httpResponse);
					InputStream is;
					try {
						is = httpResponse.getEntity().getContent();
						Log.e("DEBUG","->onResponseBody:"+is);
						BufferedReader br = new BufferedReader( new InputStreamReader(is));
						String line;
						while ((line = br.readLine()) != null) {
							Log.e("DEBUG","->"+line);
						}
						br.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
