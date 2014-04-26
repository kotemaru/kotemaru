package org.kotemaru.android.nnalert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DialogActivity extends Activity {
	private static final String TAG = DialogActivity.class.getSimpleName();

	public static final String MESSAGE_ID_KEY = "MESSAGE_ID";
	public static final String MESSAGE_DETAIL_KEY = "MESSAGE_DETAIL";

	public static final String BUTTONS_KEY = "BUTTONS";
	public static final int NONE = 0;
	public static final int OK = 1;

	private TextView dialogMessage;
	private TextView dialogMessageDetail;
	private Button okButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);

		dialogMessage = (TextView) this.findViewById(R.id.dialogMessage);
		dialogMessageDetail = (TextView) this.findViewById(R.id.dialogMessageDetail);
		okButton = (Button) this.findViewById(R.id.okButton);
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Dialog finish,");
				finish();
			}
		});
	}
	@Override
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();

		Intent intent = getIntent();
		int messageId = intent.getIntExtra(MESSAGE_ID_KEY, 0);
		String messageDetail = intent.getStringExtra(MESSAGE_DETAIL_KEY);
		int buttons = intent.getIntExtra(BUTTONS_KEY, OK);

		dialogMessage.setText(getString(messageId));
		dialogMessageDetail.setText(messageDetail);
		if (buttons == NONE) {
			okButton.setVisibility(View.GONE);
		} else {
			okButton.setVisibility(View.VISIBLE);
		}
	}

}
