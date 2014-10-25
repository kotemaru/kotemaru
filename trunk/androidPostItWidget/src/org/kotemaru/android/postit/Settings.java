package org.kotemaru.android.postit;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

public class Settings {
	public enum Key {
		IS_INITIALIZED, BACKGROUND_URI_SET, CTRL_ACTION
	}

	public enum Value {
		SINGLE_TAP, DOUBLE_TAP
	}

	private SharedPreferences mSharedPref;
	private Set<String> mBackgroundUriSet;
	private String mCtrlAction;
	private boolean mIsDoubleTapCtrlAction;

	public Settings(Context context) {
		mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public Settings autoUpdate() {
		mSharedPref.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences paramSharedPreferences, String paramString) {
				Log.d("Settings", "onSharedPreferenceChanged:" + paramString);
				mSharedPref = paramSharedPreferences;
				load();
			}
		});
		return this;
	}

	public boolean fiastBootInitialize() {
		boolean isInitialized = mSharedPref.getBoolean(Key.IS_INITIALIZED.name(), false);
		if (isInitialized) return false;
		setBackgroundUri("12:00", "assets:///default_bg_1.jpg");
		setBackgroundUri("00:00", "assets:///default_bg_2.jpg");
		save();

		SharedPreferences.Editor editor = mSharedPref.edit();
		editor.putBoolean(Key.IS_INITIALIZED.name(), true);
		editor.apply();
		return true;
	}

	public Settings load() {
		setBackgroundUriSet(getPrefSet(Key.BACKGROUND_URI_SET, null));
		setCtrlAction(getPrefValue(Key.CTRL_ACTION, Value.SINGLE_TAP));
		return this;
	}
	public void save() {
		SharedPreferences.Editor editor = mSharedPref.edit();
		editor.putStringSet(Key.BACKGROUND_URI_SET.name(), getBackgroundUriSet());
		editor.putString(Key.CTRL_ACTION.name(), getCtrlAction());
		editor.apply();
	}

	private String getPrefValue(Key key, Value defo) {
		String value = mSharedPref.getString(key.name(), null);
		if (value == null) {
			if (defo == null) return null;
			return defo.name();
		}
		return value;
	}
	private Set<String> getPrefSet(Key key, Set<String> defo) {
		Set<String> value = mSharedPref.getStringSet(key.name(), null);
		if (value == null) {
			if (defo == null) return null;
			return defo;
		}
		return value;
	}

	public void setBackgroundUri(String time, String uri) {
		if (mBackgroundUriSet == null) {
			mBackgroundUriSet = new HashSet<String>();
		}
		Iterator<String> ite = mBackgroundUriSet.iterator();
		while (ite.hasNext()) {
			String item = ite.next();
			if (item.startsWith(time)) ite.remove();
		}
		mBackgroundUriSet.add(time + "|" + uri);
	}
	public String getBackgroundUri(long time) {
		if (mBackgroundUriSet == null || mBackgroundUriSet.isEmpty()) return null;

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		boolean isDayTime = (6 <= hour && hour <= 17);
		for (String uri : mBackgroundUriSet) {
			if (isDayTime) {
				if (uri.startsWith("12:00|")) return uri.substring(6);
			} else {
				if (uri.startsWith("00:00|")) return uri.substring(6);
			}
		}
		for (String uri : mBackgroundUriSet) {
			return uri.substring(6);
		}
		return null;
	}

	// -------------------------------------------------------

	public boolean isDoubleTapCtrlAction() {
		return mIsDoubleTapCtrlAction;
	}
	public String getCtrlAction() {
		return mCtrlAction;
	}
	public void setCtrlAction(String ctrlAction) {
		this.mCtrlAction = ctrlAction;
		mIsDoubleTapCtrlAction = Value.DOUBLE_TAP.name().equals(ctrlAction);
	}

	public Set<String> getBackgroundUriSet() {
		return mBackgroundUriSet;
	}

	public void setBackgroundUriSet(Set<String> pBackgroundUriSet) {
		this.mBackgroundUriSet = pBackgroundUriSet;
	}

}
