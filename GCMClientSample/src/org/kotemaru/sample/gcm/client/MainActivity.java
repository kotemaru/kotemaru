package org.kotemaru.sample.gcm.client;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gcm.GCMRegistrar;

/**
 * クライアントアプリ本体。
 * @author @kotemaru.org
 */
public class MainActivity extends Activity {
	/**
	 * https://code.google.com/apis/console/のProject Number。
	 */
	public static final String SENDER_ID = "nnnnnnnnnnn";

	/**
	 * アプリサーバーのURL。
	 */
	public static final String SERVER_URL = "http://192.168.0.3:8888/gcmserversample";
	/**
	 * アプリのユーザID。本来はログイン中のユーザとかになるはず。
	 */
	public static final String USER_ID = "TarouYamada";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			// GCMへ端末登録。登録後、GCMIntentService.onRegistered()が呼ばれる。
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			// 登録済みの場合、ここではアプリに登録しなおしているが
			// Googleのサンプルでは unregister して register しなおしている。
			String uri = SERVER_URL+"?action=register"
					+"&userId="+USER_ID
					+"&regId="+regId;
			Util.doGetAsync(uri);
		}
	}

    @Override
    protected void onDestroy() {
        GCMRegistrar.onDestroy(this);
        super.onDestroy();
    }

}
