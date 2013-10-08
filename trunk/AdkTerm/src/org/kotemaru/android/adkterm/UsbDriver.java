package org.kotemaru.android.adkterm;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.kotemaru.android.logicasync.annotation.Logic;
import org.kotemaru.android.logicasync.annotation.Task;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;

@Logic
public class UsbDriver implements UsbReceiver.Driver {	
	private static final String TAG = "UsbDriver";
	
	private static final boolean TEST_MODE = true;
	
	private MainActivity activity;
	private UsbManager usbManager;
	
	private ParcelFileDescriptor fileDescriptor;
	private FileInputStream usbIn;
	private FileOutputStream usbOut;

	public UsbDriverAsync async = new UsbDriverAsync(this);

	public UsbDriver(MainActivity activity) {
		this.activity = activity;
		this.usbManager =  (UsbManager) activity.getSystemService(Context.USB_SERVICE);
	}
	
	@Override
	public void openAccessory(UsbAccessory accessory) {
		fileDescriptor = usbManager.openAccessory(accessory);

		if(fileDescriptor != null){
			FileDescriptor fd = fileDescriptor.getFileDescriptor();
			usbIn = new FileInputStream(fd);
			usbOut = new FileOutputStream(fd);
			activity.writeDisplay("<openAccessory>\n");
			async.doTransterUsbIn();
		}else{
			Log.d(TAG, "accessory open fail");
		}
	}

	@Override
	public void closeAccessory(UsbAccessory accessory) {
		try{
			if(fileDescriptor != null){
				fileDescriptor.close();
			}
			activity.writeDisplay("\n<closeAccessory>\n");
		}catch(IOException e){
		}finally{
			fileDescriptor = null;
		}
	}
	
	@Task
	public void doOnKey(char ch) {
		try {
			if (TEST_MODE) {
				async.doWriteDisplay(Character.toString(ch));
			} else {
				byte[] buff = Character.toString(ch).getBytes("UTF8");
				usbOut.write(buff);
				usbOut.flush();
			}
		} catch (Exception e) {
			async.doError(e);
		}
	}

	
	@Task("parallel")
	public void doTransterUsbIn() {
		try {
			byte[] buff = new byte[512];
			int len = usbIn.read(buff);
			String text = new String(buff,0,len,"UTF8");
			async.doWriteDisplay(text);
			async.doTransterUsbIn(); // Loop.
		} catch (Exception e) {
			async.doError(e);
		}
	}
	
	@Task("UI")
	public void doWriteDisplay(String text) {
		activity.writeDisplay(text);
	}
	@Task("UI")
	public void doError(Exception e) {
		activity.errorDialog(e.getMessage());
	}


}
