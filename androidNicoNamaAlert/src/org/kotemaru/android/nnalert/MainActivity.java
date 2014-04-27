package org.kotemaru.android.nnalert;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getSimpleName();
	public static final String FINISH_KEY = "finish";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		boolean isFinish = getIntent().getBooleanExtra(FINISH_KEY, false);
		if (isFinish) {
			finish();
			return;
		}
		
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);

		String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, GCMIntentService.SENDER_ID);
		} else {
			Util.transition(this, RegisterActivity.class);
		}
		finish();
	}
}
