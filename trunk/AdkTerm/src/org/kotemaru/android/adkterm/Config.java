package org.kotemaru.android.adkterm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Config  {
	
	public final static String K_KEYBORD = "keybord";
	public final static String K_ORIENT = "orientation";
	public final static String K_FONTSIZE = "fontsize";
	public final static String K_LOGSIZE = "logsize";
	public final static String V_LANDSCAPE = "Landscape";
	public final static String V_PORTRAIT = "Portrait";
	
	private static SharedPreferences sharedPrefs;
	
	public static void init(Context context) {
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static Boolean getKeybord() {
		return sharedPrefs.getBoolean(K_KEYBORD, true);
	}
	public static String getOrientation() {
		return sharedPrefs.getString(K_ORIENT, V_LANDSCAPE);
	}
	public static int getFontsize() {
		return Integer.parseInt(sharedPrefs.getString(K_FONTSIZE, "16"));
	}
	public static int getLogsize() {
		return Integer.parseInt(sharedPrefs.getString(K_LOGSIZE, "300"));
	}
	
}