/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2014- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.android.irrc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * 
 * @author kotemaru@kotemaru.org
 */
public class RemoconResource {
	private static final String TAG = "RemoconResource";
	private static final String REMOCON_URL_BASE = "file:///android_asset/remocon/";

	public static List<String> getRemoconList(Context context) {
		AssetManager assetManager = context.getResources().getAssets();
		List<String> list = new ArrayList<String>(5);
		try {
			String[] files = assetManager.list("remocon");
			for (int i = 0; i < files.length; i++) {
				if (files[i].endsWith(".html")) {
					list.add(REMOCON_URL_BASE + files[i]);
				}
			}
			return list;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

}
