package org.kotemaru.android.adkterm;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class MainActivity extends Activity {

	private static final String ACTION_USB_PERMISSION = "org.kotemaru.android.adkterm.acion.USB_PERMISSION";

	public static final char BS = (char) 8;

	private UsbDriver usbDriver;
	private UsbReceiver usbReceiver;

	private ConsoleView console;
	private ConsoleLog consoleLog;
	private US101KeyboardView keyboardView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		usbDriver = new UsbDriver(this);
		usbReceiver = UsbReceiver.init(this, ACTION_USB_PERMISSION, usbDriver);

		console = (ConsoleView) findViewById(R.id.console);
		consoleLog = new ConsoleLog(300);
		console.setConsoleLog(consoleLog);
		//console.setMaxLines(300);
		//console = new ConsoleView(this);
		//consoleScroll.addView(console);

		LinearLayout layout = (LinearLayout) findViewById(R.id.top_layout);
		keyboardView = new US101KeyboardView(this);
		keyboardView.setOnKeyboardActionListener(new US101KeyboardView.OnKeyboardListener(keyboardView) {
			public void onChar(char ch) {
				usbDriver.async.doOnKey(ch);
			}
		});
		layout.addView(keyboardView);
		console.append("AdkTerm ver-0.1\n");
	}
	
	private static final int MENU_ID_SOFTKB = (Menu.FIRST + 1);
	private static final int MENU_ID_PORTRAIT = (Menu.FIRST + 2);

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item1 = menu.add(Menu.NONE, MENU_ID_SOFTKB, Menu.NONE, "Soft Keybord");
		MenuItem item2 = menu.add(Menu.NONE, MENU_ID_PORTRAIT, Menu.NONE, "Portrait");

		item1.setCheckable(true);
		item2.setCheckable(true);
		item2.setChecked(getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		case MENU_ID_SOFTKB:
			item.setChecked(!item.isChecked());
			return true;
		case MENU_ID_PORTRAIT:
			item.setChecked(!item.isChecked());
			if (item.isChecked()) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
			return true;
		}
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
		usbDriver.async.close();
	}

	public void writeDisplay(String text) {
		console.append(text);
	}

	public void errorDialog(String message) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Error!");
		dialog.setMessage(message);

		dialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		/*
		dialog.setNeutralButton("Disconnect", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				usbReceiver.close();
				console.setText("Disconnect.\n");
				dialog.dismiss();
			}
		});*/
		dialog.setNegativeButton("Restart", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Activity self = MainActivity.this;
				Intent intent = self.getIntent();
				self.finish();
				self.startActivity(intent);
			}
		});
		dialog.show();
	}

}
