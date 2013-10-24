package org.kotemaru.android.reshelper.sample;

import org.kotemaru.android.reshelper.annotation.PreferenceReader;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

@PreferenceReader(xml="res/xml/adkterm_pref.xml")
public class Config extends ConfigPreferenceReader {
	public static void init(Context context) {
		super.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
}
