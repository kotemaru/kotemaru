package org.kotemaru.android.nnalert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DialogActivity extends Activity {
	private static final String TAG = DialogActivity.class.getSimpleName();

	public static final String MESSAGE_ID_KEY = "MESSAGE_ID";
	public static final String MESSAGE_DETAIL_KEY = "MESSAGE_DETAIL";
	public static final String TASK_ID_KEY = "TASK_ID";

	public static final String MODE_KEY = "MODE";
	public static final int MODE_ALERT = 1;
	public static final int MODE_CONFIRM = 2;
	public static final int MODE_WATING = 3;
	public static final int MODE_FINISH = 4;
	
	private TextView dialogMessage;
	private TextView dialogMessageDetail;
	
	private ProgressBar progressIcon;
	private ImageView alertIcon;

	private Button okButton;
	private Button cancelButton;
	private Button finishButton;
	private DismissListener dismissListener = new DismissListener();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);

		dialogMessage = (TextView) this.findViewById(R.id.dialogMessage);
		dialogMessageDetail = (TextView) this.findViewById(R.id.dialogMessageDetail);
		
		progressIcon =  (ProgressBar) this.findViewById(R.id.progressIcon);
		alertIcon =  (ImageView) this.findViewById(R.id.alertIcon);
		
		okButton = (Button) this.findViewById(R.id.okButton);
		cancelButton = (Button) this.findViewById(R.id.cancelButton);
		finishButton = (Button) this.findViewById(R.id.finishButton);
		setup(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(TAG, "onNewIntent");
		super.onNewIntent(intent);
		setIntent(intent);
		setup(intent);
	}

	private void setup(Intent intent) {
		final DialogActivity self = DialogActivity.this;
		@SuppressWarnings("unused")
		final NicoNamaAlertApplication application = (NicoNamaAlertApplication)getApplication();

		int messageId = intent.getIntExtra(MESSAGE_ID_KEY, 0);
		String messageDetail = intent.getStringExtra(MESSAGE_DETAIL_KEY);
		int mode = intent.getIntExtra(MODE_KEY, MODE_ALERT);
		@SuppressWarnings("unused")
		final String taskId = intent.getStringExtra(TASK_ID_KEY);

		dialogMessage.setText(getString(messageId));
		dialogMessageDetail.setText(messageDetail);

		okButton.setVisibility(View.GONE);
		cancelButton.setVisibility(View.GONE);
		finishButton.setVisibility(View.GONE);
		progressIcon.setVisibility(View.GONE);
		alertIcon.setVisibility(View.GONE);

		switch (mode) {
		case MODE_ALERT:
			alertIcon.setVisibility(View.VISIBLE);
			okButton.setVisibility(View.VISIBLE);
			okButton.setOnClickListener(dismissListener);
			break;
		case MODE_CONFIRM:
			okButton.setVisibility(View.VISIBLE);
			okButton.setOnClickListener(dismissListener);
			cancelButton.setVisibility(View.VISIBLE);
			cancelButton.setOnClickListener(dismissListener);
			break;
		case MODE_WATING:
			progressIcon.setVisibility(View.VISIBLE);
			/* Note: Socket.connect()は中断不可(timeoutのみ)なのでcancelボタンは付けない。
			cancelButton.setVisibility(View.VISIBLE);
			cancelButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "Dialog cancel.");
					AsyncTask<?,?,?> task = application.getAsyncTask(taskId);
					if (task != null) ((RegisterTask)task).abort();
					DialogActivity.this.finish();
				}
			});
			*/
			break;
		case MODE_FINISH:
			finishButton.setVisibility(View.VISIBLE);
			finishButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "Task exit,");
					DialogActivity.this.finish();
					Transit.exit(self);
				}
			});
			break;
		}
	}

	class DismissListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Log.d(TAG, "Dialog finish,");
			DialogActivity.this.finish();
		}
	};

}
