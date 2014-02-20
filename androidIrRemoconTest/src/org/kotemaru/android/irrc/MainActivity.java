package org.kotemaru.android.irrc;

import org.kotemaru.android.irrc.IrrcUsbDriver.IrrcResponseListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 「赤外線リモコンキット」のテストコード。
 * - 注：デバイスのテストなのでActivityのライフサイクル等については無視してます。
 * @author kotemaru@kotemaru.org
 */
public class MainActivity extends Activity {
	private static final String ACTION_USB_PERMISSION = "org.kotemaru.android.irrc.USB_PERMISSION";

	private IrrcUsbDriver irrcUsbDriver;
	private UsbReceiver usbReceiver;
	private byte[] irData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		irrcUsbDriver = new IrrcUsbDriver(this, ACTION_USB_PERMISSION);
		usbReceiver = UsbReceiver.init(this, irrcUsbDriver, ACTION_USB_PERMISSION);

		Button recBtn = (Button) findViewById(R.id.recBtn);
		recBtn.setOnClickListener(new OnClickListener() {
			/**
			 * 赤外線データ受信処理。
			 * @param view
			 */
			@Override
			public void onClick(final View view) {
				if (irrcUsbDriver.isReady() == false) {
					errorDialog("Not ready USB Deivce.");
					return;
				}
				view.setEnabled(false);
				irrcUsbDriver.startReceiveIR(new IrrcResponseListener() {
					@Override
					public void onIrrcResponse(byte[] data) {
						irrcUsbDriver.getReveiveIRData(new IrrcResponseListener() {
							@Override
							public void onIrrcResponse(byte[] data) {
								irData = data;
								irrcUsbDriver.endReceiveIR(null);
								view.setEnabled(true);
							}
						});
					}
				});
			}
		});

		Button sendBtn = (Button) findViewById(R.id.sendBtn);
		sendBtn.setOnClickListener(new OnClickListener() {
			/**
			 * 赤外線データ送信処理。
			 * @param view
			 */
			@Override
			public void onClick(View v) {
				if (irrcUsbDriver.isReady() == false || irData == null) {
					errorDialog("Not ready.");
					return;
				}
				irrcUsbDriver.sendData(irData);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (! irrcUsbDriver.hasDevice()) {
			// TODO: Dialog
			finish();
			return;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		usbReceiver.destroy();
	}

	public void errorDialog(String message) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Error!");
		dialog.setMessage(message);
		dialog.show();
	}

}
