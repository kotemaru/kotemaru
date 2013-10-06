package org.kotemaru.android.usbsample;

import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private static final String ACTION_USB_PERMISSION = "org.kotemaru.android.usbsample.USB_PERMISSION";

	private UsbDriver usbDriver;
	private UsbReceiver usbReceiver;
	private EditText editText1;
	private EditText editText2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		usbDriver = new UsbDriver(this);
		usbReceiver = UsbReceiver.init(this, ACTION_USB_PERMISSION, usbDriver);
		
		editText1 = (EditText) findViewById(R.id.editText1);
		editText2 = (EditText) findViewById(R.id.editText2);
		
		Button sendBtn = (Button) findViewById(R.id.sendBtn);
		sendBtn.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v) {
				String msg = editText1.getText().toString();
				try {
					usbDriver.send(msg);
					String rmsg = usbDriver.receive();
					editText2.setText(rmsg);
				} catch (IOException e) {
					errorDialog(e.getMessage());
				}
			}
		});
		Button resetBtn = (Button) findViewById(R.id.resetBtn);
		resetBtn.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v) {
				restart();
			}
		});
	}
	

	@Override
	public void onResume() {
		super.onResume();
		usbReceiver.resume();
	}

	@Override
	public void onPause() {
		super.onPause();
		usbReceiver.close();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		usbReceiver.destroy();
	}
	
	protected void restart() {
		Intent intent = this.getIntent();
		this.finish();
		this.startActivity(intent);
	}
	
	public void errorDialog(String message) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Error!");
		dialog.setMessage(message);
		dialog.show();
	}

}
