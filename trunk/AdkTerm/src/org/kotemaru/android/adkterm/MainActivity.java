package org.kotemaru.android.adkterm;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class MainActivity extends Activity {

	private static final String ACTION_USB_PERMISSION = "org.kotemaru.android.adkterm.acion.USB_PERMISSION";

	public static final char BS = (char) 8;

	//private UsbDriver usbDriver;
	private TelnetDriver usbDriver;
	private UsbReceiver usbReceiver;

	private ConsoleView console;
	private ConsoleLog consoleLog;
	private US101KeyboardView keyboardView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//usbDriver = new UsbDriver(this);
		usbDriver = new TelnetDriver(this);
		usbReceiver = UsbReceiver.init(this, ACTION_USB_PERMISSION, usbDriver);

		console = (ConsoleView) findViewById(R.id.console);
		consoleLog = new ConsoleLog(300);
		console.setConsoleLog(consoleLog);
		console.setActivity(this);
		//console.setMaxLines(300);
		//console = new ConsoleView(this);
		//consoleScroll.addView(console);
		console.setFocusable(true);
		console.setOnKeyListener(new OnKeyListener(){
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					usbDriver.async.doOnKey((char)event.getUnicodeChar());
				}
				return true;
			}
		});
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.top_layout);
		keyboardView = new US101KeyboardView(this);
		keyboardView.setOnKeyboardActionListener(new US101KeyboardView.OnKeyboardListener(keyboardView) {
			public void onChar(char ch) {
				usbDriver.async.doOnKey(ch);
			}
		});
		layout.addView(keyboardView);
		//console.append("AdkTerm ver-0.1\n");

	}
	
	private static final int MENU_ID_PREF = (Menu.FIRST + 1);

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ID_PREF, Menu.NONE, "Preference");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int menuId = item.getItemId();
		if (menuId == MENU_ID_PREF) {
			Intent intent = new Intent(this, PrefActivity.class);
			startActivityForResult(intent, MENU_ID_PREF);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MENU_ID_PREF) {
			if (resultCode == RESULT_OK) {
				showDialog("Preference changed.", 
						"Restart is necessary for reflection of the new preference.");
			}
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
		showDialog("Error!", message);
	}
	public void showDialog(String title, String message) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(title);
		dialog.setMessage(message);

		dialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
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

	public void input(String text) {
		usbDriver.async.send(text);
	}

}
