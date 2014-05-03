package org.kotemaru.nnalert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.kotemaru.nnalert.Config.UserInfo;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class RegistrationServer {
	public static void start(int port) throws IOException {
		new AcceptThread(port).start();
	}

	private static class AcceptThread extends Thread {
		private static final String UTF8 = "utf8";
		private ServerSocket ssock;

		public AcceptThread(int port) throws IOException {
			this.ssock = new ServerSocket(port);
		}

		@Override
		public void run() {
			try {
				while (true) {
					Socket sock = ssock.accept();
					doRegister(sock);
				}
			} catch (IOException e) {
				Log.e("AcceptThread", e);
			}
		}

		public void doRegister(Socket sock) {
			try {
				InputStream in = sock.getInputStream();
				OutputStream out = sock.getOutputStream();

				String result;
				try {
					UserInfo uinfo = Config.parseUserInfo(in);
					if (uinfo == null) {
						throw new Exception("Xml data error.");
					}
					if ("register".equals(uinfo.command)) {
						Config.saveUserInfo(uinfo);
						sendTestMessage("onRegistered", uinfo);
						Log.d("register user:" + uinfo.mail);
					} else { // unregister
						Config.removeUserInfo(uinfo);
						sendTestMessage("onUnRegistered", uinfo);
						Log.d("unregister user:" + uinfo.mail);
					}
					result = "<?xml version='1.0' encoding='utf-8' ?>"
							+ "<response_register status='success'>"
							+ "<userId>" + uinfo.mail + "</userId>"
							+ "</response_register>";
					Log.d("receiveRegistrationId:" + uinfo.regId);
				} catch (Throwable t) {
					result = "<?xml version='1.0' encoding='utf-8' ?>"
							+ "<response_register status='error'>"
							+ "<error>" + t.getMessage() + "</error>"
							+ "</response_register>";
					Log.e("receiveRegistrationId:", t);
				}
				out.write(result.getBytes(UTF8));
				out.flush();
			} catch (IOException e) {
				Log.e("ConnectThread", e);
			} finally {
				try {
					sock.close();
				} catch (IOException e) {
					Log.e("ConnectThread", e);
				}
			}
		}

		public Result sendTestMessage(String type, UserInfo uinfo) throws IOException {
			Sender sender = new Sender(Config.getApiKey());
			Message message = new Message.Builder()
					.addData("messageType", type)
					.addData("mail", uinfo.mail)
					.build();
			Result result = sender.send(message, uinfo.regId, 5);
			return result;
		}
	}
}