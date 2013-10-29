package org.kotemaru.android.sample.bluetooth;

import java.util.Set;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	private final static int DEVICES_DIALOG = 1;
	private final static int ERROR_DIALOG = 2;

	private BluetoothTask bluetoothTask = new BluetoothTask(this);
	
	private ProgressDialog waitDialog;
	private EditText editText1;
	private EditText editText2;
	private String errorMessage = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		editText1 = (EditText) findViewById(R.id.editText1);
		editText2 = (EditText) findViewById(R.id.editText2);

		Button sendBtn = (Button) findViewById(R.id.sendBtn);
		sendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String msg = editText1.getText().toString();
				bluetoothTask.doSend(msg);
			}
		});
		Button resetBtn = (Button) findViewById(R.id.resetBtn);
		resetBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				restart();
			}
		});
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		// Bluetooth初期化
		bluetoothTask.init();
		// ペアリング済みデバイスの一覧を表示してユーザに選ばせる。
		showDialog(DEVICES_DIALOG);
	}
	
	@Override
	protected void onDestroy() {
		bluetoothTask.doClose();
		super.onDestroy();
	}

	public void doSetResultText(String text) {
		editText2.setText(text);
	}

	protected void restart() {
		Intent intent = this.getIntent();
		this.finish();
		this.startActivity(intent);
	}

	
	//----------------------------------------------------------------
	// 以下、ダイアログ関連
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DEVICES_DIALOG) return createDevicesDialog();
		if (id == ERROR_DIALOG) return createErrorDialog();
		return null;
	}
	@SuppressWarnings("deprecation")
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == ERROR_DIALOG) {
			((AlertDialog) dialog).setMessage(errorMessage);
		}
		super.onPrepareDialog(id, dialog);
	}

	public Dialog createDevicesDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Select device");

		// ペアリング済みデバイスをダイアログのリストに設定する。
		Set<BluetoothDevice> pairedDevices = bluetoothTask.getPairedDevices();
		final BluetoothDevice[] devices = pairedDevices.toArray(new BluetoothDevice[0]);
		String[] items = new String[devices.length];
		for (int i=0;i<devices.length;i++) {
			items[i] = devices[i].getName();
		}

		alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 選択されたデバイスを通知する。そのまま接続開始。
				bluetoothTask.doConnect(devices[which]);
			}
		});
		alertDialogBuilder.setCancelable(false);
		return alertDialogBuilder.create();
	}
	
	
	@SuppressWarnings("deprecation")
	public void errorDialog(String msg) {
		if (this.isFinishing()) return;
		this.errorMessage = msg;
		this.showDialog(ERROR_DIALOG);
	}
	public Dialog createErrorDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Error");
		alertDialogBuilder.setMessage("");
		alertDialogBuilder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		return alertDialogBuilder.create();
	}

	public void showWaitDialog(String msg) {
		if (waitDialog == null) {
			waitDialog = new ProgressDialog(this);
		}
		waitDialog.setMessage(msg);
		waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		waitDialog.show();
	}
	public void hideWaitDialog() {
		waitDialog.dismiss();
	}

}
