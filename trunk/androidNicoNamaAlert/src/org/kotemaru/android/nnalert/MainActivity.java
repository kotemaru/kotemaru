package org.kotemaru.android.nnalert;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);

		String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, GCMIntentService.SENDER_ID);
		} else {
			Transit.activity(this, RegisterActivity.class);
		}
		finish();
	}
}
