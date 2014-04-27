package org.kotemaru.android.nnalert;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gcm.GCMRegistrar;

public class RegisterActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getSimpleName();

	public static final String MESSAGE_ID_KEY = "MESSAGE_ID_KEY";
	public static final String EXIT_KEY = "EXIT";
	public static final String PREF_FILE = "pref";

	private EditText mailAddressEdit;
	private EditText passwordEdit;
	private Button registerButton;
	private Button unregisterButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		mailAddressEdit = (EditText) this.findViewById(R.id.mail_address);
		passwordEdit = (EditText) this.findViewById(R.id.password);
		registerButton = (Button) this.findViewById(R.id.register);
		registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				register(true);
			}
		});
		unregisterButton = (Button) this.findViewById(R.id.unregister);
		unregisterButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				register(false);
			}
		});
		
		SharedPreferences pref = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
		mailAddressEdit.setText(pref.getString("mail", ""));
		//passwordEdit.setText(pref.getString("pass", ""));
	}
	@Override
	protected void onResume() {
		super.onResume();
		boolean isExit = getIntent().getBooleanExtra(EXIT_KEY, false);
		if (isExit) {
			finish();
		}
	}
	
	public void register(boolean isRegister) {
		String regId = GCMRegistrar.getRegistrationId(this);
		if (regId == null) return;
		String mail = mailAddressEdit.getText().toString();
		String pass = passwordEdit.getText().toString();
		
		SharedPreferences pref = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("mail", mail);
		//editor.putString("pass", pass);
		editor.commit();
		
		NicoNamaAlertServer.registerAsync(getApplicationContext(), isRegister, regId, mail, pass);
		if (!isRegister) {
			GCMRegistrar.unregister(this);
		}
		//this.finish();
	}

}
