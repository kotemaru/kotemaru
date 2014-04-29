package org.kotemaru.android.nnalert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.kotemaru.android.nnalert.PrefActivity.Config;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.util.Log;

public class RegisterTask extends AsyncTask<String, Void, String> {
	private static final String TAG = "RegisterTask";

	private static final String UTF8 = "UTF8";
	private static final String LOGIN1_URL = "https://secure.nicovideo.jp/secure/login?site=nicolive_antenna";
	private static final String LOGIN2_URL = "http://live.nicovideo.jp/api/getalertstatus";

	private static DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
	private static XPathFactory xpfactory = XPathFactory.newInstance();

	private NicoNamaAlertApplication application;
	private boolean isRegister;
	private HttpPost currentMethod;
	private Socket currentSocket;

	public RegisterTask(NicoNamaAlertApplication application, boolean isRegister) {
		this.application = application;
		this.isRegister = isRegister;
	}

	@Override
	protected void onPreExecute() {
		String taskId = application.putAsyncTask(this);
		Transit.waiting(application, application.getString(R.string.message_registoring), taskId);
	}

	@Override
	protected String doInBackground(String... params) {
		final String regId = params[0];
		final String mail = params[1];
		final String pass = params[2];

		try {
			String ticket = login1(mail, pass);
			if (this.isCancelled()) return "Cancelled";
			List<String> communities = login2(ticket);
			if (this.isCancelled()) return "Cancelled";
			String error = register(isRegister, regId, mail, communities);
			return error;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return e.getMessage();
		}
	}
	@Override
	protected void onPostExecute(String error) {
		application.removeAsyncTask(this);
		if (error == null) {
			Log.d(TAG, "register finish");
			if (isRegister) {
				Transit.finish(application, R.string.message_finish, "");
			} else {
				Transit.finish(application, R.string.message_finish_unregister, "");
			}
		} else {
			Log.d(TAG, "register error:" + error);
			Transit.dialog(application, R.string.message_error, error);
		}
	}
	@Override
	protected void onCancelled() {
		application.removeAsyncTask(this);
	}
	public void abort() {
		Log.d(TAG, "abort");
		super.cancel(false);
		synchronized (this) {
			if (currentMethod != null) {
				Log.d(TAG, "currentMethod.abort");
				currentMethod.abort();
			}
			if (currentSocket != null) {
				try {
					Log.d(TAG, "currentSocket.close");
					currentSocket.close();
				} catch (IOException e) {
					Log.e(TAG, "Socket close", e);
				}
			}
		}
	}

	public String login1(String mail, String pass) throws Exception {
		XPath xpath = xpfactory.newXPath();
		// login-1
		String param = "mail=" + URLEncoder.encode(mail, UTF8)
				+ "&password=" + URLEncoder.encode(pass, UTF8);
		Document doc = doPost(LOGIN1_URL, param);
		String status = xpath.evaluate("/nicovideo_user_response/@status", doc);
		String ticket = xpath.evaluate("/nicovideo_user_response/ticket/text()", doc);
		Log.i(TAG, "login-1: " + status + ":" + ticket + ":" + param);
		if (!"ok".equals(status)) {
			throw new Exception("Login failed. status=" + status);
		}
		return ticket;
	}
	public List<String> login2(String ticket) throws Exception {
		XPath xpath = xpfactory.newXPath();

		// login-2
		Document doc = doPost(LOGIN2_URL, "ticket=" + ticket);
		String status = xpath.evaluate("/getalertstatus/@status", doc);
		if (!"ok".equals(status)) {
			throw new Exception("Login failed. status=" + status);
		}

		NodeList nodes = (NodeList) xpath.evaluate(
				"/getalertstatus/communities/community_id", doc, XPathConstants.NODESET);
		List<String> communities = new ArrayList<String>(nodes.getLength());
		for (int i = 0; i < nodes.getLength(); i++) {
			communities.add(nodes.item(i).getTextContent());
		}
		Log.i(TAG, "login-2: " + communities);

		return communities;
	}

	private Document doPost(String url, String params) throws IOException, ParserConfigurationException,
			IllegalStateException, SAXException {
		HttpPost method = new HttpPost(url);
		this.setCurrentMethod(method);

		DefaultHttpClient client = new DefaultHttpClient();
		try {
			// POST データの設定
			StringEntity paramEntity = new StringEntity(params);
			paramEntity.setChunked(false);
			paramEntity.setContentType("application/x-www-form-urlencoded");
			method.setEntity(paramEntity);

			HttpResponse response = client.execute(method);
			int status = response.getStatusLine().getStatusCode();
			if (status != HttpStatus.SC_OK) {
				throw new IOException("Bad http status " + status);
			}

			DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
			Document document = dbuilder.parse(response.getEntity().getContent());
			return document;
		} finally {
			client.getConnectionManager().shutdown();
			this.setCurrentMethod(null);
		}
	}

	public String register(boolean isRegister, String regId, String mail, List<String> communities) {
		try {
			String xml = "<?xml version='1.0' encoding='utf-8'>\n"
					+ "<request_register>\n"
					+ "<command>" + (isRegister ? "register" : "unregister") + "</command>"
					+ "<regId>" + regId + "</regId>\n"
					+ "<mail>" + mail + "</mail>\n"
					+ "<communities>" + toXml(communities) + "</communities>\n"
					+ "</request_register>";

			Socket sock = new Socket();
			this.setCurrentSocket(sock);
			sock.connect(new InetSocketAddress(Config.getHost(), Config.getPost()), 5000);
			try {
				InputStream in = sock.getInputStream();
				OutputStream out = sock.getOutputStream();

				out.write(xml.getBytes("UTF8"));
				out.flush();

				DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
				Document document = dbuilder.parse(in);
				XPath xpath = xpfactory.newXPath();
				String status = xpath.evaluate("/response_register/@status", document);
				if (!"success".equals(status)) {
					throw new Exception("Bad status " + status);
				}
				return null;
			} finally {
				sock.close();
				this.setCurrentSocket(null);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return e.toString();
		}
	}

	private String toXml(List<String> communities) {
		StringBuilder sbuf = new StringBuilder();
		for (String commId : communities) {
			sbuf.append("<community_id>").append(commId).append("</community_id>\n");
		}
		return sbuf.toString();
	}

	public synchronized HttpPost getCurrentMethod() {
		return currentMethod;
	}

	public synchronized void setCurrentMethod(HttpPost currentMethod) {
		this.currentMethod = currentMethod;
	}

	public synchronized Socket getCurrentSocket() {
		return currentSocket;
	}

	public synchronized void setCurrentSocket(Socket currentSocket) {
		this.currentSocket = currentSocket;
	}

}
