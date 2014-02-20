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

	private MainActivity activity;
	private Driver driver;
	private String permissionName;

	public interface Driver {
		public String onAttach(UsbDevice device);
		public String onDetach(UsbDevice device);
		public String onStart(UsbDevice device);
	}

	public static UsbReceiver init(MainActivity activity, Driver driver, String permissionName) {
		UsbReceiver receiver = new UsbReceiver(activity, driver, permissionName);

		/* receiver */
		IntentFilter filter = new IntentFilter();
		
		filter.addAction(permissionName);  // USBデバイスの利用許可の通知を受ける。
		//filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		activity.registerReceiver(receiver, filter);

		return receiver;
	}

	public UsbReceiver(MainActivity activity, Driver driver, String permissionName) {
		super();
		this.permissionName = permissionName;
		this.activity = activity;
		this.driver = driver;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(TAG,"onReceive:"+action);
		UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		//if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
		//	String errorMeg = driver.onAttach(device);
		//	if (errorMeg != null) activity.errorDialog(errorMeg);
		//} else 
		if (permissionName.equals(action)) {
			String errorMeg = driver.onStart(device);
			if (errorMeg != null) activity.errorDialog(errorMeg);
		} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
			if (driver.onDetach(device) == null) {
				//activity.finish();
				activity.moveTaskToBack(true);
			}
		}
	}

	public void destroy() {
		activity.unregisterReceiver(this);
	}

}
