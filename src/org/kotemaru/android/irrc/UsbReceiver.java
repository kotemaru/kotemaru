package org.kotemaru.android.irrc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class UsbReceiver extends BroadcastReceiver {
	private static final String TAG = "UsbReceiver";
	private final static String ACTION_USB_PERMISSION = RemoconConst.ACTION_USB_PERMISSION;

	private UsbReceiverActivity activity;
	private Driver driver;

	public interface UsbReceiverActivity {
		public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter);

		public void unregisterReceiver(BroadcastReceiver receiver);

		public void errorDialog(String message);

		public void finish();
	}

	public interface Driver {
		public String onAttach(UsbDevice device);

		public String onDetach(UsbDevice device);

		public String onStart(UsbDevice device);
	}

	public static UsbReceiver init(UsbReceiverActivity activity, Driver driver) {
		UsbReceiver receiver = new UsbReceiver(activity, driver);

		/* receiver */
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_USB_PERMISSION);  // USBデバイスの利用許可の通知を受ける。
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		activity.registerReceiver(receiver, filter);

		return receiver;
	}

	public UsbReceiver(UsbReceiverActivity activity, Driver driver) {
		super();
		this.activity = activity;
		this.driver = driver;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(TAG, "onReceive:" + action);
		UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		if (ACTION_USB_PERMISSION.equals(action)) {
			String errorMeg = driver.onStart(device);
			if (errorMeg != null) {
				activity.errorDialog(errorMeg);
			}
		} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
			if (driver.onDetach(device) == null) {
				activity.finish();
			}
		}
	}

	public void destroy() {
		activity.unregisterReceiver(this);
	}

}
