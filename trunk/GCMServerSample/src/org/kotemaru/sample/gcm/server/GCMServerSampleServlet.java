package org.kotemaru.sample.gcm.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;


/**
 * GCMのサーバ・サンプル・サーブレット
 * - API
 * -- ?action=register&userId={ユーザID}&regId={端末ＩＤ}
 * -- ?action=unregister&userId={ユーザID}
 * -- ?action=send&userId={ユーザID}&mes={送信メッセージ}
 * 
 * 注：いろいろ端折ってます。Googleのサンプルも参照してください。
 * @author @kotemaru.org
 */

public class GCMServerSampleServlet extends HttpServlet {
	
	/**
	 * https://code.google.com/apis/console/ で生成したAPIキー。
	 */
	private static final String API_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
	private static final int RETRY_COUNT = 5;

	/**
	 * ユーザIDからRegistrationIdを引くテーブル。
	 * <li>本来はストレージに保存すべき情報。
	 * <li>key=ユーザID: サービスの管理するＩＤ。
	 * <li>value=RegistrationId: AndroidがGCMから取得した端末ＩＤ。
	 */
	static Map<String,String> deviceMap = new HashMap<String,String>();
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) 
			throws IOException {
		
		System.out.println("=> "+req.getQueryString());
		
		String action         = req.getParameter("action");
		String registrationId = req.getParameter("regId");
		String userId         = req.getParameter("userId");
		String msg            = req.getParameter("msg");

		if ("register".equals(action)) {
			// 端末登録、Androidから呼ばれる。
			deviceMap.put(userId, registrationId);
			
		} else if ("unregister".equals(action)) {
			// 端末登録解除、Androidから呼ばれる。
			deviceMap.remove(userId);
			
		} else if ("send".equals(action)) {
			// メッセージ送信。任意の送信アプリから呼ばれる。

			registrationId = deviceMap.get(userId);
			Sender sender = new Sender(API_KEY);
			Message message = new Message.Builder().addData("msg", msg).build();
			Result result = sender.send(message, registrationId, RETRY_COUNT);
			
			res.setContentType("text/plain");
			res.getWriter().println("Result="+result);
		} else if ("sendAll".equals(action)) {
			// TODO: 省略。googleのサンプル参照。
		} else {
			res.setStatus(500);
		}
	}
}
