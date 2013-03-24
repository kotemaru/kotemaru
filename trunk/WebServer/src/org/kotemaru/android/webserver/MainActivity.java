package org.kotemaru.android.webserver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		try {
			new AcceptThread(8080).start();
		} catch (IOException e) {
			postMessage(e.getMessage());
			Log.e("boot",e.getMessage());
		}
	}

	void postMessage(final String msg) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private class AcceptThread extends Thread {
		private ServerSocket ssock;

		public AcceptThread(int port) throws IOException {
			this.ssock = new ServerSocket(port);
		}
		
		@Override
		public void run() {
			try {
				postMessage("Server start");
				while (true) {
					Socket sock = ssock.accept();
					new ConnectThread(sock).start();
				}
			} catch (IOException e) {
				postMessage(e.getMessage());
				Log.e("AcceptThread",e.getMessage());
			}
		}
	}
	
	private class ConnectThread extends Thread {
		private Socket sock;

		public ConnectThread(Socket sock) {
			this.sock = sock;
		}
		@Override
		public void run() {
			try {
				Log.i("ConnectThread","From "+sock.getRemoteSocketAddress());
				
				BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				BufferedOutputStream out = new BufferedOutputStream(sock.getOutputStream());
				
				String reqLine = in.readLine();
				String line = in.readLine();
				while (!line.isEmpty()) {
					line = in.readLine(); // ヘッダ無視 (^^;
				}
				
				String[] parts = reqLine.split(" ");
				String path = parts[1].replaceFirst("[?].*$", "");
				File docroot = new File(Environment.getExternalStorageDirectory(),"docroot");
				
				out.write("HTTP/1.0 200 OK\r\n".getBytes());
				out.write(("Content-type: "+getCType(path)+"\r\n\r\n").getBytes());
				InputStream fin = new FileInputStream(new File(docroot,path));
				try {
					byte[] buff = new byte[1024];
					int n;
					while ((n=fin.read(buff))>0) {
						out.write(buff,0,n);
					}
					out.flush();
					out.close();
				} finally {
					fin.close();
				}
			} catch (IOException e) {
				postMessage(e.getMessage());
				Log.e("ConnectThread",e.getMessage());
			} finally {
				try {
					sock.close();
				} catch (IOException e) {
					postMessage(e.getMessage());
					Log.e("ConnectThread",e.getMessage());
				}
			}
		}
		private String getCType(String path) {
			path = path.toLowerCase();
			if (path.endsWith(".html")) return "text/html";
			if (path.endsWith(".jpg")) return "image/jpeg";
			if (path.endsWith(".png")) return "image/png";
			if (path.endsWith(".gif")) return "image/gif";
			if (path.endsWith(".js")) return "application/javascript";
			if (path.endsWith(".css")) return "text/css";
			return "unknown";
		}
		
	}
}
