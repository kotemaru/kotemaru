package org.kotemaru.android.async.sample;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.kotemaru.android.async.ByteBufferReader;
import org.kotemaru.android.async.R;
import org.kotemaru.android.async.http.AsyncHttpClient;
import org.kotemaru.android.async.http.AsyncHttpGet;
import org.kotemaru.android.async.http.AsyncHttpListenerBase;
import org.kotemaru.android.async.http.AsyncHttpPost;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
				doSend2();
				//doSend();
			}
		});
	}

	AsyncHttpClient mClient = new AsyncHttpClient();
	private void doSend() {
		try {
			AsyncHttpPost request = new AsyncHttpPost("http://192.168.0.2/cgi-bin/log.sh");
			HttpEntity httpEntity = new StringEntity("Test data");
			request.setHttpEntity(httpEntity);

			mClient.execute(request, new AsyncHttpListenerBase() {
				@Override
				public void onResponseBody(HttpResponse httpResponse) {
					if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
						// サーバからエラー
					}
					try {
						InputStream is = httpResponse.getEntity().getContent();
						BufferedReader br = new BufferedReader(new InputStreamReader(is));
						String line;
						while ((line = br.readLine()) != null) {
							Log.i("DEBUG", "->" + line);
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
	private void doSend2() {
		try {
			AsyncHttpGet request = new AsyncHttpGet("https://www.nttdocomo.co.jp/");
			mClient.execute(request, new AsyncHttpListenerBase() {
				FileChannel mFileChannel;

				@Override
				public boolean isResponseBodyPart() {
					return true;
				}
				@Override
				public void onResponseBodyPart(ByteBufferReader transporter) {
					try {
						if (mFileChannel == null) {
							@SuppressWarnings("resource")
							FileOutputStream file = new FileOutputStream(getFilesDir()+"/index.html");
							mFileChannel = file.getChannel();
						}

						ByteBuffer buffer = transporter.read();
						if (buffer != null) {
							while (buffer.hasRemaining()) {
								if (mFileChannel.write(buffer) == -1) break;
							}
						} else {
							mFileChannel.close();
						}
						transporter.release(buffer);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onResponseBody(HttpResponse httpResponse) {
					// not called.
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
