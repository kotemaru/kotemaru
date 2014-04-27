package org.kotemaru.android.nnalert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class NicoNamaAlertServer {
	private static final String TAG = "NicoNamaAlertServer";

	private static final String UTF8 = "UTF8";
	private static final String LOGIN1_URL = "https://secure.nicovideo.jp/secure/login?site=nicolive_antenna";
	private static final String LOGIN2_URL = "http://live.nicovideo.jp/api/getalertstatus";

	// private static final String SERRVER_ADDR = "kote.dip.jp";
	private static final String SERRVER_ADDR = "192.168.0.2";
	private static final int SERRVER_PORT = 9001;

	private static DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
	private static XPathFactory xpfactory = XPathFactory.newInstance();

	public interface OnLoginListener {
		public void onLogin(List<String> communities);
		public void onError(Throwable t);
	}

	public static void registerAsync(Context context, boolean isRegister, String regId, String mail, String pass) {
		new RegisterTask(context, isRegister).execute(regId, mail, pass);
	}

	private static class RegisterTask extends AsyncTask<String, Void, String> {
		private Context context;
		private boolean isRegister;

		public RegisterTask(Context context, boolean isRegister) {
			this.context = context;
			this.isRegister = isRegister;
		}

		@Override
		protected void onPreExecute() {
			Transit.waiting(context, context.getString(R.string.message_registoring));
		}

		@Override
		protected String doInBackground(String... params) {
			final String regId = params[0];
			final String mail = params[1];
			final String pass = params[2];

			try {
				List<String> communities = login(mail, pass);
				String error = register(isRegister, regId, mail, communities);
				return error;
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				return e.getMessage();
			}
		}
		@Override
		protected void onPostExecute(String error) {
			if (error == null) {
				Log.d(TAG, "register finish");
				if (isRegister) {
					Transit.finish(context, R.string.message_finish, "");
				} else {
					Transit.finish(context, R.string.message_finish_unregister, "");
				}
			} else {
				Log.d(TAG, "register error:" + error);
				Transit.dialog(context, R.string.message_error, error);
			}
		}
	}

	public static List<String> login(String mail, String pass) throws Exception {
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

		// login-2
		doc = doPost(LOGIN2_URL, "ticket=" + ticket);
		status = xpath.evaluate("/getalertstatus/@status", doc);
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

	private static Document doPost(String url, String params) throws IOException, ParserConfigurationException,
			IllegalStateException, SAXException {
		HttpPost method = new HttpPost(url);

		DefaultHttpClient client = new DefaultHttpClient();

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
	}

	public static String register(boolean isRegister, String regId, String mail, List<String> communities) {
		try {
			String xml = "<?xml version='1.0' encoding='utf-8'>\n"
					+ "<request_register>\n"
					+ "<command>" + (isRegister ? "register" : "unregister") + "</command>"
					+ "<regId>" + regId + "</regId>\n"
					+ "<mail>" + mail + "</mail>\n"
					+ "<communities>" + toXml(communities) + "</communities>\n"
					+ "</request_register>";

			Socket sock = new Socket(SERRVER_ADDR, SERRVER_PORT);
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
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return e.toString();
		}
	}

	private static String toXml(List<String> communities) {
		StringBuilder sbuf = new StringBuilder();
		for (String commId : communities) {
			sbuf.append("<community_id>").append(commId).append("</community_id>\n");
		}
		return sbuf.toString();
	}

}