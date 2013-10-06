package org.kotemaru.android.usbsample;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class UsbDriver implements UsbReceiver.Driver {
	private static final String TAG = "UsbDriver";

	private UsbManager usbManager;

	private ParcelFileDescriptor fileDescriptor;
	private FileInputStream usbIn;
	private FileOutputStream usbOut;

	public UsbDriver(MainActivity activity) {
		this.usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
	}

	@Override
	public void openAccessory(UsbAccessory accessory) {
		fileDescriptor = usbManager.openAccessory(accessory);

		if (fileDescriptor != null) {
			FileDescriptor fd = fileDescriptor.getFileDescriptor();
			usbIn = new FileInputStream(fd);
			usbOut = new FileOutputStream(fd);
		} else {
			Log.d(TAG, "accessory open fail");
		}
	}

	@Override
	public void closeAccessory(UsbAccessory accessory) {
		try {
			if (fileDescriptor != null) {
				fileDescriptor.close();
			}
		} catch (IOException e) {
			// ignore.
		} finally {
			fileDescriptor = null;
		}
	}

	
	public void send(String msg) throws IOException {
		usbOut.write(msg.getBytes("UTF-8"));
		usbOut.flush();
	}
	public String receive() throws IOException {
		byte[] buff = new byte[1024];
		int len = usbIn.read(buff);
		return new String(buff,0,len,"UTF-8");
	}

}
