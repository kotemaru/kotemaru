package org.kotemaru.android.adkterm;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.method.BaseKeyListener;
import android.text.method.KeyListener;
import android.text.method.MetaKeyKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String ACTION_USB_PERMISSION = "org.kotemaru.android.adkterm.acion.USB_PERMISSION";

	public static final char BS = (char) 8;

	private UsbDriver usbDriver;
	private UsbReceiver usbReceiver;

	private ScrollView consoleScroll;
	private TextView console;
	private US101KeyboardView keyboardView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		usbDriver = new UsbDriver(this);
		usbReceiver = UsbReceiver.init(this, ACTION_USB_PERMISSION, usbDriver);

		consoleScroll = (ScrollView) findViewById(R.id.consoleScroll);
		console = (TextView) findViewById(R.id.console);
		console.setMaxLines(300);

		LinearLayout layout = (LinearLayout) findViewById(R.id.top_layout);
		keyboardView = new US101KeyboardView(this);
		keyboardView.setOnKeyboardActionListener(new US101KeyboardView.OnKeyboardListener(keyboardView) {
			public void onChar(char ch) {
				usbDriver.async.doOnKey(ch);
			}
		});
		layout.addView(keyboardView);
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
		if (text.charAt(0) == BS) {
			CharSequence edit = console.getText();
			if (edit != null && edit.length() > 0) {
				console.setText(edit.subSequence(0, edit.length() - 1));
			}
		} else {
			console.append(text);
		}
		consoleScroll.scrollTo(0, console.getBottom());
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
		dialog.setNegativeButton("Disconnect", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				usbReceiver.close();
				console.setText("Disconnect.\n");
				dialog.dismiss();
			}
		});
		dialog.show();
	}

}
