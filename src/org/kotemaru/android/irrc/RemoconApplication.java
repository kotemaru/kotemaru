/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2014- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.android.irrc;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Application;

/**
 * 汎用のオブジェクト保管庫を持つアプリケーション。
 * - Activityのライフサイクルに従わないオブジェクトを保存する。
 * - アプリごと落とされた場合は知らない。
 * 
 * @author kotemaru@kotemaru.org
 */
public class RemoconApplication extends Application implements RemoconConst {

	private Map<String, Object> saveObjects = new HashMap<String, Object>();
	private IrrcUsbDriver irrcUsbDriver;

	public void putObject(String key, Object val) {
		saveObjects.put(key, val);
	}

	public Object getObject(String key) {
		return saveObjects.get(key);
	}

	public Object removeObject(String key) {
		return saveObjects.remove(key);
	}

	public IrrcUsbDriver getIrrcUsbDriver(Activity activity) {
		if (irrcUsbDriver == null) {
			irrcUsbDriver = IrrcUsbDriver.init(activity, ACTION_USB_PERMISSION);
		}
		return irrcUsbDriver;
	}

}
