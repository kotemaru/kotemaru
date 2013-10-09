package org.kotemaru.android.adkterm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.kotemaru.android.logicasync.annotation.Logic;
import org.kotemaru.android.logicasync.annotation.Task;

import android.hardware.usb.UsbAccessory;
import org.apache.commons.net.telnet.TelnetClient;

@Logic
public class TelnetDriver implements UsbReceiver.Driver {
	private static final String TAG = "TelnetDriver";

	
	private static final String host = "192.168.0.2";
	private static final int port = 23;
	
	private MainActivity activity;
	
	private TelnetClient fileDescriptor;
	protected InputStream usbIn;
	protected OutputStream usbOut;

	public TelnetDriverAsync async = new TelnetDriverAsync(this);

	public TelnetDriver(MainActivity activity) {
		this.activity = activity;
		async.connect();
	}
	
	@Override
	public void openAccessory(UsbAccessory accessory) {
		try{
			fileDescriptor = new TelnetClient();
			fileDescriptor.connect(host,port);
			usbIn = fileDescriptor.getInputStream();
			usbOut = fileDescriptor.getOutputStream();
			
			async.doTransterUsbIn();
		}catch(IOException e){
			activity.errorDialog(e.toString());
		}finally{
			fileDescriptor = null;
		}
	}

	@Override
	public void closeAccessory(UsbAccessory accessory) {
		try{
			if(fileDescriptor != null){
				fileDescriptor.disconnect();
			}
		}catch(IOException e){
		}finally{
			fileDescriptor = null;
		}
	}
	
	
	@Task
	public void connect() {
		openAccessory(null);
	}

	
	@Task
	public void doOnKey(char ch) {
		try {
			byte[] buff = Character.toString(ch).getBytes("UTF8");
			usbOut.write(buff);
			usbOut.flush();
		} catch (Exception e) {
			async.doError(e);
		}
	}
	
	@Task
	public void send(String text) {
		try {
			byte[] buff = text.getBytes("UTF8");
			usbOut.write(buff);
			usbOut.flush();
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
