package org.kotemaru.sample.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * 英大文字変換エコーバック Bluetooth サーバ。
 */
public class RfcommServer {
	/**
	 * UUIDは独自プロトコルのサービスの場合は固有に生成する。
	 * - 各種ツールで生成する。（ほぼ乱数）
	 * - 注：このまま使わないように。
	 */
	static final String serverUUID = "11111111111111111111111111111123";

	private StreamConnectionNotifier server = null;

	public RfcommServer() throws IOException {
		// RFCOMMベースのサーバの開始。
		// - btspp:は PRCOMM 用なのでベースプロトコルによって変わる。
		server = (StreamConnectionNotifier) Connector.open(
				"btspp://localhost:" + serverUUID,
				Connector.READ_WRITE, true
		);
		// ローカルデバイスにサービスを登録。必須ではない。
		ServiceRecord record = LocalDevice.getLocalDevice().getRecord(server);
		LocalDevice.getLocalDevice().updateRecord(record);
	}
	
	/**
	 * クライアントからの接続待ち。
	 * @return 接続されたたセッションを返す。
	 */
	public Session accept() throws IOException {
		log("Accept");
		StreamConnection channel = server.acceptAndOpen();
		log("Connect");
		return new Session(channel);
	}
	public void dispose() {
		log("Dispose");
		if (server  != null) try {server.close();} catch (Exception e) {/*ignore*/}
	}

	/**
	 * セッション。
	 * - 並列にセッションを晴れるかは試していない。
	 * - 基本的に Socket と同じ。
	 */
	static class Session implements Runnable {
		private StreamConnection channel = null;
		private InputStream btIn = null;
		private OutputStream btOut = null;
		
		public Session(StreamConnection channel) throws IOException {
			this.channel = channel;
			this.btIn = channel.openInputStream();
			this.btOut = channel.openOutputStream();
		}
		
		/**
		 * 英小文字の受信データを英大文字にしてエコーバックする。
		 * - 入力か空なら終了。
		 */
		public void run() {
			try {
				byte[] buff = new byte[512];
				int n = 0;
				while ((n = btIn.read(buff)) > 0) {
					String data = new String(buff, 0, n);
					log("Receive:"+data);
					btOut.write(data.toUpperCase().getBytes());
					btOut.flush();
				}
			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				close();
			}
		}
		public void close() {
			log("Session Close");
			if (btIn    != null) try {btIn.close();} catch (Exception e) {/*ignore*/}
			if (btOut   != null) try {btOut.close();} catch (Exception e) {/*ignore*/}
			if (channel != null) try {channel.close();} catch (Exception e) {/*ignore*/}
		}
	}
	

	//------------------------------------------------------
	public static void main(String[] args) throws Exception {
		RfcommServer server = new RfcommServer();
		while (true) {
			Session session = server.accept();
			new Thread(session).start();
		}
		//server.dispose();
	}
	private static void log(String msg) {
		System.out.println("["+(new Date()) + "] " + msg);
	}
}
