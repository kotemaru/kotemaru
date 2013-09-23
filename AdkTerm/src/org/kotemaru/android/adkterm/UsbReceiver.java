package org.kotemaru.android.adkterm;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class UsbReceiver extends BroadcastReceiver{
	private static final String TAG = "UsbReceiver";

	private Activity activity ;
	private Driver driver;
	private final String action_usb_permission;

	private UsbManager usbManager;
	private UsbAccessory activeAccessory;

	private PendingIntent permissionIntent ;
	private boolean permissionRequestPending = false;
	
	public interface Driver {
		public void openAccessory(UsbAccessory accessory);
		public void closeAccessory(UsbAccessory accessory);
	}
	
	public static UsbReceiver init(Activity activity, String permissionName, Driver driver) {
		UsbReceiver receiver = new UsbReceiver(activity, permissionName, driver);
		
		/* receiver */
		IntentFilter filter = new IntentFilter();
		filter.addAction(permissionName);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		activity.registerReceiver(receiver, filter);
		
		return receiver;
	}

	
    public UsbReceiver(Activity activity, String permissionName, Driver driver) {
		super();
		this.activity = activity;
		this.action_usb_permission = permissionName;
		this.driver = driver;
		
		this.usbManager =  (UsbManager) activity.getSystemService(Context.USB_SERVICE);
		this.permissionIntent = 
			PendingIntent.getBroadcast(activity, 0, new Intent(permissionName), 0);
	}

    public boolean isActive() {
    	return activeAccessory != null;
    }

	@Override
    public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(action_usb_permission.equals(action)){
			open(intent);
		}else if(UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)){
			close(intent);
		}
    }
	
	private synchronized void open(Intent intent) {
		UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY); 
		if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)){
			driver.openAccessory(accessory);
			activeAccessory = accessory;
		}else{
			Log.d(TAG, "permission denied for accessory "+ accessory);
		}
		permissionRequestPending = false;
	}
	

	private synchronized void close(Intent intent) {
		UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY); 
		if(accessory != null && accessory.equals(activeAccessory)){
			close();
		}
	}
	
	public void resume() {
		UsbAccessory[] accessories = usbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if(accessory != null){
			if(usbManager.hasPermission(accessory)){
				driver.openAccessory(accessory);
			}else{
				synchronized (this) {
					if(!permissionRequestPending){
						usbManager.requestPermission(accessory, permissionIntent);
						permissionRequestPending = true;
					}
				}
			}
		}else{
			Log.d(TAG, "accessory is null");
		}
	}

	public synchronized void close() {
		if (activeAccessory != null) {
			driver.closeAccessory(activeAccessory);
			activeAccessory = null;
		}
	}

	public void destroy() {
		activity.unregisterReceiver(this);
	}
	
}
