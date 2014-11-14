package org.kotemaru.android.postit.data;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * アプリの設定。
 * @author kotemaru.org
 */
public class Settings {
	/** SharedPreferences のキー */
	public enum Key {
		/** アプリ初期化フラグ。インストール直後の処理用。 */
		IS_INITIALIZED,
		/** 背景画像のURIのSet。書式は "12:00|contents://～" のように 時刻とURIを'|'で接続。 */
		BACKGROUND_URI_SET,
		/** トレイ表示を行う操作。"SINGLE_TAP" or "DOUBLE_TAP" */
		CTRL_ACTION
	}

	/** SharedPreferences の値。 */
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

	/**
	 * 他のContextが設定を更新した場合に反映させるリスナの設定。
	 * <li>なぜか動かないので使って無い。
	 * @return this
	 */
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

	/**
	 * 設定の初期化。インストール直後の一回だけ実行される。
	 * <li>各種デフォルト値を設定。
	 * @return true=実行した。
	 */
	public boolean fiastBootInitialize() {
		boolean isInitialized = mSharedPref.getBoolean(Key.IS_INITIALIZED.name(), false);
		if (isInitialized) return false;
		//setBackgroundUri("12:00", "assets:///default_bg_1.jpg");
		//setBackgroundUri("00:00", "assets:///default_bg_2.jpg");
		save();

		SharedPreferences.Editor editor = mSharedPref.edit();
		editor.putBoolean(Key.IS_INITIALIZED.name(), true);
		editor.apply();
		return true;
	}

	/**
	 * SharedPreferencesからフィールドにデータを読み込む。
	 * @return this
	 */
	public Settings load() {
		setBackgroundUriSet(getPrefSet(Key.BACKGROUND_URI_SET, null));
		setCtrlAction(getPrefValue(Key.CTRL_ACTION, Value.SINGLE_TAP));
		return this;
	}
	/**
	 * フィールドからSharedPreferencesにデータを書き込む。
	 */
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

	/**
	 * 背景画像設定。現時点では時刻は 昼/夜 の２パターンのみ。
	 * @param time  "12:00"=昼間 or "00:00"=夜間
	 * @param uri   "content://～" or "assets://～"
	 */
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
	
	/**
	 * 背景画像取得。06:00～18:00 が昼画像、その他夜画像。
	 * @param time  現在時刻。
	 * @return 画像URI。null有り。
	 */
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
	// setter/getter
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
